package model;

import exception.UnauthorizeMoveException;

public interface IGame {
    char [][] cheatBoard();
    GameStatus playGame(int x, int y, GameCommands gameCommands) throws UnauthorizeMoveException;
    char [][] getBoard();
    boolean isGameStarted();
}
