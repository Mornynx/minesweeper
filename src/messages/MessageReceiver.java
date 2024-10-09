package messages;

import java.net.Socket;
import java.io.InputStream;
import java.net.SocketTimeoutException;

public class MessageReceiver {
    private final Socket socket;
    private final int DEFAULT_BUFFER;
    private final String entity;
    private final String endMessage;

    public MessageReceiver(String entity, Socket socket, int buffer_size,String endMessage) {
        this.entity = entity;
        this.socket = socket;
        this.DEFAULT_BUFFER = buffer_size;
        this.endMessage = endMessage;//Variable representing the end of a message that we want to receive. It is used to know when to stop receiving a message.
    }

    /**
     * Method to receive a message from the server
     * @return the message received or null if none
     */
    public String receiveMessage() {
        StringBuilder message = new StringBuilder();
        byte[] buffer = new byte[DEFAULT_BUFFER];
        int bytesRead;
        boolean isEnd = false;
        try {
            InputStream inputStream = socket.getInputStream();
            while (!isEnd) {
                try {
                    // Read from the socket input stream
                    bytesRead = inputStream.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }
                    String received = new String(buffer, 0, bytesRead);
                    message.append(received);
                    if (received.contains(endMessage)) {
                        isEnd = true;
                    }
                } catch (SocketTimeoutException e) {
                    // Timeout reached: stop reading
                    System.out.printf("[%s] Timeout reached while receiving message.%n", entity);
                    return null;  // Return null to signal that no message was received in time
                }
            }
        } catch (Exception e) {
            System.out.printf("[%s] An error occurred while receiving the message. [%s]%n", entity, e.getMessage());
        }
        return message.length() > 0 ? message.toString() : null;
    }

}
