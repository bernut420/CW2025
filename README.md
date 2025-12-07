# Tetris Game - JavaFX Implementation

## GitHub Repository
[Repository on GitHub](https://github.com/bernut420/CW2025)

---

## Compilation Instructions

### Prerequisites
- **Java Development Kit (JDK) 23** or higher
- **JavaFX SDK 25.0.1** (automatically downloaded via Maven)

### Step-by-Step Compilation

####  Using IDE (IntelliJ IDEA / Eclipse)
1. **Import the project** as from the GitHub repository.
2. **Wait for Maven dependencies** to download automatically
3. click the **"Main"** button on the top right corner of the screen.
4. Tap on **"Edit Configurations"**
5. **JavaFX SDK:** Ensure it is correctly linked under Run configuration by pasting:
   ```
     --module-path "path/to/javafx-sdk-25.0.1/lib" --add-modules javafx.controls,javafx.fxml,javafx.media
   ```
6. **Run the `Main` class** located at `src/main/java/com/comp2042/Main.java`

###  Compiling using Maven Wrapper

1. **Clone or download the project from the above GitHub repository.**

2. **Compile the project:**
   ```bash
   # Windows
   mvnw.cmd clean compile
   
   # Linux/Mac
   ./mvnw clean compile
   ```

3. **Run the application:**
   ```bash
   # Windows
   mvnw.cmd javafx:run
   
   # Linux/Mac
   ./mvnw javafx:run
   ```

## Implemented and Working Properly

### Core Game Features

   **Level System
   - For every 15 lines cleared, the speed of bricks falling increases

   **Brick Movement and Rotation**
   - Ghost piece preview showing where the brick will land

   **Row Clearing**
   - Score calculation based on number of lines cleared
   - Visual and audio feedback for cleared rows

   **Score System**
   - Points awarded for:
     - Manual downward movement (1 point per cell)
     - Hard drop (2 points per cell dropped)
     - Line clears
   - High score persistence to file

   **Hold Feature**
   - Hold current piece for later use
   - Swap between held and current piece
   - One hold per piece placement (prevents abuse)
   - Visual hold panel display

   **Next Piece Preview**
   - Shows the next piece that will appear
   - Preview panel with proper scaling
   - Uses bag-based random generation for fair distribution

   **Game Over Detection**
   - Detects when new piece cannot be placed
   - Game over panel with "Play Again" option
   - Prevents further gameplay after game over

   **Lock Delay System**
   - 500ms delay before piece locks permanently
   - Allows last-second adjustments when piece touches bottom
   - Timer resets on any movement or rotation

### User Interface Features
   **Modern UI Design**
   - Custom styled borders with gradient effects
   - Fullscreen mode support
   - Start screen and play area with background image
   - Game over overlay panel

   **Settings System**
    - Ghost piece toggle (show/hide ghost piece)
    - Music enable/disable
    - Volume control slider (0-100%)
    - Background picture toggle
    - Settings persist to user's home directory

   **Visual Feedback**
    - Score notifications with fade-out animation
    - Real-time score, high score, level, and lines display
    - Keybind display showing controls
    - Smooth animations for piece placement
    - Line clear has a flash animation

   **Audio System**
    - Background music with loop support
    - Sound effects for:
      - Piece drop
      - Line clear
      - Level up
    - Volume control integration

   **Menu System**
    - In-game menu accessible via button
    - Options: Main Menu, Settings, Exit
    - Proper state management

   **Testing**
    - Comprehensive JUnit 5 test suite
    - Tests for matrix operations, score, brick rotation, and data classes
    - Test coverage for core game logic

---

## Implemented but Not Working Properly

Features implemented are working as intended.

---

## Features Not Implemented

### Level Progression System
- **Reason**: Level calculation logic exists but level progression and speed increase are not fully implemented
- **Impact**: Game difficulty does not increase over time
- **Location**: `GuiController.java` has `LINES_PER_LEVEL` constant but level advancement logic is incomplete
  
### Multiplayer Mode
- **Reason**: Out of scope for this assignment
- **Impact**: Single-player only

### Save/Load Game State
- **Reason**: Not required for the assignment
- **Impact**: Game state is not persisted between sessions

### Customizable Controls
- **Reason**: Keybinds are hardcoded
- **Impact**: Players cannot remap controls
- **Note**: Keybind display exists but remapping functionality is not implemented

### Statistics Tracking
- **Reason**: Beyond basic requirements
- **Impact**: No detailed statistics (pieces placed, average lines per game, etc.)

---

## New Java Classes

   **`NotificationPanel`**
    - **Purpose**: Temporary notification display for score bonuses
    - **Key Features**: Fade-out animation, automatic removal

  **`SettingsDialog`**
    - **Purpose**: Dialog window for game settings configuration
    - **Key Features**: Ghost piece toggle, music controls, volume slider, background picture toggle

  **`HighScoreManager`** 
    - **Purpose**: Manages high score persistence to file
    - **Key Features**: File I/O, high score tracking

  **`SettingsManager`**
    - **Purpose**: Manages game settings persistence
    - **Key Features**: Properties file I/O, settings loading/saving
      
   **`Score`** 
    - **Purpose**: Manages game score with JavaFX property binding
    - **Key Features**: Reactive score updates, reset functionality

   **`RandomBrickGenerator`**
    - **Purpose**: Implements bag-based random brick generation
    - **Key Features**: Fair distribution using shuffle algorithm, next brick preview

---

## Modified Java Classes

### **`GuiController`**

  - **Settings Integration**: Added `SettingsManager` and `HighScoreManager` integration for persistent settings and high scores
     - **Ghost Piece Rendering**: Implemented ghost piece preview panel showing where the current piece will land
     - **Hold Panel**: Added hold block panel display for the held piece
     - **Next Piece Preview**: Enhanced next block panel with proper scaling and rendering
     - **Audio System**: Integrated background music and sound effects with volume control
     - **Menu System**: Added in-game menu with popup containing Main Menu, Settings, and Exit options
     - **Settings Dialog Integration**: Connected settings dialog for ghost piece, music, volume, and background picture toggles
     - **Dynamic Scaling**: Implemented `updateGameScale()` method for responsive layout on different screen sizes
     - **Border Styling**: Added custom gradient borders for game area and HUD with multiple color stops
     - **Keybind Display**: Created keybind table showing all game controls with styled key indicators
     - **Background Images**: Added support for start screen and game area background images
     - **Notification System**: Implemented score notification panel with fade-out animations
     - **Game Over Integration**: Enhanced game over handling with high score checking and play again functionality
     - **High Score Display**: Added real-time high score label binding
     - **Level and Lines Display**: Added level and lines cleared labels with property binding
     - **Pause State Management**: Added pause/resume functionality with proper timeline control
     - **Font Loading**: Added custom font loading with null safety checks
   - **Reason**: Transformed basic UI controller into a comprehensive game interface with modern features, settings persistence, and enhanced user experience

###  **`Main`**
   - **Original Purpose**: Application entry point
   - **Modifications**:
     - Added fullscreen mode initialization
     - Added window resize listeners for dynamic scaling
     - Added nested `Platform.runLater()` calls for proper initialization timing
   - **Reason**: Ensures proper UI scaling and responsive layout on different screen sizes

###  **`Board`** 
   - **Original Purpose**: Board interface definition
   - **Modifications**: 
     - Added `holdCurrentBrick()` method for hold functionality
     - Added `getViewData()` method for comprehensive view information
   - **Reason**: Extended interface to support new features (hold, ghost piece)

###  **`InputEventListener`**
   - **Original Purpose**: Event handling interface
   - **Modifications**:
     - Added `onHardDropEvent()` method
     - Added `onHoldEvent()` method
     - Modified return types to include `DownData` for down events
   - **Reason**: Extended to support hard drop and hold features

###  **`Brick`** 
   - **Original Purpose**: Brick interface definition
   - **Modifications**: 
     - Method signatures remain the same, but implementation details enhanced
   - **Reason**: Maintained interface compatibility while improving implementations

###  **`BrickGenerator`**
   - **Original Purpose**: Brick generation interface
   - **Modifications**: 
     - Added `getNextBrick()` method for preview functionality
   - **Reason**: Enables next piece preview feature

---

## Unexpected Problems

### JavaFX Module System Compatibility
- **Problem**: JavaFX 25.0.1 requires proper module path configuration, which can be complex in some IDEs
- **Solution**: Used Maven with JavaFX plugin to handle module configuration automatically
- **Impact**: Required careful dependency management and build configuration
 
### Lock Delay Timing Issues
- **Problem**: Brick locking mechanism needed precise timing to allow last-second adjustments
- **Solution**: Implemented lock delay system with timer tracking and reset on movement
- **Impact**: Improved gameplay feel, allowing players to make final adjustments

### Maven Build Configuration
- **Problem**: JavaFX requires special Maven plugin configuration for proper execution
- **Solution**: Used `javafx-maven-plugin` with proper module configuration
- **Impact**: Simplified build process but required specific plugin version compatibility


