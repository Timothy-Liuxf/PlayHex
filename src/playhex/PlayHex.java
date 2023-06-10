package playhex;

import starter.*;
import guigame.*;
import consoletest.*;
import config.*;

public class PlayHex {
    private static GameStarter getGameStarter(String[] args) {
        for (var arg : args) {
            if (arg.startsWith("--") && arg.equals("--test")) {
                return new ConsoleTest();
            }
        }
        return new GuiGameStarter();
    }

    public static void main(String[] args) {
        for (var arg : args) {
            if (arg.startsWith("--")) {
                if (arg.equals("--version")) {
                    System.out.println(Config.VERSION_STRING);
                    return;
                }
            }
        }
        var starter = PlayHex.getGameStarter(args);
        starter.startGame(args);
    }
}
