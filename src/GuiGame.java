import java.awt.*;
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

    private class GuiBoard extends JPanel {

        private Position getCenterPos(int i, int j) {
            Position centerPos;
            if (i % 2 == 0) {
                int centerX = startX + (int) ((j + 0.5) * SQRT_3 * fullRadius);
                int centerY = startY + ((i * 3 + 2) * fullRadius) / 2;
                centerPos = new Position(centerY, centerX);
            } else {
                int centerX = startX + (int) ((j + 1) * SQRT_3 * fullRadius);
                int centerY = startY + ((i * 3 + 2) * fullRadius) / 2;
                centerPos = new Position(centerY, centerX);
            }
            return centerPos;
        }

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
                    var centerPos = getCenterPos(i, j);
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
                    var centerPos = getCenterPos(i, j);
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

        this.setSize(this.windowWidth, this.windowHeight);
        this.setResizable(false);
        this.setTitle(WINDOW_TITLE);
        var guiBoard = new GuiBoard();
        this.setContentPane(guiBoard);
        this.setVisible(true);
    }

    private Logic logic;
}
