package client;
import config.Protocol;
import model.ServerResponseType;
import utils.Regex;

import java.util.List;

/**
 * @author Lawal Benjamin
 * This class is responsible for analyzing messages received from the server.
 * It uses regex to match the message and retrieve groups from it.
 */
public class ServerMessageAnalyser {

    /**
     * Method to analyze the message received from the server.
     * @param message The message received
     * @return An array of strings containing the analyzed message.
     * The first element is the server response type and
     * the second element is the data associated with the response.
     */
    public static String[] analyse(String message){
        ServerResponseType command;
        String data;
        if(message.matches(Protocol.RX_GAME_WON)) {
            List<String> groups = Regex.getRegexGroups(message, Protocol.RX_GAME_WON);
            command = ServerResponseType.WIN;
            data = groups.get(0);
            return new String[]{command.toString(), data};
        }
        else if(message.matches(Protocol.RX_GAME_LOST)) {
            List<String> groups = Regex.getRegexGroups(message, Protocol.RX_GAME_LOST);
            command = ServerResponseType.LOSE;
            data = groups.get(0);
        }
        else if(message.matches(Protocol.RX_INVALID_RANGE)) {
            command = ServerResponseType.INVALID_RANGE;
            data = message.trim();
        }
        else if(message.matches(Protocol.RX_BOARD)) {
            command = ServerResponseType.CONTINUE;
            data = message.trim();
        }else if (message.matches(Protocol.RX_GAME_WRONG_COMMAND)){
            command = ServerResponseType.WRONG_COMMAND;
            data = "WRONG COMMAND";
        }else if (message.matches(Protocol.RX_GAME_NOT_STARTED)){
            command = ServerResponseType.GAME_NOT_STARTED;
            data = message.trim();
        }
        else{
            return null;
        }
        return new String[]{command.toString(), data};
    }
}
