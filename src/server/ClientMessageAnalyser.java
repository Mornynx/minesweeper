package server;

import config.Protocol;
import utils.Regex;

import java.util.List;

/**
 * @author Lawal Benjamin
 * This class is responsible for analysing the message received from the client.
 */
public class ClientMessageAnalyser {

    /**
     * Method to analyse the message received from the client. It will return an string array with
     * the command to process and the coordinates if needed (TRY and FLAG commands)
     * @param message The message received from the client
     * @return An array with the command to process and the coordinates if needed
     */
    public static String[] analyse(String message){
        if(message.matches(Protocol.RX_GAME_TRY)) {
            List<String> groups = Regex.getRegexGroups(message, Protocol.RX_GAME_TRY);
            //If the regex match, I'm sure that the group 1 exists
            String coordinates = groups.get(1);
            return new String[]{Protocol.TRY_MESSAGE, coordinates};
        }
        if(message.matches(Protocol.RX_GAME_FLAG)) {
            List<String> groups = Regex.getRegexGroups(message, Protocol.RX_GAME_FLAG);
            //If the regex match, I'm sure that the group 1 exists
            String coordinates = groups.get(1);
            return new String[]{Protocol.FLAG_MESSAGE, coordinates};
        }
        if(message.matches(Protocol.RX_GAME_CHEAT)) {
            return new String[]{Protocol.CHEAT_MESSAGE};
        }
        if(message.matches(Protocol.RX_GAME_QUIT)) {
            return new String[]{Protocol.QUIT_MESSAGE};
        }
        return new String[]{Protocol.GAME_WRONG_COMMAND};
    }
}
