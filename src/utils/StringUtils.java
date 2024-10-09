package utils;

public class StringUtils {
    /**
     * Method to format the board to a string.
     * @param board The board to format
     * @param endLine The end line character to use
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
