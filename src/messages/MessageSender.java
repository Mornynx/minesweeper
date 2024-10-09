package messages;

import java.io.OutputStream;
import java.net.Socket;

public class MessageSender {
    private final Socket socket;
    private final String entity;//The entity attribute is used is only used for sout messages
    public MessageSender(Socket socket,String entity) {
        this.socket = socket;
        this.entity = entity;
    }

    /**
     * Method to send a message to the server
     * @return true if the message was sent successfully, false otherwise
     */
    public boolean sendMessage(String message) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(message.getBytes());
            outputStream.flush();
            return true;
        } catch (Exception e) {
            System.out.println("An error occurred while sending the message. [" + e.getMessage() + "]");
            return false;
        }
    }

}
