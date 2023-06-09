public class ConsoleTest implements GameStarter {
    public void startGame(String[] args) {
        var gameBoard = new GameBoard();
        System.out.println(gameBoard.getChessCount(GameBoard.ChessType.EMPTY));
        System.out.println(gameBoard.getChessCount(GameBoard.ChessType.RED));
        System.out.println(gameBoard.getChessCount(GameBoard.ChessType.BLUE));
    }
}
