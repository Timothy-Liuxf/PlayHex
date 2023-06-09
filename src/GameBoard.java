import java.util.HashSet;
import java.util.Set;

public class GameBoard {
    public final int ROW = 9;
    public final int COL = 9;

    public enum ChessType {
        EMPTY,
        RED,
        BLUE,
    }

    public class Position {
        public int row;
        public int col;

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Position)) {
                return false;
            }
            Position pos = (Position) obj;
            return this.row == pos.row && this.col == pos.col;
        }

        @Override
        public int hashCode() {
            return this.row ^ this.col;
        }

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
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
        return pos.row >= 0 && pos.row < this.board.length && pos.col >= 0 && pos.col < this.board[0].length;
    }

    public GameBoard() {
        this.board = new ChessType[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; ++j) {
                this.board[i][j] = ChessType.EMPTY;
            }
        }

        // TODO: Init chess pos in gaming...
        // this.board[1][1] = ChessType.BLUE;
        // this.board[1][6] = ChessType.RED;
        // this.board[7][1] = ChessType.RED;
        // this.board[7][6] = ChessType.BLUE;
    }

    ChessType[][] board;
}
