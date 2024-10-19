package messages;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Lawal Benjamin
 * This class is responsible for sending messages to a socket.
 */
public class MessageSender {
    private final Socket socket;
    public MessageSender(Socket socket) {
        this.socket = socket;
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
