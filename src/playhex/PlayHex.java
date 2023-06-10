package playhex;

import config.*;

public class PlayHex {
    public static void main(String[] args) {
        for (var arg : args) {
            if (arg.startsWith("--")) {
                if (arg.equals("--version")) {
                    System.out.println(Config.VERSION_STRING);
                    return;
                }
            }
        }
        var starter = GameStarterFactory.getGameStarter(args);
        starter.startGame(args);
    }
}
