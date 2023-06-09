import java.util.HashSet;
import java.util.Set;

public class GameBoard {
    private static final int DEFAULT_ROW = 9;
    private static final int DEFAULT_COL = 9;

    public int getRow() {
        return this.board.length;
    }

    public int getCol() {
        return this.board[0].length;
    }

    public ChessType getChessType(Position pos) throws IndexOutOfBoundsException {
        if (!this.inRange(pos)) {
            throw new IndexOutOfBoundsException("Row or col is out of range.");
        }
        return this.board[pos.row][pos.col];
    }

    public void setChessType(Position pos, ChessType type) throws IndexOutOfBoundsException {
        if (!this.inRange(pos)) {
            throw new IndexOutOfBoundsException("Row or col is out of range.");
        }
        this.board[pos.row][pos.col] = type;
    }

    private final Position[] evenNeibors = {
            new Position(-1, -1), new Position(-1, 0), new Position(0, -1),
            new Position(0, 1), new Position(1, -1), new Position(1, 0)
    };

    private final Position[] oddNeibors = {
            new Position(-1, 0), new Position(-1, 1), new Position(0, -1),
            new Position(0, 1), new Position(1, 0), new Position(1, 1)
    };

    public Set<Position> getNeighbors(Position pos) {
        var neighbors = new HashSet<Position>();
        if (pos.row % 2 == 0) {
            for (var neighbor : this.evenNeibors) {
                var neighborPos = new Position(pos.row + neighbor.row, pos.col + neighbor.col);
                if (this.inRange(neighborPos)) {
                    neighbors.add(neighborPos);
                }
            }
        } else {
            for (var neighbor : this.oddNeibors) {
                var neighborPos = new Position(pos.row + neighbor.row, pos.col + neighbor.col);
                if (this.inRange(neighborPos)) {
                    neighbors.add(neighborPos);
                }
            }
        }
        return neighbors;
    }

    public Set<Position> getTwoStepNeibors(Position pos) {
        var neibors = this.getNeighbors(pos);
        for (var neighbor : this.getNeighbors(pos)) {
            neibors.addAll(this.getNeighbors(neighbor));
        }
        for (var neighbor : this.getNeighbors(pos)) {
            neibors.remove(neighbor);
        }
        neibors.remove(pos);
        return neibors;
    }

    public int getChessCount(ChessType type) {
        int count = 0;
        for (var row : this.board) {
            for (var chess : row) {
                if (chess == type) {
                    ++count;
                }
            }
        }
        return count;
    }

    public boolean inRange(Position pos) {
        return pos.row >= 0 && pos.row < this.getRow() && pos.col >= 0 && pos.col < this.getCol();
    }

    public GameBoard(int row, int col) throws IllegalArgumentException {
        if (row <= 0 || col <= 0) {
            throw new IllegalArgumentException("Row or col is out of range.");
        }

        this.board = new ChessType[row][col];
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                this.board[i][j] = ChessType.EMPTY;
            }
        }
    }

    public GameBoard() {
        this(DEFAULT_ROW, DEFAULT_COL);
    }

    private ChessType[][] board;
}
