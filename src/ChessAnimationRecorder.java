import java.util.LinkedList;
import java.util.Queue;

public class ChessAnimationRecorder implements ChessChangeListener {
    public static class ChessAnimationRecord {
        public ChessType type;
        public Position pos;
        public boolean isAppear;

        public ChessAnimationRecord(ChessType type, Position pos, boolean isAppear) {
            this.type = type;
            this.pos = pos;
            this.isAppear = isAppear;
        }
    }

    @Override
    public void notifyChessAppear(Position pos, ChessType type) {
        this.queue.add(new ChessAnimationRecord(type, pos, true));
        if (currentAnimateNum[pos.row][pos.col] == 0) {
            currentAnimateScale[pos.row][pos.col] = 0.0;
            currentChessType[pos.row][pos.col] = type;
        }
        ++currentAnimateNum[pos.row][pos.col];
    }

    @Override
    public void notifyChessDisappear(Position pos, ChessType type) {
        this.queue.add(new ChessAnimationRecord(type, pos, false));
        if (currentAnimateNum[pos.row][pos.col] == 0) {
            currentAnimateScale[pos.row][pos.col] = 1.0;
            currentChessType[pos.row][pos.col] = type;
        }
        ++currentAnimateNum[pos.row][pos.col];
    }

    public ChessAnimationRecord getTopAnimationRecord() {
        if (this.queue.isEmpty()) {
            return null;
        }
        return this.queue.peek();
    }

    public boolean popAnimationRecord() {
        if (this.queue.isEmpty()) {
            return false;
        }
        var record = this.queue.poll();
        --currentAnimateNum[record.pos.row][record.pos.col];
        var newRecord = this.queue.peek();
        if (newRecord != null) {
            if (newRecord.isAppear) {
                currentAnimateScale[newRecord.pos.row][newRecord.pos.col] = 0.0;
            } else {
                currentAnimateScale[newRecord.pos.row][newRecord.pos.col] = 1.0;
            }
            currentChessType[newRecord.pos.row][newRecord.pos.col] = newRecord.type;
        }
        return true;
    }

    public boolean isAnimationFinished() {
        return this.queue.isEmpty();
    }

    public boolean isAnimating(int row, int col) {
        return currentAnimateNum[row][col] != 0;
    }

    public double getAnimationScale(int row, int col) {
        return currentAnimateScale[row][col];
    }

    public void setAnimationScale(int row, int col, double scale) {
        currentAnimateScale[row][col] = scale;
    }

    public ChessType getChessType(int row, int col) {
        return currentChessType[row][col];
    }

    public ChessAnimationRecorder(int row, int col) {
        currentAnimateNum = new int[row][col];
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; j++) {
                currentAnimateNum[i][j] = 0;
            }
        }

        currentAnimateScale = new double[row][col];
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; j++) {
                currentAnimateScale[i][j] = 0.0;
            }
        }

        currentChessType = new ChessType[row][col];
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; j++) {
                currentChessType[i][j] = ChessType.EMPTY;
            }
        }
    }

    Queue<ChessAnimationRecord> queue = new LinkedList<ChessAnimationRecord>();
    int[][] currentAnimateNum;
    double[][] currentAnimateScale;
    ChessType[][] currentChessType;
}
