package com.comp2042;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;
    
    private static final long LOCK_DELAY_MS = 500;
    private long lockStartTime = -1;
    private boolean isLocking = false;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow;
        
        if (!canMove) {
            clearRow = handleBrickLocked();
        } else {
            resetLockState();
            
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }

            clearRow = new ClearRow(0, board.getBoardMatrix(), 0, null);
        }
        
        return new DownData(clearRow, board.getViewData());
    }
    
    private ClearRow handleBrickLocked() {
        if (!isLocking) {
            startLockTimer();
        }
        
        if (isLockTimerExpired()) {
            return processBrickLock();
        }
        
        return new ClearRow(0, board.getBoardMatrix(), 0, null);
    }
    
    private void startLockTimer() {
        isLocking = true;
        lockStartTime = System.currentTimeMillis();
    }
    
    private boolean isLockTimerExpired() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lockStartTime >= LOCK_DELAY_MS;
    }
    
    private ClearRow processBrickLock() {
        resetLockState();
        
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }
        
        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }
        
        if (clearRow.getLinesRemoved() == 0) {
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }
        
        return clearRow;
    }

    public DownData onHardDropEvent(MoveEvent event) {
        int linesDropped = performHardDrop();
        
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        handleLineClearScore(clearRow, linesDropped);
        handleGameOverAfterDrop();

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return new DownData(clearRow, board.getViewData());
    }
    
    private int performHardDrop() {
        int linesDropped = 0;
        while (board.moveBrickDown()) {
            linesDropped++;
        }
        return linesDropped;
    }
    
    private void handleLineClearScore(ClearRow clearRow, int linesDropped) {
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }

        if (linesDropped > 0) {
            board.getScore().add(linesDropped * 2);
        }
    }
    
    private void handleGameOverAfterDrop() {
        boolean gameOver = board.createNewBrick();
        if (gameOver) {
            viewGuiController.gameOver();
        }
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (board.moveBrickLeft()) {
            resetLockState();
        }
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (board.moveBrickRight()) {
            resetLockState();
        }
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (board.rotateLeftBrick()) {
            resetLockState();
        }
        return board.getViewData();
    }
    
    private void resetLockState() {
        isLocking = false;
        lockStartTime = -1;
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        HoldResult result = board.holdCurrentBrick();
        if (result.isGameOver()) {
            viewGuiController.gameOver();
        }
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
    
    @Override
    public int[][] getBoardMatrix() {
        return board.getBoardMatrix();
    }
}
