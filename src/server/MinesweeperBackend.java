package server;

import config.MinesweeperConfig;
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

/**
 * @author Lawal Benjamin
 * This class is responsible for handling the backend of the minesweeper game.
 * It's a thread that listens for messages from the client and processes them with the game logic.
 * @see Runnable
 * @see IGame
 */
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
            socket.setSoTimeout(MinesweeperConfig.CONNECTION_TIMEOUT);
            // Set the timeout on the socket
            initGame();
            while(isRunning){
                sleep(MinesweeperConfig.MESSAGE_DELAY);//Simulate a delay to avoid consuming too much CPU and
                //to give the client time to send a message.
                String received = messageReceiver.receiveMessage();
                if(received != null){
                    System.out.printf("[BACKEND : %s] Received message: %s\n", backendID, received);
                    String [] analyzedMessage = ClientMessageAnalyser.analyse(received);
                    processGameCommand(analyzedMessage);
                } else {
                    System.out.println("Timeout or client disconnected.");
                    break;
                }
            }
        } catch (SocketException | RuntimeException e) {
            System.out.println("An error occurred while setting the timeout. [" + e.getMessage() + "]");
        }finally {
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
     * Method to process the game command. The command is analyzed and the appropriate action is taken.
     * @param analyzedMessage The analyzed message from the client. It contains the command and the arguments.
     */
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
                    if(status == GameStatus.GAME_OVER){
                        message = getBoardAsString(game.cheatBoard()) + Protocol.GAME_LOST_MESSAGE;
                        messageSender.sendMessage(message);
                        this.isRunning = false;
                        return;
                    }else if(status == GameStatus.FINISHED){
                        message = getBoardAsString(game.cheatBoard()) + Protocol.GAME_WON_MESSAGE;
                        messageSender.sendMessage(message);
                        this.isRunning = false;
                        return;
                    }
                    message = getBoardAsString(game.getBoard()) + Protocol.RETURN_CHAR;
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
                message = getBoardAsString(game.cheatBoard()) + Protocol.RETURN_CHAR;
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

    /**
     * Method to get the board as a string in the format required by the protocol.
     * @param board The board to convert to a string.
     * @return The board as a string.
     */
    private String getBoardAsString(char[][] board){
        return StringUtils.boardToStringFormating(board, Protocol.RETURN_CHAR);
    }

    /**
     * Method to initialize the game
     */
    private void initGame(){
        System.out.printf("[BACKEND : %s] Client [Id : %s] requested to start a new game.\n", backendID,clientID);
        game = new Game();
    }


    /**
     * Method to initialize the sender and receiver
     */
    private void initSenderReceiver(){
        this.messageSender = new MessageSender(socket);
        this.messageReceiver = new MessageReceiver("SERVER",socket, MinesweeperConfig.DEFAULT_BUFFER, Protocol.END_CLIENT_MESSAGE);
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
        }finally {
            this.isRunning = false;
        }
    }
}
