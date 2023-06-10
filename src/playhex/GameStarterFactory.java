package playhex;

import consoletest.*;
import guigame.*;
import starter.*;

public class GameStarterFactory {
    public static GameStarter getGameStarter(String[] args) {
        for (var arg : args) {
            if (arg.startsWith("--") && arg.equals("--test")) {
                return new ConsoleTest();
            }
        }
        return new GuiGameStarter();
    }
}
