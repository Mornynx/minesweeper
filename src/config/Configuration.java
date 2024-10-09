package config;

public class Configuration {
    //Network configuration
    public static int SERVER_PORT = 2351;//2xxx where x is the last 3 digits of my student ID (s2402351)
    public static String SERVER_IP_ADDRESS = "*.*.*.*";//IP address of the server
    public static int MESSAGE_DELAY = 2000;
    public static int MAX_MESSAGE_COUNT = 4;
    public static int CONNECTION_TIMEOUT = 160000;//60 seconds

    //Buffer size
    public static int DEFAULT_BUFFER = 1024;


    //Game configuration
    public static int BOARD_SIZE = 7;
    public static int NUMBER_OF_MINES = 7;
    public static char FLAG_CHAR = 'F';
    public static char BOMB_CHAR = 'B';
    public static char EMPTY_CHAR = '#';
}
