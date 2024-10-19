package model;

/**
 * @author Lawal Benjamin
 * This enum contains the different types of responses that the server can send to the client.
 */
public enum ServerResponseType {
    WIN,
    LOSE,
    CONTINUE,
    INVALID_RANGE,
    WRONG_COMMAND,
    GAME_NOT_STARTED
}
