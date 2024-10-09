package config;

import static config.Configuration.*;

public class Protocol {



    public static String CARRIAGE_RETURN_CHAR = "\r";
    public static String RETURN_CHAR = "\n";
    public static String SPACE_CHAR = " ";
    public static String NUMBER = "\\d";
    public static String STRING = "\\w";


    public static String COMBINED_RETURN_CHAR = CARRIAGE_RETURN_CHAR + RETURN_CHAR;
    public static String MOVE_POSITION = NUMBER + SPACE_CHAR + NUMBER;


    //Client message
    public static String END_CLIENT_MESSAGE = COMBINED_RETURN_CHAR + COMBINED_RETURN_CHAR;
    public static String TRY_MESSAGE = "TRY";
    public static String FLAG_MESSAGE = "FLAG";
    public static String CHEAT_MESSAGE = "CHEAT";
    public static String QUIT_MESSAGE = "QUIT";



    //Server message
    public static String END_SERVER_MESSAGE = "\r\n";
    public static String GAME_NOT_STARTED = "GAME NOT STARTED";
    public static String GAME_WON = "GAME WON";
    public static String GAME_LOST = "GAME LOST" + END_SERVER_MESSAGE;
    public static String GAME_INVALID_RANGE = "INVALID RANGE";
    public static String GAME_WRONG_COMMAND = "WRONG";
    public static String GAME_CONTINUE = "CONTINUE";

    public static String GAME_WON_MESSAGE = GAME_WON + END_SERVER_MESSAGE;
    public static String GAME_LOST_MESSAGE = GAME_LOST + END_SERVER_MESSAGE;
    public static String GAME_INVALID_RANGE_MESSAGE = GAME_INVALID_RANGE + END_SERVER_MESSAGE;
    public static String GAME_WRONG_COMMAND_MESSAGE = GAME_WRONG_COMMAND + END_SERVER_MESSAGE;
    public static final String GAME_NOT_STARTED_MESSAGE = GAME_NOT_STARTED + END_SERVER_MESSAGE;


    //Board
    public static String BOARD_LINE_CELL =
            String.format("%s%s%s%s", NUMBER, EMPTY_CHAR, FLAG_CHAR, BOMB_CHAR);
    public static String BOARD_LINE =
            String.format("[%s]{%d}%s", BOARD_LINE_CELL, BOARD_SIZE, END_SERVER_MESSAGE);
    public static String BOARD =
            String.format("(%s){%d}", BOARD_LINE, BOARD_SIZE);

    //Analyse server message
    public static String RX_GAME_WON =
            String.format("^%s(%s)%s$", BOARD,GAME_WON,END_SERVER_MESSAGE);
    public static String RX_GAME_LOST =
            String.format("^%s(%s)%s$", BOARD,GAME_LOST,END_SERVER_MESSAGE);
    public static String RX_BOARD =
            String.format("^(%s)%s$", BOARD,END_SERVER_MESSAGE);
    public static String RX_INVALID_RANGE =
            String.format("^(%s)%s$", GAME_INVALID_RANGE,END_SERVER_MESSAGE);
    public static String RX_GAME_WRONG_COMMAND =
            String.format("^(%s)%s$", GAME_WRONG_COMMAND,END_SERVER_MESSAGE);
    public static String RX_GAME_NOT_STARTED =
            String.format("^(%s)%s$", GAME_NOT_STARTED,END_SERVER_MESSAGE);

    //
    public static String RX_GAME_TRY =
            String.format("(%s) (%s)%s", TRY_MESSAGE, MOVE_POSITION,END_CLIENT_MESSAGE);

    public static String RX_GAME_FLAG =
            String.format("(%s) (%s)%s", FLAG_MESSAGE, MOVE_POSITION,END_CLIENT_MESSAGE);


    public static String RX_GAME_CHEAT =
            String.format("(%s) %s", CHEAT_MESSAGE,END_CLIENT_MESSAGE);

    public static String RX_GAME_QUIT =
            String.format("(%s) %s", QUIT_MESSAGE,END_CLIENT_MESSAGE);


}
