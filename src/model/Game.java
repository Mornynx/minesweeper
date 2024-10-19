package model;

import config.MinesweeperConfig;
import exception.UnauthorizeMoveException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lawal Benjamin
 * This class represents the game. It will handle the game logic.
 */
public class Game implements IGame {
    private char [][] board;
    private char [][] cheatBoard;
    private GameStatus gameStatus;

    public Game(){
        gameStatus = GameStatus.NOT_STARTED;
        createBoard();
    }

    @Override
    public char[][] cheatBoard() {
        return cheatBoard;
    }


    @Override
    public GameStatus playGame(int x, int y, GameCommands gameCommands) throws UnauthorizeMoveException {
        if(!isCellValid(x,y)){
            throw new UnauthorizeMoveException("The cell is not valid.");
        }
        if(this.gameStatus == GameStatus.NOT_STARTED){
            fillMines(getPossibleMineCoordinates(), x, y);
            this.gameStatus = GameStatus.STARTED;
        }
        switch (gameCommands){
            case TRY:
                tryPlay(x,y);
                break;
            case FLAG:
                flagCell(x,y);
                break;
            default:
                break;
        }
        return this.gameStatus;
    }

    @Override
    public char[][] getBoard() {
        return board;
    }

    @Override
    public boolean isGameStarted() {
        return this.gameStatus == GameStatus.STARTED;
    }


    /**
     * Method to create the board and the cheat board.
     * The board is the board that the player will see.
     * It will fill the board with empty characters and the cheat board with zeros.
     */
    private void createBoard(){
        board = new char[MinesweeperConfig.BOARD_SIZE][MinesweeperConfig.BOARD_SIZE];
        cheatBoard = new char[MinesweeperConfig.BOARD_SIZE][MinesweeperConfig.BOARD_SIZE];
        for(int i = 0; i < MinesweeperConfig.BOARD_SIZE; i++){
            for(int j = 0; j < MinesweeperConfig.BOARD_SIZE; j++){
                board[i][j] = MinesweeperConfig.EMPTY_CHAR;
                cheatBoard[i][j] = '0';
            }
        }
    }


    /**
     * Method to fill the board with mines. It will randomly place the mines on the board.
     * The number of mines is defined in the configuration file.
     * @see MinesweeperConfig
     */
    private void fillMines(List<int[]> possibleCoordinates, int x, int y){
        for(int i = 0; i < MinesweeperConfig.NUMBER_OF_MINES; i++){
            int randomIndex = (int) (Math.random() * possibleCoordinates.size());
            int [] coordinates = possibleCoordinates.get(randomIndex);
            if(coordinates[0] == x && coordinates[1] == y){
                i--;
                continue;
            }
            cheatBoard[coordinates[0]][coordinates[1]] = MinesweeperConfig.BOMB_CHAR;
            updateCase(coordinates[0], coordinates[1]);
            possibleCoordinates.remove(randomIndex);//Remove so that the mine is not placed twice
        }
    }

    /**
     * Method to update the case around the mine. It will increment the number of mines around the case.
     * @param x the x coordinate of the mine
     * @param y the y coordinate of the mine
     */
    private void updateCase(int x, int y){
        for(int i = x-1; i <= x+1; i++){
            for(int j = y-1; j <= y+1; j++){
                if(i >= 0 && i < MinesweeperConfig.BOARD_SIZE && j >= 0 && j < MinesweeperConfig.BOARD_SIZE){
                    if(cheatBoard[i][j] != MinesweeperConfig.BOMB_CHAR){
                        cheatBoard[i][j] = (char) (cheatBoard[i][j] + 1);
                    }
                }
            }
        }
    }

