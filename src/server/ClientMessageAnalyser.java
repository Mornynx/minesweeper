package server;

import config.Protocol;
import utils.Regex;

import java.util.List;

public class ClientMessageAnalyser {

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
