import javax.swing.JFrame;

public class GuiGameStarter implements GameStarter {
    public void startGame(String[] args) {
        var guiGame = new GuiGame(args);
        guiGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}