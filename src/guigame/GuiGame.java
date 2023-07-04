package guigame;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.*;
import java.net.URI;

import gameboard.*;
import logic.*;
import config.*;

public class GuiGame extends JFrame {
    private static final String WINDOW_TITLE = "PlayHex";
    private static final double SQRT_3 = 1.7320508075688772;
    private static final int PAINT_TIME_INTERVAL = 100;
    private static final int SINGLE_ANIMATION_TIME = 300;
    private static final double SCALE_STEP = 1.0 / ((double) SINGLE_ANIMATION_TIME / (double) PAINT_TIME_INTERVAL);
    private final int windowHeight = 600;
    private final int windowWidth = 600;
    private int startX;
    private int startY;
    private int fullRadius;

    private static int[] getHexagonVerticesX(Position centerPos, int radius) {
        var verticesX = new int[6];
        verticesX[0] = centerPos.col;
        verticesX[1] = (int) (centerPos.col + radius * SQRT_3 / 2);
        verticesX[2] = (int) (centerPos.col + radius * SQRT_3 / 2);
        verticesX[3] = centerPos.col;
        verticesX[4] = (int) (centerPos.col - radius * SQRT_3 / 2);
        verticesX[5] = (int) (centerPos.col - radius * SQRT_3 / 2);
        return verticesX;
    }

    private static int[] getHexagonVerticesY(Position centerPos, int radius) {
        var verticesY = new int[6];
        verticesY[0] = centerPos.row - radius;
        verticesY[1] = centerPos.row - radius / 2;
        verticesY[2] = centerPos.row + radius / 2;
        verticesY[3] = centerPos.row + radius;
        verticesY[4] = centerPos.row + radius / 2;
        verticesY[5] = centerPos.row - radius / 2;
        return verticesY;
    }

    private class BoardPainter extends JPanel {
        private Position getHexagonCenterPos(int i, int j) {
            return exagonCenterPos[i][j];
        }

        private Position[][] exagonCenterPos;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Background
            g.setColor(new Color(45, 51, 57));
            g.fillRect(0, 0, GuiGame.this.windowWidth, GuiGame.this.windowHeight);

