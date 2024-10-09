package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static config.Configuration.SERVER_PORT;

public class MinesweeperServer {

    public static void main(String[] args) {
        boolean isRunning = true;
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.printf("Server started on port %d\n", SERVER_PORT);
            //serverSocket.setSoTimeout(15000);
            while(isRunning){
                try{
                    Socket socket = serverSocket.accept();
                    onClientConnected(socket);
                }catch (SocketTimeoutException ex){
                    System.out.println("Server Timeout reach. No more clients will be accepted. Client connected will be served until they disconnect.");
                    isRunning = false;
                    serverSocket.close();
                }
            }
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
            System.out.println("An error occurred.");
        }
    }

    /**
     * Method to handle the client connection. Create a new thread for each client and the MinesweeperBackend which will handle the game and the communication with the client.
     * @param socket The client socket
     */
    private static void onClientConnected(Socket socket){
        String clientId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        System.out.println("Client connected: " + clientId);
        new Thread(new MinesweeperBackend(socket)).start();
    }
}
