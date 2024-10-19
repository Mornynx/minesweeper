package utils;

/**
 * @author Lawal Benjamin
 * This class contains utility methods for string manipulation.
 */
public class StringUtils {
    /**
     * Method to format a board as a string with each line ending with the specified endLine.
     * @param board The board to format
     * @param endLine The string to end each line with
     * @return The formatted board as a string
     */
    public static String boardToStringFormating(char[][] board,String endLine){
        StringBuilder sb = new StringBuilder();
        for (char[] line : board) {
            for (int j = 0; j < board.length; j++) {
                sb.append(line[j]);
            }
            sb.append(endLine);
        }
        return sb.toString();
    }


}