            // Board
            var row = logic.getRow();
            var col = logic.getCol();
            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < col; ++j) {
                    var centerPos = getHexagonCenterPos(i, j);
                    int blankRadius = (int) (fullRadius * 0.95);
                    int[] verticesX = getHexagonVerticesX(centerPos, blankRadius);
                    int[] verticesY = getHexagonVerticesY(centerPos, blankRadius);
                    g.setColor(new Color(90, 101, 112));
                    g.fillPolygon(verticesX, verticesY, 6);
                }
            }

            final var gameState = gameStateManager.getGameState();

            // Chesses
            Position animatingPos = null;
            if (gameStateManager.getGameState() == GameState.ANIMATING) {
                var record = animationRecorder.getTopAnimationRecord();
                if (record != null) {
                    animatingPos = new Position(record.pos.row, record.pos.col);
                }
            }
            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < col; ++j) {
                    var centerPos = getHexagonCenterPos(i, j);
                    int chessRadius = (int) (fullRadius * 0.75);
                    var type = logic.getChessType(new Position(i, j));
                    Color fillColor;
                    Color strokeColor;

                    if (gameState == GameState.ANIMATING) {
                        if (animationRecorder.isAnimating(i, j)) {
                            var scale = animationRecorder.getAnimationScale(i, j);
                            chessRadius = (int) (chessRadius * scale);
                            type = animationRecorder.getChessType(i, j);
                            if (animatingPos != null
                                    && i == animatingPos.row && j == animatingPos.col) {
                                var record = animationRecorder.getTopAnimationRecord();
                                if (record != null) {
                                    if (record.isAppear) {
                                        scale += SCALE_STEP;
                                        if (scale > 1.0 - SCALE_STEP / 2.0) {
                                            scale = 1.0;
                                            animationRecorder.popAnimationRecord();
                                        }
                                    } else {
                                        scale -= SCALE_STEP;
                                        if (scale < SCALE_STEP / 2.0) {
                                            scale = 0.0;
                                            animationRecorder.popAnimationRecord();
                                        }
                                    }
                                    animationRecorder.setAnimationScale(i, j, scale);
                                }
                            }
                        }
                    }

                    if (type == ChessType.RED) {
                        fillColor = new Color(224, 70, 69);
                        strokeColor = new Color(179, 57, 57);
                    } else if (type == ChessType.BLUE) {
                        fillColor = new Color(40, 140, 186);
                        strokeColor = new Color(33, 118, 157);
                    } else {
                        continue;
                    }
                    int[] verticesX = getHexagonVerticesX(centerPos, chessRadius);
                    int[] verticesY = getHexagonVerticesY(centerPos, chessRadius);
                    g.setColor(fillColor);
                    g.fillPolygon(verticesX, verticesY, 6);
                    g.setColor(strokeColor);
                    ((Graphics2D) g).setStroke(new BasicStroke(4));
                    g.drawPolygon(verticesX, verticesY, 6);
                }
            }

            final int highLightRadius = (int) (fullRadius * 0.95);
            final int cursorRadius = highLightRadius;
            final int highLightStroke = 2;
            final int cursorStroke = 2;
            if (gameState == GameState.CHOOSING_DESTINATION) {
                // Draw chosen chess
                var pos = chosenPos;
                var centerPos = getHexagonCenterPos(pos.row, pos.col);
                g.setColor(Color.WHITE);
                ((Graphics2D) g).setStroke(new BasicStroke(highLightStroke));
                g.drawPolygon(getHexagonVerticesX(centerPos, highLightRadius),
                        getHexagonVerticesY(centerPos, highLightRadius),
                        6);

                // Draw possible destinations
                var neibors = logic.getNeighbors(pos);
                var twoStepNeibors = logic.getTwoStepNeibors(pos);
                for (var neighbor : neibors) {
                    if (logic.getChessType(neighbor) == ChessType.EMPTY) {
                        centerPos = getHexagonCenterPos(neighbor.row, neighbor.col);
                        g.setColor(new Color(102, 179, 44));
                        ((Graphics2D) g).setStroke(new BasicStroke(highLightStroke));
                        g.drawPolygon(getHexagonVerticesX(centerPos, highLightRadius),
                                getHexagonVerticesY(centerPos, highLightRadius),
                                6);
                    }
                }
                for (var neighbor : twoStepNeibors) {
                    if (logic.getChessType(neighbor) == ChessType.EMPTY) {
                        centerPos = getHexagonCenterPos(neighbor.row, neighbor.col);
                        g.setColor(new Color(248, 171, 33));
                        ((Graphics2D) g).setStroke(new BasicStroke(highLightStroke));
                        g.drawPolygon(getHexagonVerticesX(centerPos, highLightRadius),
                                getHexagonVerticesY(centerPos, highLightRadius),
                                6);
                    }
                }
            }

            if (gameState == GameState.CHOOSING_CHESS || gameState == GameState.CHOOSING_DESTINATION) {
                // Draw cursor
                var pos = cursorPos;
                var centerPos = getHexagonCenterPos(pos.row, pos.col);
                if (logic.getCurrentPlayer() == ChessType.RED) {
                    g.setColor(new Color(255, 151, 153));
                } else {
                    g.setColor(new Color(154, 205, 255));
                }
                ((Graphics2D) g).setStroke(new BasicStroke(cursorStroke));
                g.drawPolygon(getHexagonVerticesX(centerPos, cursorRadius),
                        getHexagonVerticesY(centerPos, cursorRadius),
                        6);
            }

            if (gameState == GameState.ANIMATING) {
                if (animationRecorder.isAnimationFinished()) {
                    gameStateManager.animationFinished();
                }
            }
        }

        public BoardPainter() {
            super();
            var row = logic.getRow();
            var col = logic.getCol();
            exagonCenterPos = new Position[row][col];
            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < col; ++j) {
                    if (i % 2 == 0) {
                        int centerX = startX + (int) ((j + 0.5) * SQRT_3 * fullRadius);
                        int centerY = startY + ((i * 3 + 2) * fullRadius) / 2;
                        exagonCenterPos[i][j] = new Position(centerY, centerX);
                    } else {
                        int centerX = startX + (int) ((j + 1) * SQRT_3 * fullRadius);
                        int centerY = startY + ((i * 3 + 2) * fullRadius) / 2;
                        exagonCenterPos[i][j] = new Position(centerY, centerX);
                    }
                }
            }
        }
    }

    private class KeyboardControl extends KeyAdapter {
        // Key pressed
        @Override
        public void keyPressed(java.awt.event.KeyEvent e) {
            final var gameState = gameStateManager.getGameState();
            if (gameState == GameState.GAME_OVER || gameState == GameState.ANIMATING) {
                return;
            }

            var row = logic.getRow();
            var col = logic.getCol();
            boolean repaintNow = false;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (gameState == GameState.CHOOSING_CHESS || gameState == GameState.CHOOSING_DESTINATION) {
                        if (cursorPos.row > 0) {
                            cursorPos.row -= 1;
                            repaintNow = true;
                        }
                    }
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    if (gameState == GameState.CHOOSING_CHESS || gameState == GameState.CHOOSING_DESTINATION) {
                        if (cursorPos.col > 0) {
                            cursorPos.col -= 1;
                            repaintNow = true;
                        }
                    }
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (gameState == GameState.CHOOSING_CHESS || gameState == GameState.CHOOSING_DESTINATION) {
                        if (cursorPos.row < row - 1) {
                            cursorPos.row += 1;
                            repaintNow = true;
                        }
                    }
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    if (gameState == GameState.CHOOSING_CHESS || gameState == GameState.CHOOSING_DESTINATION) {
                        if (cursorPos.col < col - 1) {
                            cursorPos.col += 1;
                            repaintNow = true;
                        }
                    }
                    break;
                case KeyEvent.VK_Q:
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_NUMPAD0:
                    if (gameState == GameState.CHOOSING_DESTINATION) {
                        gameStateManager.cancelChoose();
                    }
                    cursorPos.row = chosenPos.row;
                    cursorPos.col = chosenPos.col;
                    break;
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    if (gameState == GameState.CHOOSING_CHESS) {
                        var pos = cursorPos;
                        if (logic.canMove(pos)) {
                            chosenPos.row = pos.row;
                            chosenPos.col = pos.col;
                            gameStateManager.choseChess();
                        }
                    } else if (gameState == GameState.CHOOSING_DESTINATION) {
                        var pos = cursorPos;
                        if (logic.getChessType(pos) == ChessType.EMPTY) {
                            if (logic.moveChess(chosenPos, pos)) {
                                gameStateManager.choseDestination();
                            }
                        } else if (logic.getChessType(pos) == logic.getCurrentPlayer()) {
                            gameStateManager.cancelChoose();
                            chosenPos.row = pos.row;
                            chosenPos.col = pos.col;
                            gameStateManager.choseChess();
                        }
                    }
                    break;
            }
            if (repaintNow) {
                GuiGame.this.repaint();
            }
        }
    }

    public GuiGame(String[] args) {
        super(WINDOW_TITLE);

        this.logic = new Logic();
        /*
         * this.logic = new Logic(5, 5, Set.of(new Position(0, 0), new Position(4, 4)),
         * Set.of(new Position(4, 0), new Position(0, 4)));
         */
        final int row = logic.getRow();
        final int col = logic.getCol();
        final double substractRatio = 0.9;
        final int cellSize = Math.min(this.windowWidth / col, this.windowHeight / row);
        final int boardWidth = (int) (cellSize * col * substractRatio);
        final int boardHeight = (int) (cellSize * row * substractRatio);
        this.startX = (this.windowWidth - boardWidth) / 2;
        this.startY = (this.windowHeight - boardHeight) / 2;
        this.fullRadius = (int) (boardWidth / (SQRT_3 * (col + 0.5)));
        this.cursorPos.row = row / 2;
        this.cursorPos.col = col / 2;
        this.animationRecorder = new ChessAnimationRecorder(row, col);
        this.logic.addChessChangeListener(this.animationRecorder);

        this.setSize(this.windowWidth, this.windowHeight);
        this.setResizable(false);
        this.setTitle(WINDOW_TITLE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(GuiGame.this, "Are you sure to exit?", "Exit",
                        JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
                GuiGame.this.paintTimer.stop();
                GuiGame.this.dispose();
                System.exit(0);
            }
        });

        var menuBar = new JMenuBar();
        var fileMenu = new JMenu("File");
        fileMenu.addSeparator();
        var exit = new JMenuItem("Exit");
        exit.addActionListener(e -> {
            GuiGame.this.dispatchEvent(new WindowEvent(GuiGame.this, WindowEvent.WINDOW_CLOSING));
        });
        fileMenu.add(exit);
        var helpMenu = new JMenu("Help");
        var localHelp = new JMenuItem("Local Help");
        localHelp.addActionListener(e -> {
            JOptionPane.showMessageDialog(GuiGame.this, Config.HELP_STRING, "Local Help",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(localHelp);
        var onlineHelp = new JMenuItem("Online Help");
        onlineHelp.addActionListener(e -> {
            final String onlineHelpUrl = "https://Timothy-Liuxf.github.io/PlayHex";
            int result = JOptionPane.showConfirmDialog(GuiGame.this,
                    "Will open external website: " + onlineHelpUrl
                            + "\nAre you sure?",
                    "Online Help",
                    JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            boolean browse = false;
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(onlineHelpUrl));
                    browse = true;
                } catch (Exception ex) {
                }
            }
            if (!browse) {
                JOptionPane.showMessageDialog(GuiGame.this,
                        "Failed to open browser.\nPlease browse " + onlineHelpUrl + " for online help.", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        helpMenu.add(onlineHelp);
        var aboutMenu = new JMenu("About");
        var about = new JMenuItem("About...");
        about.addActionListener(e -> {
            JOptionPane.showMessageDialog(GuiGame.this, Config.ABOUT_STRING, "About",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        aboutMenu.add(about);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);
        this.setJMenuBar(menuBar);

        this.addKeyListener(new KeyboardControl());
        var boardPainter = new BoardPainter();
        this.setContentPane(boardPainter);
        this.setVisible(true);
        this.paintTimer = new Timer(PAINT_TIME_INTERVAL, e -> {
            GuiGame.this.repaint();
        });
        this.paintTimer.start();
    }

    private Logic logic;
    private Timer paintTimer;

    private static enum GameState {
        CHOOSING_CHESS,
        CHOOSING_DESTINATION,
        ANIMATING,
        GAME_OVER,
    }

    private class GameStateManager {
        public boolean choseChess() {
            if (this.gameState == GameState.CHOOSING_CHESS) {
                this.gameState = GameState.CHOOSING_DESTINATION;
                return true;
            }
            return false;
        }

        public boolean choseDestination() {
            if (this.gameState == GameState.CHOOSING_DESTINATION) {
                this.gameState = GameState.ANIMATING;
                return true;
            }
            return false;
        }

        public boolean cancelChoose() {
            if (this.gameState == GameState.CHOOSING_DESTINATION) {
                this.gameState = GameState.CHOOSING_CHESS;
                return true;
            }
            return false;
        }

        public boolean animationFinished() {
            if (this.gameState == GameState.ANIMATING) {
                if (logic.isFinished()) {
                    this.gameState = GameState.GAME_OVER;
                    JOptionPane.showMessageDialog(GuiGame.this, "WINNER: " + logic.getWinner(), "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    this.gameState = GameState.CHOOSING_CHESS;
                }
                return true;
            }
            return false;
        }

        public GameState getGameState() {
            return gameState;
        }

        private GameState gameState = GameState.CHOOSING_CHESS;
    }

    private GameStateManager gameStateManager = new GameStateManager();
    private ChessAnimationRecorder animationRecorder;
    private Position cursorPos = new Position(0, 0);
    private Position chosenPos = new Position(0, 0);
}
