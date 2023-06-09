public class Program {
    private static GameStarter getGameStarter(String[] args) {
        for (var arg : args) {
            if (arg.startsWith("--") && arg.equals("--test")) {
                return new ConsoleTest();
            }
        }
        throw new IllegalArgumentException("Invalid argument.");
    }

    public static void main(String[] args) {
        var starter = Program.getGameStarter(args);
        starter.startGame(args);
    }
}
