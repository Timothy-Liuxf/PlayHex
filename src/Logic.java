import java.util.Set;

public class Logic {
    public int getChessCount(ChessType type) {
        return this.gameBoard.getChessCount(type);
    }

    public ChessType getCurrentPlayer() {
        return this.currentPlayer;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public Set<Position> getNeighbors(Position pos) {
        return this.gameBoard.getNeighbors(pos);
    }

    public Set<Position> getTwoStepNeibors(Position pos) {
        return this.gameBoard.getTwoStepNeibors(pos);
    }

    public int getRow() {
        return this.gameBoard.getRow();
    }

    public int getCol() {
        return this.gameBoard.getCol();
    }

    public ChessType getChessType(Position pos) {
        return this.gameBoard.getChessType(pos);
    }

    public ChessType getWinner() {
        if (!this.finished) {
            return ChessType.EMPTY;
        }
        return this.winner;
    }

    public boolean inRange(Position pos) {
        return this.gameBoard.inRange(pos);
    }

    public boolean moveChess(Position from, Position to) {
        if (this.finished) {
            return false;
        }

        var currentPlayer = this.currentPlayer;
        if (this.gameBoard.getChessType(from) != currentPlayer) {
            return false;
        }

        var neibors = this.getNeighbors(from);
        if (neibors.contains(to)) {
            if (this.gameBoard.getChessType(to) != ChessType.EMPTY) {
                return false;
            }
            this.insertNewChess(to);
        } else {
            var twoStepNeibors = this.getTwoStepNeibors(from);
            if (!twoStepNeibors.contains(to)) {
                return false;
            }
            if (this.gameBoard.getChessType(to) != ChessType.EMPTY) {
                return false;
            }
            this.clearChess(from);
            this.insertNewChess(to);
        }
        var toNeibors = this.getNeighbors(to);
        var opposite = currentPlayer == ChessType.RED ? ChessType.BLUE : ChessType.RED;
        for (var neighbor : toNeibors) {
            var chessType = this.gameBoard.getChessType(neighbor);
            if (chessType == opposite) {
                this.flipChess(neighbor);
            }
        }
        this.currentPlayer = opposite;
        var finished = this.checkFinished();
        if (finished) {
            this.finished = true;
            for (int i = 0; i < this.gameBoard.getRow(); ++i) {
                for (int j = 0; j < this.gameBoard.getCol(); ++j) {
                    var pos = new Position(i, j);
                    if (this.gameBoard.getChessType(pos) == ChessType.EMPTY) {
                        this.insertNewChess(pos, currentPlayer);
                    }
                }
            }

            var currentChessCount = this.gameBoard.getChessCount(currentPlayer);
            var oppositeChessCount = this.gameBoard.getChessCount(opposite);
            if (currentChessCount > oppositeChessCount) {
                this.winner = currentPlayer;
            } else if (currentChessCount < oppositeChessCount) {
                this.winner = opposite;
            } else {
                this.winner = ChessType.EMPTY;
            }
        }
        return true;
    }

    private boolean checkFinished() {
        if (this.gameBoard.getChessCount(ChessType.EMPTY) == 0) {
            return true; // Full board
        }

        var currentPlayer = this.currentPlayer;
        if (this.gameBoard.getChessCount(currentPlayer) == 0) {
            return true; // Opposite player has no chess
        }

        for (int i = 0; i < this.gameBoard.getRow(); ++i) {
            for (int j = 0; j < this.gameBoard.getCol(); ++j) {
                var pos = new Position(i, j);
                if (this.gameBoard.getChessType(pos) == currentPlayer) {
                    var neibors = this.getNeighbors(pos);
                    for (var neighbor : neibors) {
                        if (this.gameBoard.getChessType(neighbor) == ChessType.EMPTY) {
                            return false;
                        }
                    }
                    var twoStepNeibors = this.getTwoStepNeibors(pos);
                    for (var neighbor : twoStepNeibors) {
                        if (this.gameBoard.getChessType(neighbor) == ChessType.EMPTY) {
                            return false;
                        }
                    }
                }
            }
        }
        return true; // No valid move
    }

    private void insertNewChess(Position pos) throws IllegalArgumentException {
        if (this.gameBoard.getChessType(pos) != ChessType.EMPTY) {
            throw new IllegalArgumentException("The position is not empty.");
        }
        this.gameBoard.setChessType(pos, this.currentPlayer);
    }

    private void insertNewChess(Position pos, ChessType type) throws IllegalArgumentException {
        if (this.gameBoard.getChessType(pos) != ChessType.EMPTY) {
            throw new IllegalArgumentException("The position is not empty.");
        }
        if (type == ChessType.EMPTY) {
            throw new IllegalArgumentException("The chess type is empty.");
        }
        this.gameBoard.setChessType(pos, type);
    }

    private void clearChess(Position pos) {
        this.gameBoard.setChessType(pos, ChessType.EMPTY);
    }

    private void flipChess(Position pos) throws IllegalArgumentException {
        var type = this.gameBoard.getChessType(pos);
        if (type == ChessType.EMPTY) {
            throw new IllegalArgumentException("The position is empty.");
        } else if (type == ChessType.RED) {
            this.gameBoard.setChessType(pos, ChessType.BLUE);
        } else if (type == ChessType.BLUE) {
            this.gameBoard.setChessType(pos, ChessType.RED);
        }
    }

    public Logic() throws IllegalArgumentException {
        this(Set.of(new Position(1, 6), new Position(7, 1)), Set.of(new Position(1, 1), new Position(7, 6)));
    }

    public Logic(Set<Position> redChessInitPos, Set<Position> blueChessInitPos)
            throws IllegalArgumentException {
        this.gameBoard = new GameBoard();

        for (var pos : redChessInitPos) {
            this.gameBoard.setChessType(pos, ChessType.RED);
        }
        for (var pos : blueChessInitPos) {
            if (this.gameBoard.getChessType(pos) == ChessType.RED) {
                throw new IllegalArgumentException("The position is occupied.");
            }
            this.gameBoard.setChessType(pos, ChessType.BLUE);
        }

        this.currentPlayer = ChessType.RED;
        this.finished = false;
        this.winner = ChessType.EMPTY;
    }

    public Logic(int row, int col, Set<Position> redChessInitPos, Set<Position> blueChessInitPos)
            throws IllegalArgumentException {
        this.gameBoard = new GameBoard(row, col);

        for (var pos : redChessInitPos) {
            this.gameBoard.setChessType(pos, ChessType.RED);
        }
        for (var pos : blueChessInitPos) {
            if (this.gameBoard.getChessType(pos) == ChessType.RED) {
                throw new IllegalArgumentException("The position is occupied.");
            }
            this.gameBoard.setChessType(pos, ChessType.BLUE);
        }

        this.currentPlayer = ChessType.RED;
        this.finished = false;
        this.winner = ChessType.EMPTY;
    }

    private GameBoard gameBoard;
    private ChessType currentPlayer;
    private boolean finished;
    private ChessType winner;
}
