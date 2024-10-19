package model;

import exception.UnauthorizeMoveException;

public interface IGame {
    /**
     * Method to create the board and the cheat board.
     * @return the cheat board as a 2D array of characters
     */
    char [][] cheatBoard();

    /**
     * Method to play the game. It will play the game on the coordinates x,y with the command gameCommands.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param gameCommands the command to execute
     * @return the status of the game after the command is executed.
     */
    GameStatus playGame(int x, int y, GameCommands gameCommands) throws UnauthorizeMoveException;

    /**
     * Method to get the board.
     * @return the board as a 2D array of characters
     */
    char [][] getBoard();

    /**
     * Method to check if the game is started.
     * @return true if the game is started, false otherwise.
     */
    boolean isGameStarted();

}
