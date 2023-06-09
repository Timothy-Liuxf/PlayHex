
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
