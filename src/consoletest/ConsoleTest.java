package consoletest;

import java.util.Set;

import gameboard.*;
import starter.*;
import logic.*;

public class ConsoleTest implements GameStarter {
    private static void printChessBoard(Logic logic) {
        for (int i = 0; i < logic.getRow(); ++i) {
            if (i % 2 != 0) {
                System.out.print(" ");
            }
            for (int j = 0; j < logic.getCol(); ++j) {
                var pos = new Position(i, j);
                var type = logic.getChessType(pos);
                System.out.print(type == ChessType.EMPTY ? 0 : type == ChessType.RED ? 1 : 2);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    @Override
    public void startGame(String[] args) {
        {
            var logic = new Logic();
            System.out.println(logic.getChessCount(ChessType.EMPTY));
            System.out.println(logic.getChessCount(ChessType.RED));
            System.out.println(logic.getChessCount(ChessType.BLUE));
            printChessBoard(logic);

            System.out.println("\n================\n");
        }

        {
            var logic = new Logic(2, 2, Set.of(new Position(0, 0)), Set.of(new Position(1, 1)));
            printChessBoard(logic);
            System.out.println();
            if (logic.isFinished()) {
                System.out.println("Game finished");
            } else {
                System.out.println("Game not finished");
            }
            logic.moveChess(new Position(0, 0), new Position(1, 0));
            printChessBoard(logic);
            System.out.println();
            if (logic.isFinished()) {
                System.out.println("Game finished");
                System.out.println("Winner: " + logic.getWinner());
            } else {
                System.out.println("Game not finished");
            }
        }

        {
            var logic = new Logic(3, 3, Set.of(new Position(0, 0)), Set.of(new Position(2, 2)));
            printChessBoard(logic);
            System.out.println();

            logic.moveChess(new Position(0, 0), new Position(2, 0));
            printChessBoard(logic);
            System.out.println();

            if (logic.moveChess(new Position(0, 0), new Position(2, 0))) {
                System.out.println("NONONO!!!");
            }
            printChessBoard(logic);
            System.out.println();

            logic.moveChess(new Position(2, 2), new Position(1, 1));
            printChessBoard(logic);
            System.out.println();

            logic.moveChess(new Position(2, 0), new Position(1, 0));
            printChessBoard(logic);
            System.out.println();

            logic.moveChess(new Position(2, 2), new Position(1, 2));
            printChessBoard(logic);
            System.out.println();

            logic.moveChess(new Position(1, 0), new Position(0, 0));
            printChessBoard(logic);
            System.out.println();

            logic.moveChess(new Position(1, 1), new Position(0, 1));
            printChessBoard(logic);
            System.out.println();

            logic.moveChess(new Position(2, 0), new Position(2, 1));
            printChessBoard(logic);
            System.out.println();

            if (logic.isFinished()) {
                System.out.println("Game finished");
            } else {
                System.out.println("Game not finished");
            }

            logic.moveChess(new Position(1, 2), new Position(0, 2));
            printChessBoard(logic);
            System.out.println();

            if (logic.isFinished()) {
                System.out.println("Game finished");
                System.out.println("Winner: " + logic.getWinner());
            } else {
                System.out.println("Game not finished");
            }
        }
    }
}
