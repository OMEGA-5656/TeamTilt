# TeamTilt

TeamTilt is a 2D platformer built with [libGDX](https://libgdx.com/). You guide a character across floating platforms using touch/keyboard controls, with responsive physics powered by Box2D. The game features a main menu, a two-step level selection flow (Worlds → Levels), a pause/sidebar menu in-game, and a modular level system that defines platform layouts per level.

Key features:
- Worlds screen with 4 worlds, each containing 6 levels (3×2 grid)
- Per-level platform layouts via pluggable `LevelDefinition` classes (24 levels scaffolded)
- Box2D movement and jumping with consistent spawn/respawn behavior
- In-game translucent pause/resume icon and slide-in sidebar with Quit
- Asset-backed main menu with dynamic fonts and styled UI

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

## Run Instructions

Desktop (LWJGL3):
- Windows: `./gradlew.bat lwjgl3:run`
- macOS/Linux: `./gradlew lwjgl3:run`

Build runnable JAR:
- `./gradlew lwjgl3:jar` → output at `lwjgl3/build/libs/TeamTilt-<version>.jar`

Android:
- Ensure Android SDK is installed and `local.properties` points to it
- Assemble: `./gradlew android:assembleDebug`
- Install/Run on device: `./gradlew android:installDebug` then launch from the app drawer

## Controls

- Desktop: Arrow keys or A/D to move, Space to jump (if mapped); UI buttons on screen for touch
- Mobile: On-screen left/right/jump buttons; pause icon at top-left

## Project Structure & Navigation

- `core/src/main/java/com/newgame/teamtilt/TeamTiltMain.java`: entry `Game`
- Screens:
  - `MainMenuScreen` → tap to start → `WorldsScreen`
  - `WorldsScreen` (4 worlds) → `LevelsScreen` (3×2 levels)
  - `GameScreen` plays the selected level, supports pause/resume and quit
- Levels:
  - Interface: `levels/LevelDefinition`
  - Factory: `levels/LevelFactory`
  - Implementations: `levels/worlds{1..4}/World{N}Level{1..6}`
  - Current default layout lives in `World1Level1`

## Extending Levels

Each level implements:
```java
void build(World world, Array<Platform> platforms)
```
Add platforms using the provided `Platform` wrapper. Spawn and respawn behavior are handled uniformly in `GameScreen`.

To launch a specific level from UI, `LevelsScreen` uses:
```java
LevelDefinition def = LevelFactory.getLevel(worldIndex, levelIndex);
game.setScreen(new GameScreen(game, def));
```

## Assets

Place art, fonts, and sounds under `assets/`. The build generates an `assets.txt` manifest at compile time for reference.

## License

This project includes third-party components under their respective licenses. LWJGL3 helper for macOS startup is under Apache-2.0 (see header in `StartupHelper.java`).
