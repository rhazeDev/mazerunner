# Maze Runner Game

A 2D maze game where players navigate through a procedurally generated maze, collect items, and avoid monsters to reach the exit door.

## Table of Contents
- [Features](#features)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Importing the Project in Eclipse](#importing-the-project-in-eclipse)
- [How to Play](#how-to-play)
- [Game Elements](#game-elements)
- [Difficulty Levels](#difficulty-levels)
- [Project Structure](#project-structure)
- [Credits](#credits)

## Features

- Procedurally generated mazes
- Multiple difficulty levels (Easy, Medium, Hard)
- User authentication system
- Global leaderboard tracking best completion times
- Sound effects and background music
- In-game items:
  - Keys to unlock the exit door
  - Portals for teleportation
  - Bullets to shoot monsters
  - Bombs to create new paths

## System Requirements

- Java JDK 11 or higher
- MySQL Server 5.7 or higher
- Eclipse IDE for Java Developers (recommended) or any Java IDE

## Installation

1. Clone or download this repository
2. Set up the database (instructions in [Database Setup](#database-setup))
3. Import the project into Eclipse (instructions in [Importing the Project in Eclipse](#importing-the-project-in-eclipse))
4. Run the project from Eclipse

## Database Setup

1. Install MySQL Server if you don't already have it
2. Create a new database named `dbmaze`
3. Execute the following SQL script to create the necessary tables:

```sql
CREATE DATABASE IF NOT EXISTS dbmaze;
USE dbmaze;

CREATE TABLE IF NOT EXISTS tblLeaderboard (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    time_finished DOUBLE NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

4. Make sure MySQL is running on port 3306
5. Default database connection settings in the game:
   - User: `root`
   - Password: `` (empty)
   - Database: `dbmaze`
   - Host: `localhost`
   - Port: `3306`

If you need to change these settings, modify the connection parameters in `src/Database/Database.java`:

```java
private static final String url = "jdbc:mysql://localhost:3306/dbmaze";
private static final String user = "root";
private static final String pass = "";
```

## Importing the Project in Eclipse

1. Open Eclipse IDE
2. Select File > Import
3. Choose "Existing Projects into Workspace" under General
4. Select the root directory where you cloned/downloaded the project
5. Click Finish

### Adding MySQL Connector JAR File

1. Right-click on the project in Eclipse's Project Explorer
2. Select Properties
3. Click on "Java Build Path" in the left panel
4. Go to the "Libraries" tab
5. Click "Add External JARs..."
6. Navigate to where the `mysql-connector-java-8.0.13.jar` file is located (it's included in the project root directory)
7. Click Open and then Apply and Close

## How to Play

1. Launch the application
2. Enter your username in the login screen
3. From the main menu, click "Start Game"
4. Select a difficulty level
5. Use the following controls to navigate through the maze:
   - Arrow keys or WASD: Move the player
   - Spacebar: Shoot bullets (if you have them)
   - P: Pause the game
   - ESC: Return to the main menu

## Game Elements

- **Player**: The character you control
- **Wall**: Obstacles that block your path
- **Key**: Collect to unlock the exit door
- **Door**: The exit, requires a key to open
- **Monsters**: Enemies that will end your game if they touch you
- **Bullets**: Collect and use to eliminate monsters
- **Portals**: Teleport you to another location in the maze
- **Bombs**: Create new paths by destroying walls

## Difficulty Levels

- **Easy**: Smaller maze, fewer monsters, more bullets and bombs
- **Medium**: Moderate maze size, balanced number of monsters and items
- **Hard**: Large maze, more monsters, fewer items

## Project Structure

The project is organized into several packages:

- `mazerunner`: Contains the main game logic and UI components
- `Database`: Database connection and user data management
- `Dialogz`: UI dialogs for instructions, login, leaderboard, etc.

Key files:
- `MazeRunner.java`: Main application class
- `GamePanel.java`: Game rendering and logic
- `MazeGenerator.java`: Procedural maze generation
- `SoundManager.java`: Audio system
- `Database.java`: Database operations

## Credits

- Game Development: Justine Agcanas
- Sound Effects: Various sources
- Graphics: Custom-made and open source resources
- Markdown file: Made using copilot

## License

This project is licensed under the MIT License - see the LICENSE file for details.