    /**
     * Method to get all the possible coordinates on the board.
     * @return a list of all the possible coordinates on the board.
     */
    private List<int[]> getPossibleMineCoordinates(){
        List<int[]> possibleCoordinates = new ArrayList<>(MinesweeperConfig.BOARD_SIZE* MinesweeperConfig.BOARD_SIZE);
        for(int i = 0; i < MinesweeperConfig.BOARD_SIZE; i++){
            for(int j = 0; j < MinesweeperConfig.BOARD_SIZE; j++){
                int [] coordinates = new int[2];
                coordinates[0] = i;
                coordinates[1] = j;
                possibleCoordinates.add(coordinates);
            }
        }
        return possibleCoordinates;
    }


    /**
     * Method to check if the coordinates x,y are valid for the board.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the cell is valid, false otherwise.
     */
    private boolean isCellValid(int x, int y){
        return x >= 0 && x < MinesweeperConfig.BOARD_SIZE && y >= 0 && y < MinesweeperConfig.BOARD_SIZE;
    }

    /**
     * Method for the try command. It will try to play on the cell x,y.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private void tryPlay(int x, int y){
        if(cheatBoard[x][y] == MinesweeperConfig.BOMB_CHAR){
            this.gameStatus = GameStatus.GAME_OVER;
        }else if(cheatBoard[x][y] == '0') {
            revealAdjacentCellsRecursive(x, y);
        }else{
            updateBoard(x,y,cheatBoard[x][y]);
        }
        isTheGameFinished();
    }

    /**
     * Method to reveal the adjacent cells of the cell x,y when the cell is empty.
     * @param x the x coordinate of the cell played
     * @param y the y coordinate of the cell played
     */
    private void revealAdjacentCellsRecursive(int x, int y) {
        if (x < 0 || x >= MinesweeperConfig.BOARD_SIZE || y < 0 || y >= MinesweeperConfig.BOARD_SIZE || board[x][y] != MinesweeperConfig.EMPTY_CHAR) {
            return;
        }
        if (cheatBoard[x][y] == MinesweeperConfig.BOMB_CHAR) {
            return;
        }
        board[x][y] = cheatBoard[x][y];
        if (cheatBoard[x][y] != '0') {
            return;
        }
        revealAdjacentCellsRecursive(x - 1, y);
        revealAdjacentCellsRecursive(x + 1, y);
        revealAdjacentCellsRecursive(x, y - 1);
        revealAdjacentCellsRecursive(x, y + 1);
        revealAdjacentCellsRecursive(x - 1, y - 1);
        revealAdjacentCellsRecursive(x - 1, y + 1);
        revealAdjacentCellsRecursive(x + 1, y - 1);
        revealAdjacentCellsRecursive(x + 1, y + 1);
    }


    /**
     * Method to update the board on coordinates x,y with the char toInsert
     * @param x the x coordinate
     * @param y the y coordinate
     * @param toInsert the char to insert into the board.
     */
    private void updateBoard(int x, int y, char toInsert){
        if(board[x][y] == MinesweeperConfig.EMPTY_CHAR || board[x][y] == MinesweeperConfig.FLAG_CHAR){
            board[x][y] = toInsert;
        }
    }

    /**
     * Method to flag a cell. If the cell is already flagged, it will unflag it.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private void flagCell(int x, int y){
        if(board[x][y] == MinesweeperConfig.FLAG_CHAR){
            board[x][y] = MinesweeperConfig.EMPTY_CHAR;
        }else if(board[x][y] == MinesweeperConfig.EMPTY_CHAR){
            board[x][y] = MinesweeperConfig.FLAG_CHAR;
        }
    }

    /**
     * Method to check if the game is finished. The game is finished when all the cells are revealed.
     */
    private void isTheGameFinished(){
        for(int i = 0; i < MinesweeperConfig.BOARD_SIZE; i++){
            for(int j = 0; j < MinesweeperConfig.BOARD_SIZE; j++){
                if(board[i][j] == MinesweeperConfig.EMPTY_CHAR && cheatBoard[i][j] != MinesweeperConfig.BOMB_CHAR){
                    return;
                }
            }
        }
        this.gameStatus = GameStatus.FINISHED;
    }
}
