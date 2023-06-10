import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Set;

import javax.swing.*;

public class GuiGame extends JFrame {
    private static final String WINDOW_TITLE = "PlayHex";
    private static final double SQRT_3 = 1.7320508075688772;
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

            // Chesses
            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < col; ++j) {
                    var centerPos = getHexagonCenterPos(i, j);
                    int chessRadius = (int) (fullRadius * 0.75);
                    int[] verticesX = getHexagonVerticesX(centerPos, chessRadius);
                    int[] verticesY = getHexagonVerticesY(centerPos, chessRadius);
                    var type = logic.getChessType(new Position(i, j));
                    Color fillColor;
                    Color strokeColor;
                    if (type == ChessType.RED) {
                        fillColor = new Color(224, 70, 69);
                        strokeColor = new Color(179, 57, 57);
                    } else if (type == ChessType.BLUE) {
                        fillColor = new Color(40, 140, 186);
                        strokeColor = new Color(33, 118, 157);
                    } else {
                        continue;
                    }
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
            if (gameState == GameState.GAME_OVER || gameState == GameState.ANIMATING) {
                return;
            }

            var row = logic.getRow();
            var col = logic.getCol();
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    if (cursorPos.row > 0) {
                        cursorPos.row -= 1;
                    }
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    if (cursorPos.col > 0) {
                        cursorPos.col -= 1;
                    }
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    if (cursorPos.row < row - 1) {
                        cursorPos.row += 1;
                    }
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    if (cursorPos.col < col - 1) {
                        cursorPos.col += 1;
                    }
                    break;
                case KeyEvent.VK_Q:
                case KeyEvent.VK_ESCAPE:
                    if (gameState == GameState.CHOOSING_DESTINATION) {
                        gameState = GameState.CHOOSING_CHESS;
                    }
                    cursorPos.row = chosenPos.row;
                    cursorPos.col = chosenPos.col;
                    break;
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    if (gameState == GameState.CHOOSING_CHESS) {
                        var pos = cursorPos;
                        if (logic.getChessType(pos) == logic.getCurrentPlayer()) {
                            chosenPos.row = pos.row;
                            chosenPos.col = pos.col;
                            gameState = GameState.CHOOSING_DESTINATION;
                        }
                    } else if (gameState == GameState.CHOOSING_DESTINATION) {
                        if (logic.moveChess(chosenPos, cursorPos)) {
                            if (logic.isFinished()) {
                                gameState = GameState.GAME_OVER;
                                GuiGame.this.repaint();
                                JOptionPane.showMessageDialog(GuiGame.this, "WINNER: " + logic.getWinner(), "Game over",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                gameState = GameState.CHOOSING_CHESS;
                            }
                        }
                    }
                    break;
            }
            GuiGame.this.repaint();
        }
    }

    public GuiGame(String[] args) {
        super(WINDOW_TITLE);

        this.logic = new Logic();
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

        this.setSize(this.windowWidth, this.windowHeight);
        this.setResizable(false);
        this.setTitle(WINDOW_TITLE);
        this.addKeyListener(new KeyboardControl());
        var boardPainter = new BoardPainter();
        this.setContentPane(boardPainter);
        this.setVisible(true);
    }

    private Logic logic;

    private enum GameState {
        CHOOSING_CHESS,
        CHOOSING_DESTINATION,
        ANIMATING,
        GAME_OVER,
    }

    private GameState gameState = GameState.CHOOSING_CHESS;
    private Position cursorPos = new Position(0, 0);
    private Position chosenPos = new Position(0, 0);
}
