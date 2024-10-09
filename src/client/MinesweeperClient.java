package client;

import config.Protocol;
import messages.MessageReceiver;
import messages.MessageSender;
import model.ServerResponseType;
import utils.Console;

import java.net.Socket;
import java.net.SocketException;

import static config.Configuration.*;
import static config.Protocol.*;

/**
 * This class represents the Minesweeper client.
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
            Socket socket = new Socket(SERVER_IP_ADDRESS, SERVER_PORT);
            System.out.println("Connected to the server.");
            return socket;
        }catch (Exception ex){
            System.out.println("An error occurred.");
            return null;
        }
    }

    private void initSenderReceiver(Socket socket){
        try{
            this.messageSender = new MessageSender(socket,"CLIENT");
            this.messageReceiver = new MessageReceiver("CLIENT",socket,DEFAULT_BUFFER, END_SERVER_MESSAGE);
        }catch (Exception e){
            System.out.println("An error occurred while reading the message. [" + e.getMessage() + "]");
            closeConnection();
            this.isRunning = false;//Stop the client
        }
    }

    private void closeConnection(){
        try {
            System.out.println("[CLIENT] Closing the connection with the server.");
            socket.close();
            this.isRunning = false;
        } catch (Exception e) {
            System.out.println("An error occurred while closing the connection. [" + e.getMessage() + "]");
        }
    }


    private void play(){
        try {
            socket.setSoTimeout(CONNECTION_TIMEOUT);
            while(isRunning){
                GameMenu.displayMenu();
                int command = getCommand();
                boolean result = executeCommand(command);
                if(!result){
                    continue;
                }
                String message = waitForAnswer();
                if(message != null){
                    processResponse(
                            analyzeMessage(message),
                            message
                    );
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }finally {
            closeConnection();
        }

    }

    private void processResponse(String [] analysedResponse, String message){
        if(analysedResponse == null){
            System.out.println("An error occurred while analyzing the message.");
            return;
        }
        ServerResponseType command = ServerResponseType.valueOf(analysedResponse[0]);
        switch (command){
            case WIN,LOSE:
                System.out.println(message);
                this.isRunning = false;
                break;
            case CONTINUE, INVALID_RANGE:
                System.out.println(message);
                break;
            case WRONG_COMMAND, GAME_NOT_STARTED:
                System.out.println(analysedResponse[1]);
                break;
            default:
                // UNKNOWN
                this.isRunning = false;
                System.out.println("Enable to process the response from the server. The game will be stopped.");
                break;
        }
    }
    private String [] analyzeMessage(String message){
        return ServerMessageAnalyser.analyse(message);
    }

    /**
     * Method to execute the command basing on the GameMenu.
     * @param commandNumber The number of the command.
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
                message += " " + play + END_CLIENT_MESSAGE;
                break;
            case 2:
                //Flag
                message = Protocol.FLAG_MESSAGE;
                String playFlag = getPlay();
                message += " " + playFlag + END_CLIENT_MESSAGE;
                break;
            case 3:
                message = String.format("%s %s",CHEAT_MESSAGE, END_CLIENT_MESSAGE);
                break;
            case 4:
                message = String.format("%s %s",QUIT_MESSAGE, END_CLIENT_MESSAGE);
                messageSender.sendMessage(message);
                this.isRunning = false;
                return true;
            default:
                System.out.println("Invalid command. Please try again.");
                return false;
        }

        return messageSender.sendMessage(message);
    }



    private String waitForAnswer(){
        boolean isReceived = false;
        int count = 0;
        String message = null;
        while(!isReceived && isRunning){
            if(count != 0){
               sleep(MESSAGE_DELAY);
            }
            if(count >= MAX_MESSAGE_COUNT){
                System.out.println("The server is not responding. Please try again later.");
                messageSender.sendMessage(Protocol.QUIT_MESSAGE + END_CLIENT_MESSAGE);
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

    private void sleep(int delay){
        try{
            Thread.sleep(delay);
        }catch (Exception e){
            System.out.println("An error occurred while sleeping. [" + e.getMessage() + "]");
        }
    }


    private int getCommand(){
        return Console.readInt("Your choice : ");
    }

    /**
     * Method to get the play from the player
     * @return the play
     */
    private String getPlay(){
        //read line from player input
        return Console.readString("Enter your play [x y]: ");
    }

}
