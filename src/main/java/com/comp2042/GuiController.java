package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.geometry.Pos;
import java.util.Optional;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 25;
    private static final int HIDDEN_ROWS = 2;
    private static final int NEXT_PREVIEW_SIZE = 4;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane ghostPanel;

    @FXML
    private GridPane nextBlockPanel;

    @FXML
    private GridPane holdBlockPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label highScoreLabel;

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

    @FXML
    private Rectangle gameBorderOuter;

    @FXML
    private Rectangle hudBorder;

    @FXML
    private Rectangle hudBorderOuter;

    @FXML
    private VBox startScreen;

    @FXML
    private Button playButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button menuButton;

    private Popup menuPopup;
    private Button resumeMenuButton;
    private Button settingsMenuButton;
    private Button exitMenuButton;

    @FXML
    private Button restartButton;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][] nextBrickPreview;
    private Rectangle[][] holdBrickPreview;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private final IntegerProperty currentScore = new SimpleIntegerProperty(0);
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);
    private int boardColumns;
    private int visibleRows;

    private double scaleFactor = 1.0;
    private static final double BASE_GAME_WIDTH = 480; // Total width including right-side HUD
    private static final double BASE_GAME_HEIGHT = 640; // Total height

    // Settings
    private SettingsManager settingsManager;
    private static final double BASE_GAME_SPEED = 400.0; // milliseconds

    // High Score
    private HighScoreManager highScoreManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        initializeScoreDisplay();
        initializeBorders();
        initializeMenu();
        
        // Hide game initially, show start screen
        if (scalingContainer != null) {
            scalingContainer.setVisible(false);
        }
        if (startScreen != null) {
            startScreen.setVisible(true);
        }

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
                    if (keyEvent.getCode() == KeyCode.C || keyEvent.getCode() == KeyCode.SHIFT) {
                        holdCurrentBrick();
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
        // Initialize managers
        highScoreManager = new HighScoreManager();
        settingsManager = new SettingsManager();
        
        // Apply loaded settings
        if (ghostPanel != null) {
            ghostPanel.setVisible(settingsManager.isGhostPieceEnabled());
        }
        
        // Bind labels to properties if they exist
        if (scoreLabel != null) {
            scoreLabel.textProperty().bind(currentScore.asString("Score: %d"));
        }
        if (highScoreLabel != null) {
            updateHighScoreDisplay();
        }
        if (levelLabel != null) {
            levelLabel.textProperty().bind(currentLevel.asString("Level: %d"));
        }
        if (linesLabel != null) {
            // Use a change listener to ensure the label updates
            linesCleared.addListener((obs, oldVal, newVal) -> {
                if (linesLabel != null) {
                    linesLabel.setText("Lines: " + newVal.intValue());
                }
            });
            // Set initial value
            linesLabel.setText("Lines: " + linesCleared.get());
        }
        
        // Listen for score changes to update high score
        currentScore.addListener((obs, oldVal, newVal) -> {
            checkAndUpdateHighScore(newVal.intValue());
        });
    }

    private void updateHighScoreDisplay() {
        if (highScoreLabel != null && highScoreManager != null) {
            highScoreLabel.setText("High Score: " + highScoreManager.getHighScore());
        }
    }

    private void checkAndUpdateHighScore(int currentScore) {
        if (highScoreManager != null && currentScore > highScoreManager.getHighScore()) {
            highScoreManager.updateHighScore(currentScore);
            updateHighScoreDisplay();
        }
    }

    private void initializeBorders() {
        // Add drop shadow effects to game border
        if (gameBorder != null) {
            DropShadow gameShadow = new DropShadow();
            gameShadow.setColor(Color.rgb(74, 144, 226, 0.5));
            gameShadow.setRadius(8);
            gameShadow.setOffsetX(0);
            gameShadow.setOffsetY(0);
            gameBorder.setEffect(gameShadow);
        }
        
        // Add drop shadow effects to HUD border
        if (hudBorder != null) {
            DropShadow hudShadow = new DropShadow();
            hudShadow.setColor(Color.rgb(139, 127, 168, 0.4));
            hudShadow.setRadius(6);
            hudShadow.setOffsetX(0);
            hudShadow.setOffsetY(0);
            hudBorder.setEffect(hudShadow);
        }
    }

    private void initializeMenu() {
        // Create popup menu
        menuPopup = new Popup();
        menuPopup.setAutoHide(true);
        
        // Create VBox container for menu buttons
        VBox menuContainer = new VBox(2);
        menuContainer.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #4a4a4a; -fx-border-width: 1px; -fx-padding: 4px;");
        
        // Create menu buttons
        resumeMenuButton = new Button("Pause");
        resumeMenuButton.setPrefWidth(100);
        resumeMenuButton.setPrefHeight(30);
        resumeMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;");
        resumeMenuButton.setOnAction(e -> {
            menuPopup.hide();
            resumeGame(e);
        });
        resumeMenuButton.setOnMouseEntered(e -> resumeMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #5a5a5a;"));
        resumeMenuButton.setOnMouseExited(e -> resumeMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;"));
        
        settingsMenuButton = new Button("Settings");
        settingsMenuButton.setPrefWidth(100);
        settingsMenuButton.setPrefHeight(30);
        settingsMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;");
        settingsMenuButton.setOnAction(e -> {
            menuPopup.hide();
            openSettings(e);
        });
        settingsMenuButton.setOnMouseEntered(e -> settingsMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #5a5a5a;"));
        settingsMenuButton.setOnMouseExited(e -> settingsMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;"));
        
        exitMenuButton = new Button("Exit");
        exitMenuButton.setPrefWidth(100);
        exitMenuButton.setPrefHeight(30);
        exitMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;");
        exitMenuButton.setOnAction(e -> {
            menuPopup.hide();
            exitGame(e);
        });
        exitMenuButton.setOnMouseEntered(e -> exitMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #5a5a5a;"));
        exitMenuButton.setOnMouseExited(e -> exitMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;"));
        
        menuContainer.getChildren().addAll(resumeMenuButton, settingsMenuButton, exitMenuButton);
        menuPopup.getContent().add(menuContainer);
        
        updateResumeMenuItemText();
    }

    @FXML
    public void showMenu(ActionEvent actionEvent) {
        if (menuButton != null && menuPopup != null) {
            // Calculate position below the button
            Window window = menuButton.getScene().getWindow();
            
            // Get button position relative to scene
            javafx.geometry.Point2D point = menuButton.localToScene(0, 0);
            
            // Calculate absolute screen coordinates
            double x = window.getX() + point.getX() + menuButton.getScene().getX();
            double y = window.getY() + point.getY() + menuButton.getScene().getY() + menuButton.getHeight() + 2;
            
            menuPopup.show(window, x, y);
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        boardColumns = boardMatrix[0].length;
        visibleRows = boardMatrix.length - HIDDEN_ROWS;
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
        
        // Initialize ghost panel
        if (ghostPanel != null) {
            ghostPanel.getChildren().clear();
            ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                    rectangle.setOpacity(0.3); // Make ghost semi-transparent
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    ghostRectangles[i][j] = rectangle;
                    ghostPanel.add(rectangle, j, i);
                }
            }
            // Apply ghost visibility setting
            if (settingsManager != null) {
                ghostPanel.setVisible(settingsManager.isGhostPieceEnabled());
            } else {
                ghostPanel.setVisible(true);
            }
        }
        
        initialiseNextBrickPanel(brick.getNextBrickData());
        initialiseHoldBrickPanel(brick.getHoldBrickData());
        updateGameBorderDimensions();
        updateBrickPosition(brick);
        updateGhostPosition(brick);

        // Initialize level and lines cleared
        currentLevel.set(1);
        linesCleared.set(0);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(BASE_GAME_SPEED),
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
            updateGhostPosition(brick);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            updateNextBrickPanel(brick.getNextBrickData());
            updateHoldBrickPanel(brick.getHoldBrickData());
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
        nextBlockPanel.setAlignment(javafx.geometry.Pos.CENTER);
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

    private void initialiseHoldBrickPanel(int[][] holdBrickData) {
        if (holdBlockPanel == null) {
            return;
        }
        holdBlockPanel.getChildren().clear();
        holdBlockPanel.setAlignment(javafx.geometry.Pos.CENTER);
        holdBrickPreview = new Rectangle[NEXT_PREVIEW_SIZE][NEXT_PREVIEW_SIZE];
        for (int row = 0; row < NEXT_PREVIEW_SIZE; row++) {
            for (int col = 0; col < NEXT_PREVIEW_SIZE; col++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                holdBrickPreview[row][col] = rectangle;
                holdBlockPanel.add(rectangle, col, row);
            }
        }
        updateHoldBrickPanel(holdBrickData);
    }

    private void updateHoldBrickPanel(int[][] holdBrickData) {
        if (holdBrickPreview == null || holdBrickData == null) {
            return;
        }
        for (int row = 0; row < NEXT_PREVIEW_SIZE; row++) {
            for (int col = 0; col < NEXT_PREVIEW_SIZE; col++) {
                int value = (row < holdBrickData.length && col < holdBrickData[row].length)
                        ? holdBrickData[row][col]
                        : 0;
                setRectangleData(value, holdBrickPreview[row][col]);
            }
        }
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            ClearRow clearRow = downData.getClearRow();
            
            // Always check for cleared rows (ClearRow is never null now)
            if (clearRow.getLinesRemoved() > 0) {
                // Animate line clear
                animateLineClear(clearRow.getClearedRowIndices());
                
                NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());

                // Update lines cleared and level
                updateLevelAndLines(clearRow.getLinesRemoved());
            }
            
            // Always refresh brick to show current state (including during lock delay)
            refreshBrick(downData.getViewData());
            
            // Refresh background only if no lines were cleared (animation handles it otherwise)
            if (clearRow.getLinesRemoved() == 0 && eventListener != null) {
                refreshGameBackground(eventListener.getBoardMatrix());
            }
        }
        gamePanel.requestFocus();
    }

    private void hardDrop() {
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
            
            // Handle cleared rows if any
            ClearRow clearRow = downData.getClearRow();
            if (clearRow.getLinesRemoved() > 0) {
                // Animate line clear - this will handle background refresh after animation
                animateLineClear(clearRow.getClearedRowIndices());
                
                NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());

                // Update lines cleared and level
                updateLevelAndLines(clearRow.getLinesRemoved());
            } else {
                // No lines cleared, refresh background normally
                if (eventListener != null) {
                    refreshGameBackground(eventListener.getBoardMatrix());
                }
            }
            
            // Refresh the brick display
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    private void holdCurrentBrick() {
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            ViewData data = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
            refreshBrick(data);
        }
        gamePanel.requestFocus();
    }

    private void animateLineClear(List<Integer> clearedRowIndices) {
        if (clearedRowIndices == null || displayMatrix == null || clearedRowIndices.isEmpty()) {
            return;
        }
        
        // Create parallel animation for all cleared rows (flash effect)
        javafx.animation.ParallelTransition parallel = new javafx.animation.ParallelTransition();
        
        for (Integer rowIndex : clearedRowIndices) {
            // Convert board row index to display row index (accounting for hidden rows)
            int displayRow = rowIndex - HIDDEN_ROWS;
            if (displayRow >= 0 && displayRow < displayMatrix.length) {
                for (int col = 0; col < displayMatrix[displayRow].length; col++) {
                    if (displayMatrix[displayRow][col] != null) {
                        Rectangle rect = displayMatrix[displayRow][col];
                        FadeTransition fade = new FadeTransition(Duration.millis(150), rect);
                        fade.setFromValue(1.0);
                        fade.setToValue(0.1);
                        fade.setCycleCount(4);
                        fade.setAutoReverse(true);
                        parallel.getChildren().add(fade);
                    }
                }
            }
        }
        
        // After animation completes refresh the background to show cleared state
        parallel.setOnFinished(e -> {
            Platform.runLater(() -> {
                if (eventListener != null) {
                    refreshGameBackground(eventListener.getBoardMatrix());
                }
            });
        });
        
        parallel.play();
    }

    private void updateLevelAndLines(int linesRemoved) {
        if (linesRemoved > 0) {
            // Update lines cleared
            int newLinesTotal = linesCleared.get() + linesRemoved;
            linesCleared.setValue(newLinesTotal);
            
            // Update label directly
            if (linesLabel != null) {
                linesLabel.setText("Lines: " + newLinesTotal);
            }

            // Increase level every 15 lines cleared
            int newLevel = (newLinesTotal / 15) + 1;
            int oldLevel = currentLevel.get();
            if (newLevel != oldLevel) {
                currentLevel.setValue(newLevel);
                updateGameSpeed();
            }
        }
    }

    private void updateGameSpeed() {
        if (timeLine != null) {
            timeLine.stop();
            // Increase speed as level increases (max 5x speed)
            double speedFactor = Math.max(0.2, 1.0 - (currentLevel.get() - 1) * 0.15);
            Duration newDuration = Duration.millis(BASE_GAME_SPEED * speedFactor);

            timeLine = new Timeline(new KeyFrame(
                    newDuration,
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
        }
    }

    public void updateGameScale() {
        if (gameContainer == null || scalingContainer == null) return;

        // Use layout bounds to get actual dimensions, fallback to width/height
        double containerWidth = gameContainer.getWidth();
        double containerHeight = gameContainer.getHeight();
        

        if (containerWidth <= 0 || containerHeight <= 0) {
            containerWidth = gameContainer.getLayoutBounds().getWidth();
            containerHeight = gameContainer.getLayoutBounds().getHeight();
        }
        
        if (containerWidth <= 0 || containerHeight <= 0) return;

        // Get preferred size of the scaling container
        double contentWidth = scalingContainer.getPrefWidth() > 0 ? scalingContainer.getPrefWidth() : BASE_GAME_WIDTH;
        double contentHeight = scalingContainer.getPrefHeight() > 0 ? scalingContainer.getPrefHeight() : BASE_GAME_HEIGHT;

        // Calculate scale factors for width and height
        double scaleX = containerWidth / contentWidth;
        double scaleY = containerHeight / contentHeight;

        scaleFactor = Math.min(scaleX, scaleY) * 0.85;
        
        // Cap maximum scale at 1.5x to make game bigger on large screens
        if (scaleFactor > 1.0) {
            scaleFactor = 1.0;
        }

        // Set transform origin to top-left corner (0, 0)
        scalingContainer.setTranslateX(0);
        scalingContainer.setTranslateY(0);
        
        // Apply scaling from top-left origin
        scalingContainer.setScaleX(scaleFactor);
        scalingContainer.setScaleY(scaleFactor);

        // Calculate scaled dimensions
        double scaledWidth = contentWidth * scaleFactor;
        double scaledHeight = contentHeight * scaleFactor;
        
        // Center the scaled content by translating it
        double offsetX = (containerWidth - scaledWidth) / 2;
        double offsetY = (containerHeight - scaledHeight) / 2;
        
        scalingContainer.setTranslateX(offsetX);
        scalingContainer.setTranslateY(offsetY);
    }

    private void centerGameInFullscreen() {
        if (gameContainer != null) {
            // Force re-layout to center the game
            gameContainer.requestLayout();

            // Re-center the game elements if needed
            if (gamePanel != null && brickPanel != null) {
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
        isGameOver.setValue(Boolean.TRUE);
        
        // Check and update high score when game ends
        if (highScoreManager != null) {
            checkAndUpdateHighScore(currentScore.get());
        }
        
        // Set up button actions
        if (gameOverPanel != null) {
            gameOverPanel.getPlayAgainButton().setOnAction(e -> newGame(e));
            gameOverPanel.getMainMenuButton().setOnAction(e -> {
                // Hide game, show start screen
                if (scalingContainer != null) {
                    scalingContainer.setVisible(false);
                }
                if (startScreen != null) {
                    startScreen.setVisible(true);
                }
                gameOverPanel.setVisible(false);
            });
        }
        
        gameOverPanel.setVisible(true);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);

        // Reset game statistics
        currentLevel.set(1);
        linesCleared.set(0);

        eventListener.createNewGame();
        gamePanel.requestFocus();

        // Reset game speed to initial value
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(BASE_GAME_SPEED),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        
        // Reset menu button state
        updateResumeMenuItemText();
    }

    @FXML
    public void resumeGame(ActionEvent actionEvent) {
        if (isGameOver.getValue() == Boolean.TRUE) {
            return;
        }
        
        boolean currentPauseState = isPause.getValue();
        isPause.setValue(!currentPauseState);
        
        if (isPause.getValue()) {
            // Pause the game
            if (timeLine != null) {
                timeLine.pause();
            }
        } else {
            // Resume the game
            if (timeLine != null) {
                timeLine.play();
            }
        }
        updateResumeMenuItemText();
        gamePanel.requestFocus();
    }

    private void updateResumeMenuItemText() {
        if (resumeMenuButton != null) {
            if (isPause.getValue()) {
                resumeMenuButton.setText("Resume");
            } else {
                resumeMenuButton.setText("Pause");
            }
        }
    }

    @FXML
    public void openSettings(ActionEvent actionEvent) {
        // Pause the game when opening settings
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            if (timeLine != null) {
                timeLine.pause();
            }
            isPause.setValue(Boolean.TRUE);
            updateResumeMenuItemText();
        }
        
        // Create and show settings dialog
        SettingsDialog settingsDialog = new SettingsDialog();
        
        // Set dialog owner to center it on the main window
        if (gameContainer != null && gameContainer.getScene() != null && gameContainer.getScene().getWindow() != null) {
            settingsDialog.initOwner(gameContainer.getScene().getWindow());
        }
        
        // Load current settings
        if (settingsManager != null) {
            settingsDialog.setGhostPieceEnabled(settingsManager.isGhostPieceEnabled());
            settingsDialog.setMusicEnabled(settingsManager.isMusicEnabled());
            settingsDialog.setMusicVolume(settingsManager.getMusicVolume());
        }
        
        // Show dialog and wait for result
        Optional<ButtonType> result = settingsDialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK && settingsManager != null) {
            // Apply and save settings
            settingsManager.setGhostPieceEnabled(settingsDialog.isGhostPieceEnabled());
            settingsManager.setMusicEnabled(settingsDialog.isMusicEnabled());
            settingsManager.setMusicVolume(settingsDialog.getMusicVolume());
            
            // Apply ghost visibility immediately
            if (ghostPanel != null) {
                ghostPanel.setVisible(settingsManager.isGhostPieceEnabled());
            }
            
            //  Will apply music setting when music feature is implemented
            //  Will apply music volume when music feature is implemented
 
        }
        
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

    private void updateGhostPosition(ViewData brick) {
        if (ghostPanel != null && ghostRectangles != null) {
            // Position ghost at the landing location
            double originX = gamePanel.getLayoutX();
            double originY = gamePanel.getLayoutY();
            double cellWidth = BRICK_SIZE + gamePanel.getHgap();
            double cellHeight = BRICK_SIZE + gamePanel.getVgap();
            double xPos = originX + (brick.getGhostXPosition() * cellWidth);
            double yPos = originY + ((brick.getGhostYPosition() - HIDDEN_ROWS) * cellHeight);

            ghostPanel.setLayoutX(xPos);
            ghostPanel.setLayoutY(yPos);
            
            // Update ghost block colors to match current brick
            int[][] brickData = brick.getBrickData();
            for (int i = 0; i < brickData.length && i < ghostRectangles.length; i++) {
                for (int j = 0; j < brickData[i].length && j < ghostRectangles[i].length; j++) {
                    if (ghostRectangles[i][j] != null) {
                        if (brickData[i][j] != 0) {
                            ghostRectangles[i][j].setFill(getFillColor(brickData[i][j]));
                            ghostRectangles[i][j].setOpacity(0.3); // Keep semi-transparent
                            ghostRectangles[i][j].setVisible(true);
                        } else {
                            ghostRectangles[i][j].setVisible(false);
                        }
                    }
                }
            }
        }
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @FXML
    private void startGame(ActionEvent event) {
        if (startScreen != null) {
            startScreen.setVisible(false);
        }
        if (scalingContainer != null) {
            scalingContainer.setVisible(true);
        }
        if (eventListener == null) {
            // Initialize game if not already initialized
            new GameController(this);
        }
        gamePanel.requestFocus();
    }

    @FXML
    private void exitGame(ActionEvent event) {
        Platform.exit();
    }

    private void updateGameBorderDimensions() {
        if (gameBorder == null || gamePanel == null) {
            return;
        }
        double gapX = gamePanel.getHgap();
        double gapY = gamePanel.getVgap();
        double gridWidth = (boardColumns * BRICK_SIZE) + (Math.max(0, boardColumns - 1) * gapX);
        double gridHeight = (visibleRows * BRICK_SIZE) + (Math.max(0, visibleRows - 1) * gapY);
        double padding = gameBorder.getStrokeWidth();

        gameBorder.setWidth(gridWidth + padding);
        gameBorder.setHeight(gridHeight + padding);
        gameBorder.setLayoutX(gamePanel.getLayoutX() - padding / 2);
        gameBorder.setLayoutY(gamePanel.getLayoutY() - padding / 2);
    }
}
