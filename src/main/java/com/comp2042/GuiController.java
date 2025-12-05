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
            LinearGradient gameGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0.0, Color.rgb(91, 160, 242)),
                new Stop(0.3, Color.rgb(74, 144, 226)),
                new Stop(0.7, Color.rgb(53, 122, 189)),
                new Stop(1.0, Color.rgb(42, 95, 143))
            );
            gameBorder.setStroke(gameGradient);
            gameBorder.setStrokeWidth(4);
            
            DropShadow gameShadow = new DropShadow();
            gameShadow.setColor(Color.rgb(74, 144, 226, 0.6));
            gameShadow.setRadius(10);
            gameShadow.setOffsetX(0);
            gameShadow.setOffsetY(0);
            gameBorder.setEffect(gameShadow);
        }
        
        if (hudBorder != null) {
            LinearGradient hudGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0.0, Color.rgb(91, 160, 242)),
                new Stop(0.3, Color.rgb(74, 144, 226)),
                new Stop(0.7, Color.rgb(53, 122, 189)),
                new Stop(1.0, Color.rgb(42, 95, 143))
            );
            hudBorder.setStroke(hudGradient);
            hudBorder.setStrokeWidth(4);
            
            DropShadow hudShadow = new DropShadow();
            hudShadow.setColor(Color.rgb(74, 144, 226, 0.6));
            hudShadow.setRadius(10);
            hudShadow.setOffsetX(0);
            hudShadow.setOffsetY(0);
            hudBorder.setEffect(hudShadow);
        }
        
        if (gameBorderOuter != null) {
            LinearGradient outerGameGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0.0, Color.rgb(123, 179, 255)),
                new Stop(0.5, Color.rgb(107, 163, 240)),
                new Stop(1.0, Color.rgb(74, 144, 226))
            );
            gameBorderOuter.setStroke(outerGameGradient);
            gameBorderOuter.setStrokeWidth(2);
            
            DropShadow outerGameShadow = new DropShadow();
            outerGameShadow.setColor(Color.rgb(107, 163, 240, 0.4));
            outerGameShadow.setRadius(6);
            outerGameShadow.setOffsetX(0);
            outerGameShadow.setOffsetY(0);
            gameBorderOuter.setEffect(outerGameShadow);
        }
        
        if (hudBorderOuter != null) {
            LinearGradient outerHudGradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0.0, Color.rgb(123, 179, 255)),
                new Stop(0.5, Color.rgb(107, 163, 240)),
                new Stop(1.0, Color.rgb(74, 144, 226))
            );
            hudBorderOuter.setStroke(outerHudGradient);
            hudBorderOuter.setStrokeWidth(2);
            
            DropShadow outerHudShadow = new DropShadow();
            outerHudShadow.setColor(Color.rgb(107, 163, 240, 0.4));
            outerHudShadow.setRadius(6);
            outerHudShadow.setOffsetX(0);
            outerHudShadow.setOffsetY(0);
            hudBorderOuter.setEffect(outerHudShadow);
        }
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
            Label keyLabel = new Label(keybind[0]);
            keyLabel.setStyle(
                "-fx-font-size: 11px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #FFD700; " +
                "-fx-background-color: rgba(255, 215, 0, 0.15); " +
                "-fx-background-radius: 4px; " +
                "-fx-border-color: #FFD700; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 4px; " +
                "-fx-padding: 4px 8px; " +
                "-fx-alignment: center;"
            );
            keyLabel.setPrefWidth(70);
            keyLabel.setMinWidth(70);
            keyLabel.setMaxWidth(70);
            
            Label actionLabel = new Label(keybind[1]);
            actionLabel.setStyle(
                "-fx-font-size: 11px; " +
                "-fx-font-weight: normal; " +
                "-fx-text-fill: #e0e0e0; " +
                "-fx-padding: 4px 0;"
            );
            
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
    
    private void initializeMenu() {
        menuPopup = new Popup();
        menuPopup.setAutoHide(true);
        
        VBox menuContainer = new VBox(2);
        menuContainer.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #4a4a4a; -fx-border-width: 1px; -fx-padding: 4px;");
        
        mainMenuButton = new Button("Main Menu");
        mainMenuButton.setPrefWidth(100);
        mainMenuButton.setPrefHeight(30);
        mainMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;");
        mainMenuButton.setOnAction(e -> {
            menuActionTaken = true;
            menuPopup.hide();
            
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
        });
        mainMenuButton.setOnMouseEntered(e -> mainMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #5a5a5a;"));
        mainMenuButton.setOnMouseExited(e -> mainMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;"));
        
        settingsMenuButton = new Button("Settings");
        settingsMenuButton.setPrefWidth(100);
        settingsMenuButton.setPrefHeight(30);
        settingsMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;");
        settingsMenuButton.setOnAction(e -> {
            menuActionTaken = true;
            boolean menuAutoPaused = !wasPausedBeforeMenu;
            menuPopup.hide();
            openSettings(e, menuAutoPaused);
        });
        settingsMenuButton.setOnMouseEntered(e -> settingsMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #5a5a5a;"));
        settingsMenuButton.setOnMouseExited(e -> settingsMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;"));
        
        exitMenuButton = new Button("Exit");
        exitMenuButton.setPrefWidth(100);
        exitMenuButton.setPrefHeight(30);
        exitMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;");
        exitMenuButton.setOnAction(e -> {
            menuActionTaken = true;
            menuPopup.hide();
            exitGame(e);
        });
        exitMenuButton.setOnMouseEntered(e -> exitMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #5a5a5a;"));
        exitMenuButton.setOnMouseExited(e -> exitMenuButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #4a4a4a;"));
        
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
            
            if (clearRow.getLinesRemoved() > 0) {
                animateLineClear(clearRow.getClearedRowIndices());
                
                NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());

                updateLevelAndLines(clearRow.getLinesRemoved());
            }
            
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
            if (clearRow.getLinesRemoved() > 0) {
                animateLineClear(clearRow.getClearedRowIndices());
                
                NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());

                updateLevelAndLines(clearRow.getLinesRemoved());
            } else {
                if (eventListener != null) {
                    refreshGameBackground(eventListener.getBoardMatrix());
                }
            }
            
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

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(BASE_GAME_SPEED),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

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
        
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            if (timeLine != null) {
                timeLine.pause();
            }
            isPause.setValue(Boolean.TRUE);
        }
        
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
        
        settingsDialog.setVolumeChangeListener(volume -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volume);
            }
            if (settingsManager != null) {
                settingsManager.setMusicVolume(volume);
            }
        });
        
        settingsDialog.setMusicEnabledChangeListener(enabled -> {
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
        });
        
        settingsDialog.setGhostPieceChangeListener(enabled -> {
            if (settingsManager != null) {
                settingsManager.setGhostPieceEnabled(enabled);
            }
            if (ghostPanel != null) {
                ghostPanel.setVisible(enabled);
            }
        });
        
        settingsDialog.setBackgroundPictureChangeListener(enabled -> {
            if (settingsManager != null) {
                settingsManager.setBackgroundPictureEnabled(enabled);
            }
            if (gameAreaBackgroundImageView != null) {
                gameAreaBackgroundImageView.setVisible(enabled);
            }
        });
        
        Optional<ButtonType> result = settingsDialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
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
        
        // Auto-resume if we should (either menu auto-paused or we auto-paused in settings)
        if (shouldResumeAfterSettings && isPause.getValue() == Boolean.TRUE && isGameOver.getValue() == Boolean.FALSE) {
            isPause.setValue(Boolean.FALSE);
            if (timeLine != null) {
                timeLine.play();
            }
        }
        
        // Request focus back to game panel if game is running, otherwise focus stays on start screen
        if (gamePanel != null && scalingContainer != null && scalingContainer.isVisible()) {
            gamePanel.requestFocus();
        }
    }

    private void updateBrickPosition(ViewData brick) {
        if (brickPanel != null) {
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
            double originX = gamePanel.getLayoutX();
            double originY = gamePanel.getLayoutY();
            double cellWidth = BRICK_SIZE + gamePanel.getHgap();
            double cellHeight = BRICK_SIZE + gamePanel.getVgap();
            double xPos = originX + (brick.getGhostXPosition() * cellWidth);
            double yPos = originY + ((brick.getGhostYPosition() - HIDDEN_ROWS) * cellHeight);

            ghostPanel.setLayoutX(xPos);
            ghostPanel.setLayoutY(yPos);
            
            int[][] brickData = brick.getBrickData();
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
            
            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(BASE_GAME_SPEED),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
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
