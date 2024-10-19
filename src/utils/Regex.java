package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lawal Benjamin
 * This class is responsible for analyzing messages using regex.
 * It uses regex to match the message and retrieve groups from it.
 */
public class Regex {
    /**
     * Method to analyze the message received from the client. It uses regex to match the message and retrieve groups from it.
     * @param message The message received
     * @param regex The regex to match the message
     * @return All the groups matched by the regex.
     */
    public static List<String> getRegexGroups(String message, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher groups = pattern.matcher(message);
        if(groups.matches()){
            List<String> result = new ArrayList<>();
            for(int i = 1; i <= groups.groupCount(); i++){
                result.add(groups.group(i));
            }
            return result;
        }
        return null;
    }

}
