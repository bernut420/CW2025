package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int HIDDEN_ROWS = 2;
    private static final int NEXT_PREVIEW_SIZE = 4;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane nextBlockPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private Label linesLabel;

    @FXML
    private Pane scalingContainer;

    @FXML
    private StackPane gameContainer;

    @FXML
    private Rectangle gameBorder;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] nextBrickPreview;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private final IntegerProperty currentScore = new SimpleIntegerProperty(0);
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);

    private double scaleFactor = 1.0;
    private static final double BASE_GAME_WIDTH = 250; // 10 bricks * 25px + gaps
    private static final double BASE_GAME_HEIGHT = 500; // 20 bricks * 25px + gaps

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        initializeScoreDisplay();

        // Add resize listener for responsive scaling
        if (gameContainer != null) {
            gameContainer.widthProperty().addListener((obs, oldVal, newVal) -> updateGameScale());
            gameContainer.heightProperty().addListener((obs, oldVal, newVal) -> updateGameScale());
        }


        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        hardDrop();
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);

        // Fullscreen Listener
        if (gameContainer != null && gameContainer.getScene() != null) {
            gameContainer.getScene().windowProperty().addListener((observable, oldWindow, newWindow) -> {
                if (newWindow instanceof Stage stage) {
                    stage.fullScreenProperty().addListener((obs, wasFullscreen, isNowFullscreen) -> {
                        Platform.runLater(this::updateGameScale);
                    });
                }
            });
        }
    }

    private void initializeScoreDisplay() {
        // Bind labels to properties if they exist
        if (scoreLabel != null) {
            scoreLabel.textProperty().bind(currentScore.asString("Score: %d"));
        }
        if (levelLabel != null) {
            levelLabel.textProperty().bind(currentLevel.asString("Level: %d"));
        }
        if (linesLabel != null) {
            linesLabel.textProperty().bind(linesCleared.asString("Lines: %d"));
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        initialiseNextBrickPanel(brick.getNextBrickData());
        updateBrickPosition(brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        Platform.runLater(this::updateGameScale);
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            updateBrickPosition(brick);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            updateNextBrickPanel(brick.getNextBrickData());
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void initialiseNextBrickPanel(int[][] nextBrickData) {
        if (nextBlockPanel == null) {
            return;
        }
        nextBlockPanel.getChildren().clear();
        nextBrickPreview = new Rectangle[NEXT_PREVIEW_SIZE][NEXT_PREVIEW_SIZE];
        for (int row = 0; row < NEXT_PREVIEW_SIZE; row++) {
            for (int col = 0; col < NEXT_PREVIEW_SIZE; col++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                nextBrickPreview[row][col] = rectangle;
                nextBlockPanel.add(rectangle, col, row);
            }
        }
        updateNextBrickPanel(nextBrickData);
    }

    private void updateNextBrickPanel(int[][] nextBrickData) {
        if (nextBrickPreview == null || nextBrickData == null) {
            return;
        }
        for (int row = 0; row < NEXT_PREVIEW_SIZE; row++) {
            for (int col = 0; col < NEXT_PREVIEW_SIZE; col++) {
                int value = (row < nextBrickData.length && col < nextBrickData[row].length)
                        ? nextBrickData[row][col]
                        : 0;
                setRectangleData(value, nextBrickPreview[row][col]);
            }
        }
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());

                // Update lines cleared and level
                updateLevelAndLines(downData.getClearRow().getLinesRemoved());

            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    private void hardDrop() {
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            // Keep moving the brick down until it can't move anymore
            boolean canMove = true;
            int dropDistance = 0;

            while (canMove) {
                DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
                canMove = (downData.getClearRow() == null); // If clearRow is null, brick is still moving
                if (canMove) {
                    refreshBrick(downData.getViewData());
                    dropDistance++;
                }
            }

            // Add score bonus based on drop distance
            if (dropDistance > 0) {

                int bonusPoints = dropDistance * 2;

                System.out.println("Hard drop: " + dropDistance + " rows, bonus: " + bonusPoints);
            }
        }
        gamePanel.requestFocus();
    }

    private void updateLevelAndLines(int linesRemoved) {
        if (linesRemoved > 0) {
            linesCleared.set(linesCleared.get() + linesRemoved);

            // Update level: increase every 10 lines cleared
            int newLevel = (linesCleared.get() / 10) + 1;
            if (newLevel > currentLevel.get()) {
                currentLevel.set(newLevel);
                updateGameSpeed();
            }
        }
    }

    private void updateGameSpeed() {
        if (timeLine != null) {
            timeLine.stop();
            // Increase speed as level increases (max 5x speed)
            double speedFactor = Math.max(0.2, 1.0 - (currentLevel.get() - 1) * 0.15);
            Duration newDuration = Duration.millis(400 * speedFactor);

            timeLine = new Timeline(new KeyFrame(
                    newDuration,
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
        }
    }

    private void updateGameScale() {
        if (gameContainer == null || scalingContainer == null) return;

        double containerWidth = gameContainer.getWidth();
        double containerHeight = gameContainer.getHeight();

        if (containerWidth <= 0 || containerHeight <= 0) return;

        // Calculate scale factors for width and height
        double scaleX = containerWidth / BASE_GAME_WIDTH;
        double scaleY = containerHeight / BASE_GAME_HEIGHT;

        // Use the smaller scale to maintain aspect ratio and fit entirely in view
        scaleFactor = Math.min(scaleX, scaleY);

        // Apply scaling
        scalingContainer.setScaleX(scaleFactor);
        scalingContainer.setScaleY(scaleFactor);

        // Center the scaled content
        scalingContainer.setTranslateX((containerWidth - (BASE_GAME_WIDTH * scaleFactor)) / 2);
        scalingContainer.setTranslateY((containerHeight - (BASE_GAME_HEIGHT * scaleFactor)) / 2);
    }

    private void centerGameInFullscreen() {
        if (gameContainer != null) {
            // Force re-layout to center the game
            gameContainer.requestLayout();

            // Re-center the game elements if needed
            if (gamePanel != null && brickPanel != null) {
                // Optional: Add any additional centering logic here
                System.out.println("Fullscreen changed - recentering game");
            }
        }
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        currentScore.bind(integerProperty);
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);

        // Reset game statistics
        currentScore.set(0);
        currentLevel.set(1);
        linesCleared.set(0);

        eventListener.createNewGame();
        gamePanel.requestFocus();

        // Reset game speed to initial value
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

    private void updateBrickPosition(ViewData brick) {
        if (brickPanel != null) {
            // Anchor to the background grid so active bricks never drift when they lock in place
            double originX = gamePanel.getLayoutX();
            double originY = gamePanel.getLayoutY();
            double cellWidth = BRICK_SIZE + gamePanel.getHgap();
            double cellHeight = BRICK_SIZE + gamePanel.getVgap();
            double xPos = originX + (brick.getxPosition() * cellWidth);
            double yPos = originY + ((brick.getyPosition() - HIDDEN_ROWS) * cellHeight);

            brickPanel.setLayoutX(xPos);
            brickPanel.setLayoutY(yPos);
        }
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
}
