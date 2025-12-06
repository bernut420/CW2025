package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Popup;
import javafx.stage.Window;
import java.util.Optional;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 25;
    private static final int HIDDEN_ROWS = 2;
    private static final int NEXT_PREVIEW_SIZE = 4;
    private static final int LINES_PER_LEVEL = 15;
    
    // Styling constants
    private static final String BUTTON_BASE_STYLE = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;";
    private static final String BUTTON_HOVER_STYLE = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #5a5a5a;";
    private static final String KEYBIND_KEY_STYLE = "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-background-color: rgba(255, 215, 0, 0.15); -fx-background-radius: 4px; -fx-border-color: #FFD700; -fx-border-width: 1px; -fx-border-radius: 4px; -fx-padding: 4px 8px; -fx-alignment: center;";
    private static final String KEYBIND_ACTION_STYLE = "-fx-font-size: 11px; -fx-font-weight: normal; -fx-text-fill: #e0e0e0; -fx-padding: 4px 0;";
    
    // Border gradient colors
    private static final Color BORDER_COLOR_1 = Color.rgb(91, 160, 242);
    private static final Color BORDER_COLOR_2 = Color.rgb(74, 144, 226);
    private static final Color BORDER_COLOR_3 = Color.rgb(53, 122, 189);
    private static final Color BORDER_COLOR_4 = Color.rgb(42, 95, 143);
    private static final Color BORDER_OUTER_COLOR_1 = Color.rgb(123, 179, 255);
    private static final Color BORDER_OUTER_COLOR_2 = Color.rgb(107, 163, 240);

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
    private Button mainMenuButton;
    private Button settingsMenuButton;
    private Button exitMenuButton;
    private boolean wasPausedBeforeMenu = false;
    private boolean menuActionTaken = false;
    private boolean wasPausedBeforeSettings = false;

    @FXML
    private Button restartButton;

    @FXML
    private Pane keybindsPane;

    @FXML
    private VBox keybindsContainer;

    @FXML
    private GridPane keybindsTable;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][] nextBrickPreview;
    private Rectangle[][] holdBrickPreview;

    private ImageView gameAreaBackgroundImageView;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private final IntegerProperty currentScore = new SimpleIntegerProperty(0);
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);
    private int boardColumns;
    private int visibleRows;

    private double scaleFactor = 1.0;
    private static final double BASE_GAME_WIDTH = 480;
    private static final double BASE_GAME_HEIGHT = 640;

    private SettingsManager settingsManager;
    private static final double BASE_GAME_SPEED = 400.0;

    private HighScoreManager highScoreManager;
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        initializeScoreDisplay();
        initializeBorders();
        initializeMenu();
        initializeStartScreen();
        initializeGameAreaBackground();
        initializeKeybinds();
        positionKeybinds();
        initializeMusic();
        
        if (scalingContainer != null) {
            scalingContainer.setVisible(false);
        }
        if (startScreen != null) {
            startScreen.setVisible(true);
        }
        if (keybindsContainer != null) {
            keybindsContainer.setVisible(false);
        }
        
        if (gameContainer != null) {
            gameContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
                positionKeybinds();
                updateGameScale();
            });
            gameContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
                positionKeybinds();
                updateGameScale();
            });
        }

        gamePanel.setOnKeyPressed(this::handleKeyPress);
        gameOverPanel.setVisible(false);

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
        highScoreManager = new HighScoreManager();
        settingsManager = new SettingsManager();
        
        if (ghostPanel != null) {
            ghostPanel.setVisible(settingsManager.isGhostPieceEnabled());
        }
        
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
            linesCleared.addListener((obs, oldVal, newVal) -> {
                if (linesLabel != null) {
                    linesLabel.setText("Lines: " + newVal.intValue());
                }
            });
            linesLabel.setText("Lines: " + linesCleared.get());
        }
        
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
        if (gameBorder != null) {
            applyBorderStyle(gameBorder, createMainBorderGradient(), 4, createMainBorderShadow());
        }
        
        if (hudBorder != null) {
            applyBorderStyle(hudBorder, createMainBorderGradient(), 4, createMainBorderShadow());
        }
        
        if (gameBorderOuter != null) {
            applyBorderStyle(gameBorderOuter, createOuterBorderGradient(), 2, createOuterBorderShadow());
        }
        
        if (hudBorderOuter != null) {
            applyBorderStyle(hudBorderOuter, createOuterBorderGradient(), 2, createOuterBorderShadow());
        }
    }
    
    private LinearGradient createMainBorderGradient() {
        return new LinearGradient(
            0, 0, 1, 1, true, null,
            new Stop(0.0, BORDER_COLOR_1),
            new Stop(0.3, BORDER_COLOR_2),
            new Stop(0.7, BORDER_COLOR_3),
            new Stop(1.0, BORDER_COLOR_4)
        );
    }
    
    private LinearGradient createOuterBorderGradient() {
        return new LinearGradient(
            0, 0, 1, 1, true, null,
            new Stop(0.0, BORDER_OUTER_COLOR_1),
            new Stop(0.5, BORDER_OUTER_COLOR_2),
            new Stop(1.0, BORDER_COLOR_2)
        );
    }
    
    private DropShadow createMainBorderShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(74, 144, 226, 0.6));
        shadow.setRadius(10);
        shadow.setOffsetX(0);
        shadow.setOffsetY(0);
        return shadow;
    }
    
    private DropShadow createOuterBorderShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(107, 163, 240, 0.4));
        shadow.setRadius(6);
        shadow.setOffsetX(0);
        shadow.setOffsetY(0);
        return shadow;
    }
    
    private void applyBorderStyle(Rectangle border, LinearGradient gradient, double strokeWidth, DropShadow shadow) {
        border.setStroke(gradient);
        border.setStrokeWidth(strokeWidth);
        border.setEffect(shadow);
    }

    private void initializeStartScreen() {
        if (startScreen != null) {
            try {
                URL imageUrl = getClass().getClassLoader().getResource("Pixel Fields.png");
                if (imageUrl == null) {
                    System.err.println("Failed to find 'Pixel Fields.png' in resources");
                    return;
                }
                
                Image backgroundImage = new Image(imageUrl.toExternalForm());
                if (backgroundImage.isError()) {
                    System.err.println("Error loading image: " + backgroundImage.getException().getMessage());
                    return;
                }
                
                BackgroundSize backgroundSize = new BackgroundSize(
                    BackgroundSize.AUTO, BackgroundSize.AUTO,
                    false, false, false, true
                );
                
                BackgroundImage bgImage = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    backgroundSize
                );
                
                startScreen.setBackground(new Background(bgImage));
            } catch (Exception e) {
                System.err.println("Failed to load start screen background image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void initializeGameAreaBackground() {
        if (scalingContainer != null) {
            try {
                URL imageUrl = getClass().getClassLoader().getResource("Pixel Field Play.png");
                
                if (imageUrl == null) {
                    System.err.println("Failed to find 'Pixel Field Play.png' in resources");
                    return;
                }
                
                Image backgroundImage = new Image(imageUrl.toExternalForm());
                
                if (backgroundImage.isError()) {
                    System.err.println("Error loading game area image: " + backgroundImage.getException().getMessage());
                    return;
                }
                
                gameAreaBackgroundImageView = new ImageView(backgroundImage);
                double scaleX = 5.0;
                double scaleY = 2.5;
                gameAreaBackgroundImageView.setFitWidth(BASE_GAME_WIDTH * scaleX);
                gameAreaBackgroundImageView.setFitHeight(BASE_GAME_HEIGHT * scaleY);
                gameAreaBackgroundImageView.setPreserveRatio(false);
                gameAreaBackgroundImageView.setLayoutX(-BASE_GAME_WIDTH * (scaleX - 1) / 2);
                gameAreaBackgroundImageView.setLayoutY(-BASE_GAME_HEIGHT * (scaleY - 1) / 2);
                gameAreaBackgroundImageView.setSmooth(true);
                
                scalingContainer.getChildren().add(0, gameAreaBackgroundImageView);
                
                if (settingsManager != null) {
                    gameAreaBackgroundImageView.setVisible(settingsManager.isBackgroundPictureEnabled());
                } else {
                    gameAreaBackgroundImageView.setVisible(true);
                }
            } catch (Exception e) {
                System.err.println("Failed to load game area background image: " + e.getMessage());
            }
        }
    }

    private void initializeKeybinds() {
        if (keybindsTable == null) {
            return;
        }
        
        String[][] keybinds = {
            {"← / A", "Move Left"},
            {"→ / D", "Move Right"},
            {"↑ / W", "Rotate"},
            {"↓ / S", "Move Down"},
            {"SPACE", "Hard Drop"},
            {"C / SHIFT", "Hold"},
            {"N", "New Game"}
        };
        
        int row = 0;
        for (String[] keybind : keybinds) {
            Label keyLabel = createKeybindKeyLabel(keybind[0]);
            Label actionLabel = createKeybindActionLabel(keybind[1]);
            
            keybindsTable.add(keyLabel, 0, row);
            keybindsTable.add(actionLabel, 1, row);
            row++;
        }
    }

    private void positionKeybinds() {
        if (keybindsPane == null || gameContainer == null) {
            return;
        }
        
        keybindsPane.setLayoutX(30);
        keybindsPane.setLayoutY(50);
        
        if (keybindsContainer != null) {
            keybindsContainer.setLayoutX(0);
            keybindsContainer.setLayoutY(0);
        }
    }
    
    private void initializeMusic() {
        try {
            URL musicUrl = getClass().getClassLoader().getResource("Music.mp3");
            if (musicUrl == null) {
                System.err.println("Failed to find 'Music.mp3' in resources");
                return;
            }
            
            Media media = new Media(musicUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            
            if (settingsManager != null) {
                mediaPlayer.setVolume(settingsManager.getMusicVolume());
                if (settingsManager.isMusicEnabled()) {
                    mediaPlayer.play();
                }
            } else {
                mediaPlayer.setVolume(0.7);
                mediaPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateMusicSettings() {
        if (mediaPlayer == null || settingsManager == null) {
            return;
        }
        
        mediaPlayer.setVolume(settingsManager.getMusicVolume());
        
        if (settingsManager.isMusicEnabled()) {
            if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                mediaPlayer.play();
            }
        } else {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            }
        }
    }
    
    private void playDropSound() {
        try {
            URL dropSoundUrl = getClass().getClassLoader().getResource("drop.mp3");
            if (dropSoundUrl != null) {
                Media dropMedia = new Media(dropSoundUrl.toExternalForm());
                MediaPlayer dropPlayer = new MediaPlayer(dropMedia);
                dropPlayer.setVolume(0.7);
                dropPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Failed to play drop sound: " + e.getMessage());
        }
    }
    
    private void playLineClearSound() {
        try {
            URL lineClearSoundUrl = getClass().getClassLoader().getResource("lineclear.mp3");
            if (lineClearSoundUrl != null) {
                Media lineClearMedia = new Media(lineClearSoundUrl.toExternalForm());
                MediaPlayer lineClearPlayer = new MediaPlayer(lineClearMedia);
                lineClearPlayer.setVolume(0.7);
                lineClearPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Failed to play line clear sound: " + e.getMessage());
        }
    }
    
    private void playLevelUpSound() {
        try {
            URL levelUpSoundUrl = getClass().getClassLoader().getResource("levelup.mp3");
            if (levelUpSoundUrl != null) {
                Media levelUpMedia = new Media(levelUpSoundUrl.toExternalForm());
                MediaPlayer levelUpPlayer = new MediaPlayer(levelUpMedia);
                levelUpPlayer.setVolume(0.7);
                levelUpPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Failed to play level up sound: " + e.getMessage());
        }
    }
    
    private void initializeMenu() {
        menuPopup = new Popup();
        menuPopup.setAutoHide(true);
        
        VBox menuContainer = new VBox(2);
        menuContainer.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #4a4a4a; -fx-border-width: 1px; -fx-padding: 4px;");
        
        mainMenuButton = createMenuButton("Main Menu", e -> {
            menuActionTaken = true;
            menuPopup.hide();
            handleMainMenuAction();
        });
        
        settingsMenuButton = createMenuButton("Settings", e -> {
            menuActionTaken = true;
            boolean menuAutoPaused = !wasPausedBeforeMenu;
            menuPopup.hide();
            openSettings(e, menuAutoPaused);
        });
        
        exitMenuButton = createMenuButton("Exit", e -> {
            menuActionTaken = true;
            menuPopup.hide();
            exitGame(e);
        });
        
        menuContainer.getChildren().addAll(mainMenuButton, settingsMenuButton, exitMenuButton);
        menuPopup.getContent().add(menuContainer);
        
        menuPopup.setOnHidden(e -> {
            if (!menuActionTaken && wasPausedBeforeMenu == false && isPause.getValue() == Boolean.TRUE) {
                isPause.setValue(Boolean.FALSE);
                if (timeLine != null && isGameOver.getValue() == Boolean.FALSE) {
                    timeLine.play();
                }
            }
            wasPausedBeforeMenu = false;
            menuActionTaken = false;
        });
    }

    @FXML
    public void showMenu(ActionEvent actionEvent) {
        if (menuButton != null && menuPopup != null) {
            menuActionTaken = false;
            if (isGameOver.getValue() == Boolean.FALSE) {
                wasPausedBeforeMenu = isPause.getValue();
                if (!isPause.getValue()) {
                    isPause.setValue(Boolean.TRUE);
                    if (timeLine != null) {
                        timeLine.pause();
                    }
                }
            }
            
            Window window = menuButton.getScene().getWindow();
            javafx.geometry.Point2D point = menuButton.localToScene(0, 0);
            double x = window.getX() + point.getX() + menuButton.getScene().getX();
            double y = window.getY() + point.getY() + menuButton.getScene().getY() + menuButton.getHeight() + 2;
            
            menuPopup.show(window, x, y);
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        initializeBoardDimensions(boardMatrix);
        initializeDisplayMatrix(boardMatrix);
        initializeBrickRectangles(brick);
        initializeGhostRectangles(brick);
        initializePreviewPanels(brick);
        
        updateGameBorderDimensions();
        updateBrickPosition(brick);
        updateGhostPosition(brick);

        resetGameStats();
        startGameTimeline(BASE_GAME_SPEED);
        Platform.runLater(this::updateGameScale);
    }
    
    private void initializeBoardDimensions(int[][] boardMatrix) {
        boardColumns = boardMatrix[0].length;
        visibleRows = boardMatrix.length - HIDDEN_ROWS;
    }
    
    private void initializeDisplayMatrix(int[][] boardMatrix) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - HIDDEN_ROWS);
            }
        }
    }
    
    private void initializeBrickRectangles(ViewData brick) {
        int[][] brickData = brick.getBrickData();
        rectangles = new Rectangle[brickData.length][brickData[0].length];
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brickData[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
    }
    
    private void initializeGhostRectangles(ViewData brick) {
        if (ghostPanel == null) {
            return;
        }
        
        ghostPanel.getChildren().clear();
        int[][] brickData = brick.getBrickData();
        ghostRectangles = new Rectangle[brickData.length][brickData[0].length];
        
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                Rectangle rectangle = createGhostRectangle(brickData[i][j]);
                ghostRectangles[i][j] = rectangle;
                ghostPanel.add(rectangle, j, i);
            }
        }
        
        boolean ghostVisible = settingsManager != null 
            ? settingsManager.isGhostPieceEnabled() 
            : true;
        ghostPanel.setVisible(ghostVisible);
    }
    
    private Rectangle createGhostRectangle(int colorIndex) {
        Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
        rectangle.setFill(getFillColor(colorIndex));
        rectangle.setOpacity(0.3);
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
        return rectangle;
    }
    
    private void initializePreviewPanels(ViewData brick) {
        initialiseNextBrickPanel(brick.getNextBrickData());
        initialiseHoldBrickPanel(brick.getHoldBrickData());
    }
    
    private void resetGameStats() {
        currentLevel.set(1);
        linesCleared.set(0);
    }

    private static final Paint[] BRICK_COLORS = {
        Color.TRANSPARENT,  // 0
        Color.AQUA,         // 1
        Color.BLUEVIOLET,   // 2
        Color.DARKGREEN,    // 3
        Color.YELLOW,       // 4
        Color.RED,          // 5
        Color.BEIGE,        // 6
        Color.BURLYWOOD     // 7
    };
    
    private Paint getFillColor(int colorIndex) {
        if (colorIndex >= 0 && colorIndex < BRICK_COLORS.length) {
            return BRICK_COLORS[colorIndex];
        }
        return Color.WHITE;
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
        nextBrickPreview = initializePreviewPanel(nextBlockPanel);
        updateNextBrickPanel(nextBrickData);
    }

    private void updateNextBrickPanel(int[][] nextBrickData) {
        updatePreviewPanel(nextBrickPreview, nextBrickData);
    }

    private void initialiseHoldBrickPanel(int[][] holdBrickData) {
        if (holdBlockPanel == null) {
            return;
        }
        holdBrickPreview = initializePreviewPanel(holdBlockPanel);
        updateHoldBrickPanel(holdBrickData);
    }

    private void updateHoldBrickPanel(int[][] holdBrickData) {
        updatePreviewPanel(holdBrickPreview, holdBrickData);
    }
    
    private Rectangle[][] initializePreviewPanel(GridPane panel) {
        panel.getChildren().clear();
        panel.setAlignment(javafx.geometry.Pos.CENTER);
        Rectangle[][] preview = new Rectangle[NEXT_PREVIEW_SIZE][NEXT_PREVIEW_SIZE];
        for (int row = 0; row < NEXT_PREVIEW_SIZE; row++) {
            for (int col = 0; col < NEXT_PREVIEW_SIZE; col++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                preview[row][col] = rectangle;
                panel.add(rectangle, col, row);
            }
        }
        return preview;
    }
    
    private void updatePreviewPanel(Rectangle[][] preview, int[][] brickData) {
        if (preview == null || brickData == null) {
            return;
        }
        for (int row = 0; row < NEXT_PREVIEW_SIZE; row++) {
            for (int col = 0; col < NEXT_PREVIEW_SIZE; col++) {
                int value = (row < brickData.length && col < brickData[row].length)
                        ? brickData[row][col]
                        : 0;
                setRectangleData(value, preview[row][col]);
            }
        }
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            ClearRow clearRow = downData.getClearRow();
            
            handleLineClear(clearRow);
            refreshBrick(downData.getViewData());
            
            if (clearRow.getLinesRemoved() == 0 && eventListener != null) {
                refreshGameBackground(eventListener.getBoardMatrix());
            }
        }
        gamePanel.requestFocus();
    }

    private void hardDrop() {
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            playDropSound();
            DownData downData = eventListener.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
            
            ClearRow clearRow = downData.getClearRow();
            handleLineClear(clearRow);
            
            if (clearRow.getLinesRemoved() == 0 && eventListener != null) {
                refreshGameBackground(eventListener.getBoardMatrix());
            }
            
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }
    
    private void handleLineClear(ClearRow clearRow) {
        if (clearRow.getLinesRemoved() > 0) {
            playLineClearSound();
            animateLineClear(clearRow.getClearedRowIndices());
            showScoreNotification(clearRow.getScoreBonus());
            updateLevelAndLines(clearRow.getLinesRemoved());
        }
    }
    
    private void showScoreNotification(int scoreBonus) {
        NotificationPanel notificationPanel = new NotificationPanel("+" + scoreBonus);
        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());
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
        
        javafx.animation.ParallelTransition parallel = new javafx.animation.ParallelTransition();
        
        for (Integer rowIndex : clearedRowIndices) {
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
            int newLinesTotal = linesCleared.get() + linesRemoved;
            linesCleared.setValue(newLinesTotal);
            
            if (linesLabel != null) {
                linesLabel.setText("Lines: " + newLinesTotal);
            }

            int newLevel = calculateLevel(newLinesTotal);
            int oldLevel = currentLevel.get();
            if (newLevel != oldLevel) {
                currentLevel.setValue(newLevel);
                playLevelUpSound();
                updateGameSpeed();
            }
        }
    }
    
    private int calculateLevel(int totalLinesCleared) {
        return (totalLinesCleared / LINES_PER_LEVEL) + 1;
    }

    private void updateGameSpeed() {
        if (timeLine != null) {
            timeLine.stop();
            double speedFactor = Math.max(0.2, 1.0 - (currentLevel.get() - 1) * 0.15);
            double newSpeed = BASE_GAME_SPEED * speedFactor;
            startGameTimeline(newSpeed);
        }
    }

    public void updateGameScale() {
        if (gameContainer == null || scalingContainer == null) return;

        double containerWidth = gameContainer.getWidth();
        double containerHeight = gameContainer.getHeight();

        if (containerWidth <= 0 || containerHeight <= 0) {
            containerWidth = gameContainer.getLayoutBounds().getWidth();
            containerHeight = gameContainer.getLayoutBounds().getHeight();
        }
        
        if (containerWidth <= 0 || containerHeight <= 0) return;

        double contentWidth = scalingContainer.getPrefWidth() > 0 ? scalingContainer.getPrefWidth() : BASE_GAME_WIDTH;
        double contentHeight = scalingContainer.getPrefHeight() > 0 ? scalingContainer.getPrefHeight() : BASE_GAME_HEIGHT;

        double scaleX = containerWidth / contentWidth;
        double scaleY = containerHeight / contentHeight;

        scaleFactor = Math.min(scaleX, scaleY) * 0.85;
        
        if (scaleFactor > 1.0) {
            scaleFactor = 1.0;
        }

        scalingContainer.setTranslateX(0);
        scalingContainer.setTranslateY(0);
        scalingContainer.setScaleX(scaleFactor);
        scalingContainer.setScaleY(scaleFactor);

        double scaledWidth = contentWidth * scaleFactor;
        double scaledHeight = contentHeight * scaleFactor;
        
        double offsetX = (containerWidth - scaledWidth) / 2;
        double offsetY = (containerHeight - scaledHeight) / 2;
        
        scalingContainer.setTranslateX(offsetX);
        scalingContainer.setTranslateY(offsetY);
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
        
        // Set up Play Again button action
        if (gameOverPanel != null) {
            gameOverPanel.getPlayAgainButton().setOnAction(e -> newGame(e));
        }
        
        gameOverPanel.setVisible(true);
    }

    public void newGame(ActionEvent actionEvent) {
        if (timeLine != null) {
            timeLine.stop();
        }
        gameOverPanel.setVisible(false);

        currentLevel.set(1);
        linesCleared.set(0);

        eventListener.createNewGame();
        gamePanel.requestFocus();

        startGameTimeline(BASE_GAME_SPEED);

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    @FXML
    public void resumeGame(ActionEvent actionEvent) {
        if (isGameOver.getValue() == Boolean.TRUE) {
            return;
        }
        
        boolean currentPauseState = isPause.getValue();
        isPause.setValue(!currentPauseState);
        
        if (isPause.getValue()) {
            if (timeLine != null) {
                timeLine.pause();
            }
        } else {
            if (timeLine != null) {
                timeLine.play();
            }
        }
        gamePanel.requestFocus();
    }

    @FXML
    public void openSettings(ActionEvent actionEvent) {
        openSettings(actionEvent, false);
    }
    
    private void openSettings(ActionEvent actionEvent, boolean menuAutoPaused) {
        wasPausedBeforeSettings = isPause.getValue();
        boolean shouldResumeAfterSettings = menuAutoPaused || (!wasPausedBeforeSettings);
        
        pauseGameForSettings();
        
        SettingsDialog settingsDialog = createAndConfigureSettingsDialog();
        setupSettingsDialogListeners(settingsDialog);
        
        Optional<ButtonType> result = settingsDialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            applySettingsChanges();
        }
        
        handleSettingsDialogClose(shouldResumeAfterSettings);
    }
    
    private void pauseGameForSettings() {
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            if (timeLine != null) {
                timeLine.pause();
            }
            isPause.setValue(Boolean.TRUE);
        }
    }
    
    private SettingsDialog createAndConfigureSettingsDialog() {
        SettingsDialog settingsDialog = new SettingsDialog();
        
        if (gameContainer != null && gameContainer.getScene() != null && gameContainer.getScene().getWindow() != null) {
            settingsDialog.initOwner(gameContainer.getScene().getWindow());
        }
        
        if (settingsManager != null) {
            settingsDialog.setGhostPieceEnabled(settingsManager.isGhostPieceEnabled());
            settingsDialog.setMusicEnabled(settingsManager.isMusicEnabled());
            settingsDialog.setMusicVolume(settingsManager.getMusicVolume());
            settingsDialog.setBackgroundPictureEnabled(settingsManager.isBackgroundPictureEnabled());
        }
        
        return settingsDialog;
    }
    
    private void setupSettingsDialogListeners(SettingsDialog settingsDialog) {
        settingsDialog.setVolumeChangeListener(this::handleVolumeChange);
        settingsDialog.setMusicEnabledChangeListener(this::handleMusicEnabledChange);
        settingsDialog.setGhostPieceChangeListener(this::handleGhostPieceChange);
        settingsDialog.setBackgroundPictureChangeListener(this::handleBackgroundPictureChange);
    }
    
    private void handleVolumeChange(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
        if (settingsManager != null) {
            settingsManager.setMusicVolume(volume);
        }
    }
    
    private void handleMusicEnabledChange(boolean enabled) {
        if (mediaPlayer != null) {
            if (enabled) {
                if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                    mediaPlayer.play();
                }
            } else {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                }
            }
        }
        if (settingsManager != null) {
            settingsManager.setMusicEnabled(enabled);
        }
    }
    
    private void handleGhostPieceChange(boolean enabled) {
        if (settingsManager != null) {
            settingsManager.setGhostPieceEnabled(enabled);
        }
        if (ghostPanel != null) {
            ghostPanel.setVisible(enabled);
        }
    }
    
    private void handleBackgroundPictureChange(boolean enabled) {
        if (settingsManager != null) {
            settingsManager.setBackgroundPictureEnabled(enabled);
        }
        if (gameAreaBackgroundImageView != null) {
            gameAreaBackgroundImageView.setVisible(enabled);
        }
    }
    
    private void applySettingsChanges() {
        if (settingsManager != null) {
            if (ghostPanel != null) {
                ghostPanel.setVisible(settingsManager.isGhostPieceEnabled());
            }
            if (gameAreaBackgroundImageView != null) {
                gameAreaBackgroundImageView.setVisible(settingsManager.isBackgroundPictureEnabled());
            }
            updateMusicSettings();
        }
    }
    
    private void handleSettingsDialogClose(boolean shouldResumeAfterSettings) {
        if (shouldResumeAfterSettings && isPause.getValue() == Boolean.TRUE && isGameOver.getValue() == Boolean.FALSE) {
            isPause.setValue(Boolean.FALSE);
            if (timeLine != null) {
                timeLine.play();
            }
        }
        
        if (gamePanel != null && scalingContainer != null && scalingContainer.isVisible()) {
            gamePanel.requestFocus();
        }
    }

    private void updateBrickPosition(ViewData brick) {
        if (brickPanel != null) {
            Position position = calculatePosition(brick.getxPosition(), brick.getyPosition());
            brickPanel.setLayoutX(position.x);
            brickPanel.setLayoutY(position.y);
        }
    }

    private void updateGhostPosition(ViewData brick) {
        if (ghostPanel != null && ghostRectangles != null) {
            Position position = calculatePosition(brick.getGhostXPosition(), brick.getGhostYPosition());
            ghostPanel.setLayoutX(position.x);
            ghostPanel.setLayoutY(position.y);
            
            updateGhostRectangles(brick.getBrickData());
        }
    }
    
    private Position calculatePosition(int gridX, int gridY) {
        double originX = gamePanel.getLayoutX();
        double originY = gamePanel.getLayoutY();
        double cellWidth = BRICK_SIZE + gamePanel.getHgap();
        double cellHeight = BRICK_SIZE + gamePanel.getVgap();
        double xPos = originX + (gridX * cellWidth);
        double yPos = originY + ((gridY - HIDDEN_ROWS) * cellHeight);
        return new Position(xPos, yPos);
    }
    
    private void updateGhostRectangles(int[][] brickData) {
        for (int i = 0; i < brickData.length && i < ghostRectangles.length; i++) {
            for (int j = 0; j < brickData[i].length && j < ghostRectangles[i].length; j++) {
                if (ghostRectangles[i][j] != null) {
                    if (brickData[i][j] != 0) {
                        ghostRectangles[i][j].setFill(getFillColor(brickData[i][j]));
                        ghostRectangles[i][j].setOpacity(0.3);
                        ghostRectangles[i][j].setVisible(true);
                    } else {
                        ghostRectangles[i][j].setVisible(false);
                    }
                }
            }
        }
    }
    
    // Helper class for position calculation
    private static class Position {
        final double x;
        final double y;
        
        Position(double x, double y) {
            this.x = x;
            this.y = y;
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
        
        if (keybindsContainer != null) {
            keybindsContainer.setVisible(true);
        }
        positionKeybinds();
        
        if (timeLine != null) {
            timeLine.stop();
        }
        
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(false);
        }
        
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        
        if (eventListener == null) {
            new GameController(this);
        } else {
            eventListener.createNewGame();
            
            currentLevel.set(1);
            linesCleared.set(0);
            
            startGameTimeline(BASE_GAME_SPEED);
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
    
    // Key event handling
    private void handleKeyPress(KeyEvent keyEvent) {
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            handleGameKeyPress(keyEvent);
        }
        if (keyEvent.getCode() == KeyCode.N) {
            newGame(null);
        }
    }
    
    private void handleGameKeyPress(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        
        if (code == KeyCode.LEFT || code == KeyCode.A) {
            refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
            keyEvent.consume();
        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
            refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
            keyEvent.consume();
        } else if (code == KeyCode.UP || code == KeyCode.W) {
            refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
            keyEvent.consume();
        } else if (code == KeyCode.DOWN || code == KeyCode.S) {
            moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        } else if (code == KeyCode.SPACE) {
            hardDrop();
            keyEvent.consume();
        } else if (code == KeyCode.C || code == KeyCode.SHIFT) {
            holdCurrentBrick();
            keyEvent.consume();
        }
    }
    
    // Helper methods for UI components
    private Label createKeybindKeyLabel(String text) {
        Label label = new Label(text);
        label.setStyle(KEYBIND_KEY_STYLE);
        label.setPrefWidth(70);
        label.setMinWidth(70);
        label.setMaxWidth(70);
        return label;
    }
    
    private Label createKeybindActionLabel(String text) {
        Label label = new Label(text);
        label.setStyle(KEYBIND_ACTION_STYLE);
        return label;
    }
    
    private Button createMenuButton(String text, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        button.setPrefWidth(100);
        button.setPrefHeight(30);
        button.setStyle(BUTTON_BASE_STYLE);
        button.setOnAction(action);
        button.setOnMouseEntered(e -> button.setStyle(BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(BUTTON_BASE_STYLE));
        return button;
    }
    
    private void handleMainMenuAction() {
        if (timeLine != null) {
            timeLine.stop();
        }
        
        if (scalingContainer != null) {
            scalingContainer.setVisible(false);
        }
        if (startScreen != null) {
            startScreen.setVisible(true);
        }
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(false);
        }
        if (keybindsContainer != null) {
            keybindsContainer.setVisible(false);
        }
    }
    
    // Timeline management
    private void startGameTimeline(double speed) {
        Duration duration = Duration.millis(speed);
        timeLine = new Timeline(new KeyFrame(
                duration,
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }
}
