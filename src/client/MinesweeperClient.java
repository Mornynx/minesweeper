package client;

import config.MinesweeperConfig;
import config.Protocol;
import messages.MessageReceiver;
import messages.MessageSender;
import model.ServerResponseType;
import utils.Console;

import java.net.Socket;
import java.net.SocketException;

import static config.MinesweeperConfig.MAX_MESSAGE_COUNT;
import static config.MinesweeperConfig.MESSAGE_DELAY;
import static config.Protocol.END_CLIENT_MESSAGE;
import static config.Protocol.QUIT_MESSAGE;

/**
 * @author Lawal Benjamin
 * This class represents the Minesweeper client.
 * It connects to the server and plays the game.
 */
public class MinesweeperClient implements Runnable {

    private Socket socket;
    private MessageReceiver messageReceiver;
    private MessageSender messageSender;
    private boolean isRunning;

    public MinesweeperClient(){
        this.isRunning = true;
    }

    public static void main(String[] args) {
        new MinesweeperClient().run();
    }

    @Override
    public void run() {
        socket = connect();
        if(socket == null){
            System.out.println("An error occurred. The server might be not available.");
            return;
        }
        initSenderReceiver(socket);
        play();
    }

    /**
     * Method to connect to the server
     * @return the socket
     */
    private Socket connect(){
        //connect to the server
        try {
            Socket socket = new Socket(MinesweeperConfig.SERVER_IP_ADDRESS, MinesweeperConfig.SERVER_PORT);
            socket.setSoTimeout(MinesweeperConfig.CONNECTION_TIMEOUT);
            System.out.println("Connected to the server.");
            return socket;
        }catch (SocketException e){
            System.out.println("[Client] : An error occurred while setting the timeout. [" + e.getMessage() + "]");
            return null;
        }
        catch (Exception ex){
            System.out.println("An error occurred.");
            return null;
        }
    }

    /**
     * Method to initialize objects to send and receive messages between the client and the server.
     * @param socket The socket to use to send and receive messages.
     */
    private void initSenderReceiver(Socket socket){
        try{
            this.messageSender = new MessageSender(socket);
            this.messageReceiver = new MessageReceiver("CLIENT",socket, MinesweeperConfig.DEFAULT_BUFFER, Protocol.END_SERVER_MESSAGE);
        }catch (Exception e){
            System.out.println("An error occurred while reading the message. [" + e.getMessage() + "]");
            closeConnection();
            this.isRunning = false;//Stop the client
        }
    }

    /**
     * Method to play the game.
     */
    private void play(){
        try {
            while(isRunning){
                GameMenu.displayMenu();
                int command = getCommand();//Get the command from the player
                boolean result = executeCommand(command);//Execute the command
                if(!result){
                    continue;
                }
                String message = waitForResponse();
                if(message != null){
                    processResponse(
                            analyzeMessage(message),
                            message
                    );
                }
            }
        } catch (RuntimeException e) {
            System.out.printf("[CLIENT] An error occurred. [%s]\n", e.getMessage());
        }finally {
            closeConnection();
        }
    }

    /**
     * Method to process the response from the server.
     * @param analysedResponse The analyzed response from the server. It contains the command and the message as an array.
     * @param message The message received from the server.
     */
    private void processResponse(String [] analysedResponse, String message){
        if(analysedResponse == null){
            System.out.println("An error occurred while analyzing the message.");
            return;
        }
        ServerResponseType command = ServerResponseType.valueOf(analysedResponse[0]);
        switch (command){
            case WIN :
            case LOSE :
                System.out.println(message);
                this.isRunning = false;
                break;
            case CONTINUE :
            case INVALID_RANGE:
                System.out.println(message);
                break;
            case WRONG_COMMAND :
            case GAME_NOT_STARTED:
                System.out.println(analysedResponse[1]);
                break;
            default:
                // UNKNOWN
                this.isRunning = false;
                System.out.println("Enable to process the response from the server. The game will be stopped.");
                break;
        }
    }

    /**
     * Method to analyze the message from the server.
     * The message is analyzed and returned as an array.
     * The first element of the array is the command.
     * The other elements are the data associated with the command.
     * @param message The message to analyze.
     * @return the analyzed message as an array.
     * @see ServerMessageAnalyser
     */
    private String [] analyzeMessage(String message){
        return ServerMessageAnalyser.analyse(message);
    }

    /**
     * Method to execute the command basing on the GameMenu.
     * @param commandNumber The number of the command selected by the player.
     * @return true if the command was executed successfully, false otherwise.
     */
    private boolean executeCommand(int commandNumber){
        //4 possibles command :
        String message;
        switch (commandNumber) {
            case 1:
                //Reveal a cell
                message = Protocol.TRY_MESSAGE;
                String play = getPlay();
                message += " " + play + Protocol.END_CLIENT_MESSAGE;
                break;
            case 2:
                //Flag
                message = Protocol.FLAG_MESSAGE;
                String playFlag = getPlay();
                message += " " + playFlag + Protocol.END_CLIENT_MESSAGE;
                break;
            case 3:
                message = String.format("%s%s",Protocol.CHEAT_MESSAGE, Protocol.END_CLIENT_MESSAGE);
                break;
            case 4:
                message = String.format("%s%s",Protocol.QUIT_MESSAGE, Protocol.END_CLIENT_MESSAGE);
                messageSender.sendMessage(message);
                this.isRunning = false;
                return true;
            default:
                System.out.println("Invalid command. Please try again.");
                return false;
        }

        return messageSender.sendMessage(message);
    }

    /**
     * Method to wait for a response from the server.
     * If the server does not respond after a certain number of tries, the client will stop.
     * @return the message received from the server.
     */
    private String waitForResponse() throws RuntimeException{
        boolean isReceived = false;
        int count = 0;
        String message = null;
        while(!isReceived && isRunning){
            if(count != 0){
                sleep(MESSAGE_DELAY);
            }
            if(count >= MAX_MESSAGE_COUNT){
                System.out.println("The server is not responding. Please try again later.");
                messageSender.sendMessage(QUIT_MESSAGE + END_CLIENT_MESSAGE);
                this.isRunning = false;
                return null;
            }
            message = messageReceiver.receiveMessage();
            if(message != null){
                isReceived = true;
            }else {
                count++;
            }
        }
        return message;
    }

    /**
     * Method to sleep for a certain amount of time.
     * @param delay The delay in milliseconds.
     */
    private void sleep(int delay){
        try{
            Thread.sleep(delay);
        }catch (Exception e){
            System.out.println("An error occurred while sleeping. [" + e.getMessage() + "]");
        }
    }


    /**
     * Method to get the command from the player
     * @return an integer representing the command selected by the player.
     * @see GameMenu for the corresponding commands.
     */
    private int getCommand(){
        return Console.readInt("Your choice : ");
    }

    /**
     * Method to get the play from the player.
     * @return the play
     */
    private String getPlay(){
        return Console.readString("Enter your play [x y]: ");
    }

    /**
     * Method to close the connection with the server.
     */
    private void closeConnection(){
        try {
            System.out.println("[CLIENT] Closing the connection with the server.");
            socket.close();
        } catch (Exception e) {
            System.out.println("An error occurred while closing the connection. [" + e.getMessage() + "]");
        }finally {
            this.isRunning = false;
        }
    }


}
