public class Program {
    private static GameStarter getGameStarter() {
        return new ConsoleTest();
    }

    public static void main(String[] args) {
        var starter = Program.getGameStarter();
        starter.startGame(args);
    }
}
