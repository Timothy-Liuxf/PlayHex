public interface ChessChangeListener {
    public void notifyChessAppear(Position pos, ChessType type);

    public void notifyChessDisappear(Position pos, ChessType type);
}
