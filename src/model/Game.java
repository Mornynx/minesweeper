package model;

import exception.UnauthorizeMoveException;

import java.util.ArrayList;
import java.util.List;

import static config.Configuration.*;
/**
 * Class representing the minesweeper game.
 */
public class Game implements IGame{
    private char [][] board;
    private char [][] cheatBoard;
    private GameStatus gameStatus;
    private final List<int[]> minesCoordinates;
    private int numberOfPlay;
    //Maybe add attribut to check the number of play possible.

    public Game(){
        gameStatus = GameStatus.NOT_STARTED;
        numberOfPlay = BOARD_SIZE * BOARD_SIZE;
        minesCoordinates = new ArrayList<>(NUMBER_OF_MINES);
        createBoard();
    }

    private void createBoard(){
        board = new char[BOARD_SIZE][BOARD_SIZE];
        cheatBoard = new char[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                board[i][j] = EMPTY_CHAR;
                cheatBoard[i][j] = '0';
            }
        }
    }


    /**
     * Method to fill the board with mines. It will randomly place the mines on the board.
     * The number of mines is defined in the configuration file.
     * It calls getPossibleMineCoordinates to retrieve all the possible coordinates where a mine can be placed.
     * getPossibleMineCoordinates, so I can remove all the coordinates where a mine is already placed.
     * After having setting a mine, it will call updateCase to update cases around the mine.
     */
    private void fillMines(List<int[]> possibleCoordinates, int x, int y){
        for(int i = 0; i < NUMBER_OF_MINES; i++){
            int randomIndex = (int) (Math.random() * possibleCoordinates.size());
            int [] coordinates = possibleCoordinates.get(randomIndex);
            if(coordinates[0] == x && coordinates[1] == y){
                i--;
                continue;
            }
            cheatBoard[coordinates[0]][coordinates[1]] = BOMB_CHAR;
            updateCase(coordinates[0], coordinates[1]);
            this.minesCoordinates.add(coordinates);
            possibleCoordinates.remove(randomIndex);
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
                if(i >= 0 && i < BOARD_SIZE && j >= 0 && j < BOARD_SIZE){
                    if(cheatBoard[i][j] != BOMB_CHAR){
                        cheatBoard[i][j] = (char) (cheatBoard[i][j] + 1);
                    }
                }
            }
        }
    }

    /**
     * Always the same so could be a static method and place in another class.
     */
    private List<int[]> getPossibleMineCoordinates(){
        List<int[]> possibleCoordinates = new ArrayList<>(BOARD_SIZE*BOARD_SIZE);
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                int [] coordinates = new int[2];
                coordinates[0] = i;
                coordinates[1] = j;
                possibleCoordinates.add(coordinates);
            }
        }
        return possibleCoordinates;
    }


    /**
     * Method to return the cheat board. The cheat board is the board with all the mines and numbers shown.
     * @return the cheat board.
     */
    @Override
    public char[][] cheatBoard() {
        return cheatBoard;
    }

    /**
     * Method to play the game. It will return the board after the move.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the board after the move.
     */
    @Override
    public GameStatus playGame(int x, int y, GameCommands gameCommands) throws UnauthorizeMoveException {
        if(!isCellValid(x,y)){
            throw new UnauthorizeMoveException("The cell is not valid.");
        }
        if(numberOfPlay == BOARD_SIZE * BOARD_SIZE){
            //Means it is the first play of the game. So we need to fill the board with mines.
            //Number of play is equal to the number of cells in the board. So, for each cell revealed, we decrement the number of play.
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

    private boolean isCellValid(int x, int y){
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    private void tryPlay(int x, int y){
        if(cheatBoard[x][y] == BOMB_CHAR){
            this.gameStatus = GameStatus.GAME_OVER;
        }else if(cheatBoard[x][y] == '0') {
            revealAdjacentCells(x, y);
        }else{
            updateBoard(x,y,cheatBoard[x][y]);
            numberOfPlay--;
        }
        isTheGameFinished();
    }

    /**
     * Method to reveal the adjacent cells of the cell x,y when the cell is empty.
     * @param x the x coordinate of the cell played
     * @param y the y coordinate of the cell played
     */
    private void revealAdjacentCells(int x, int y){
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < BOARD_SIZE && j >= 0 && j < BOARD_SIZE) {
                    if (board[i][j] == EMPTY_CHAR && cheatBoard[i][j] != BOMB_CHAR) {
                        board[i][j] = cheatBoard[i][j];
                        numberOfPlay--;
                    }
                }
            }
        }
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
     * Method to update the board on coordinates x,y with the char toInsert
     * @param x the x coordinate
     * @param y the y coordinate
     * @param toInsert the char to insert into the board.
     */
    private void updateBoard(int x, int y, char toInsert){
        if(board[x][y] == EMPTY_CHAR || board[x][y] == FLAG_CHAR){
            board[x][y] = toInsert;
        }
    }

    /**
     * Method to flag a cell. If the cell is already flagged, it will unflag it.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private void flagCell(int x, int y){
        if(board[x][y] == FLAG_CHAR){
            board[x][y] = EMPTY_CHAR;
        }else if(board[x][y] == EMPTY_CHAR){
            board[x][y] = FLAG_CHAR;
        }
    }

    /**
     * Method to check if the game is finished. It will check if the number of play is less than 0.
     * If it is the case, it will set the game status to FINISHED.
     */
    private void isTheGameFinished(){
        if(numberOfPlay - NUMBER_OF_MINES <= 0){
            this.gameStatus = GameStatus.FINISHED;
        }
    }
}
