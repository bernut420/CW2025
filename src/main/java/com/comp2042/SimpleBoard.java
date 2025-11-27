package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private static final int HIDDEN_ROWS = 2;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick holdBrick;
    private boolean holdUsedThisTurn;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        holdBrick = null;
        holdUsedThisTurn = false;
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        // Spawn bricks just above the visible playfield so players can stack up to 20 blocks high
        currentOffset = new Point(4, HIDDEN_ROWS);
        holdUsedThisTurn = false;
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        Point ghostPosition = calculateGhostPosition();
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), 
                brickGenerator.getNextBrick().getShapeMatrix().get(0), getHoldPreviewData(),
                (int) ghostPosition.getX(), (int) ghostPosition.getY());
    }

    private Point calculateGhostPosition() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int[][] currentShape = brickRotator.getCurrentShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();
        
        // Start from current position and move down until collision
        int ghostY = currentY;
        while (true) {
            int testY = ghostY + 1;
            if (MatrixOperations.intersect(currentMatrix, currentShape, currentX, testY)) {
                break;
            }
            ghostY = testY;
        }
        
        return new Point(currentX, ghostY);
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
        holdBrick = null;
        holdUsedThisTurn = false;
    }

    @Override
    public HoldResult holdCurrentBrick() {
        if (holdUsedThisTurn) {
            return HoldResult.failure();
        }

        Brick current = brickRotator.getBrick();
        if (current == null) {
            return HoldResult.failure();
        }

        if (holdBrick == null) {
            holdBrick = current;
            boolean gameOver = createNewBrick();
            holdUsedThisTurn = true;
            return HoldResult.success(gameOver);
        } else {
            Brick swap = holdBrick;
            holdBrick = current;
            brickRotator.setBrick(swap);
            currentOffset = new Point(4, HIDDEN_ROWS);
            boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
            if (conflict) {
                holdBrick = swap;
                brickRotator.setBrick(current);
                return HoldResult.failure();
            }
            holdUsedThisTurn = true;
            return HoldResult.success(false);
        }
    }

    private int[][] getHoldPreviewData() {
        if (holdBrick == null) {
            return new int[4][4];
        }
        return holdBrick.getShapeMatrix().get(0);
    }
}
