package server;

import config.Protocol;
import exception.UnauthorizeMoveException;
import messages.MessageReceiver;
import messages.MessageSender;
import model.Game;
import model.GameCommands;
import model.GameStatus;
import model.IGame;
import utils.StringUtils;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

import static config.Configuration.*;
import static model.GameStatus.GAME_OVER;

public class MinesweeperBackend implements Runnable {
    private final String backendID;
    private final Socket socket;
    private final String clientID;
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;
    private boolean isRunning;
    private IGame game;

    public MinesweeperBackend(Socket socket) {
        this.backendID = UUID.randomUUID().toString();
        this.socket = socket;
        this.clientID = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        isRunning = true;
    }


    @Override
    public void run() {
        initSenderReceiver();
        System.out.printf("CLIENT [%s] CONNECTED TO BACKEND [%s]\n", clientID, backendID);
        try {
            socket.setSoTimeout(CONNECTION_TIMEOUT);
            // Set the timeout on the socket
            initGame();
            while(isRunning){
                sleep(MESSAGE_DELAY);
                String received = messageReceiver.receiveMessage();
                if(received != null){
                    System.out.printf("[BACKEND : %s] Received message: %s\n", backendID, received.trim());
                    analyzeMessage(received);
                } else {
                    // Handle the case where the timeout occurred
                    System.out.println("Timeout or client disconnected.");
                    break;  // Exit the loop if the timeout occurs
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }



    /**
     * Method to simulate a delay
     * @param delay The delay in milliseconds
     */
    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to analyze the message received from the client.
     * POSSIBLE MESSAGES:
     * - QUIT
     * - TRY
     * - FLAG
     * - CHEAT
     * @param message The message received
     */
    private void analyzeMessage(String message){
        if(isPlayValid(message)){
                String [] analyzedMessage = ClientMessageAnalyser.analyse(message);
                processGameCommand(analyzedMessage);
        }
    }

    private void processGameCommand(String[] analyzedMessage){
        if(analyzedMessage==null || analyzedMessage.length == 0){
            return;
        }
        GameCommands command = GameCommands.valueOf(analyzedMessage[0]);
        String message;
        switch (command) {
            case TRY:
            case FLAG:
                String[] coordinates = analyzedMessage[1].split(" ");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                try{
                    GameStatus status = game.playGame(x, y, command);
                    if(status == GAME_OVER){
                        message = getBoardAsString(game.cheatBoard()) + Protocol.GAME_LOST_MESSAGE;
                        messageSender.sendMessage(message);
                        this.isRunning = false;
                        return;
                    }else if(status == GameStatus.FINISHED){
                        message = getBoardAsString(game.getBoard()) + Protocol.GAME_WON_MESSAGE;
                        messageSender.sendMessage(message);
                        this.isRunning = false;
                        return;
                    }
                    message = getBoardAsString(game.getBoard()) + Protocol.END_SERVER_MESSAGE;
                    break;
                }catch(UnauthorizeMoveException e){
                    messageSender.sendMessage(Protocol.GAME_INVALID_RANGE_MESSAGE);
                    return;
                }
            case CHEAT:
                if(!game.isGameStarted()){
                    System.out.println("Game not started yet.");
                    message = Protocol.GAME_NOT_STARTED_MESSAGE;
                    messageSender.sendMessage(message);
                    return;
                }
            message = getBoardAsString(game.cheatBoard()) + Protocol.END_SERVER_MESSAGE;
                break;
            case QUIT:
                this.isRunning = false;
                return;
            default:
                message = Protocol.GAME_WRONG_COMMAND_MESSAGE;
                break;
        }
        messageSender.sendMessage(message);
    }

    private String getBoardAsString(char[][] board){
        return StringUtils.boardToStringFormating(board, Protocol.END_SERVER_MESSAGE);
    }

    private void initGame(){
        System.out.printf("[BACKEND : %s] Client [Id : %s] requested to start a new game.\n", backendID,clientID);
        game = new Game();
        //messageSender.sendMessage("NEW GAME STARTED" + Protocol.END_SERVER_MESSAGE);
    }

    private boolean isPlayValid(String message){
        //TODO: Implement the logic to check if the play is valid
        return true;
    }

    /**
     * Method to initialize the sender and receiver
     */
    private void initSenderReceiver(){
        this.messageSender = new MessageSender(socket,"SERVER");
        this.messageReceiver = new MessageReceiver("SERVER",socket, DEFAULT_BUFFER, Protocol.END_CLIENT_MESSAGE);
    }


    /**
     * Method to close the connection
     */
    private void closeConnection(){
        try {
            System.out.printf("[BACKEND : %s] Closing the connection with the client [Id : %s]\n", backendID,clientID);
            socket.close();
        } catch (IOException e) {
            System.out.println("An error occurred while closing the connection. [" + e.getMessage() + "]");
        }finally{
            this.isRunning = false;
        }
    }
}
