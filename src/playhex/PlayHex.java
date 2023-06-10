package playhex;

import starter.*;
import guigame.*;
import consoletest.*;

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
        var starter = PlayHex.getGameStarter(args);
        starter.startGame(args);
    }
}
