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
            if (!isLocking) {
                isLocking = true;
                lockStartTime = System.currentTimeMillis();
            }
            
            long currentTime = System.currentTimeMillis();
            if (currentTime - lockStartTime >= LOCK_DELAY_MS) {
                isLocking = false;
                lockStartTime = -1;
                
                board.mergeBrickToBackground();
                clearRow = board.clearRows();
                
                if (clearRow.getLinesRemoved() > 0) {
                    board.getScore().add(clearRow.getScoreBonus());
                }
                
                if (board.createNewBrick()) {
                    viewGuiController.gameOver();
                }
                
                if (clearRow.getLinesRemoved() == 0) {
                    viewGuiController.refreshGameBackground(board.getBoardMatrix());
                }
            } else {
                clearRow = new ClearRow(0, board.getBoardMatrix(), 0, null);
            }
        } else {
            isLocking = false;
            lockStartTime = -1;
            
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }

            clearRow = new ClearRow(0, board.getBoardMatrix(), 0, null);
        }
        
        return new DownData(clearRow, board.getViewData());
    }

    public DownData onHardDropEvent(MoveEvent event) {
        boolean canMove = true;
        int linesDropped = 0;

        while (canMove) {
            canMove = board.moveBrickDown();
            if (canMove) {
                linesDropped++;
            }
        }

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }

        if (linesDropped > 0) {
            board.getScore().add(linesDropped * 2);
        }

        boolean gameOver = board.createNewBrick();
        if (gameOver) {
            viewGuiController.gameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (board.moveBrickLeft()) {
            isLocking = false;
            lockStartTime = -1;
        }
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (board.moveBrickRight()) {
            isLocking = false;
            lockStartTime = -1;
        }
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (board.rotateLeftBrick()) {
            isLocking = false;
            lockStartTime = -1;
        }
        return board.getViewData();
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
