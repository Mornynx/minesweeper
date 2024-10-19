package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Lawal Benjamin
 * This class is responsible for reading the input from the console.
 */
public class Console {

    /**
     * Method to read a string from the console
     * @param message The message to display to the user
     * @return The string read from the console
     */
    public static String readString(String message){
        System.out.print(message);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = reader.readLine();
            System.out.println();
            return line;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to read an integer from the console
     * @param message The message to display to the user
     * @return The integer read from the console
     */
    public static int readInt(String message){
        int resultat = 0;
        boolean ok = false;
        while(!ok){
            try{
                String line = readString(message);
                resultat = Integer.parseInt(line);
                ok = true;
            }catch(Exception e){
                System.out.println("Invalid number. Please try again.");
            }
        }
        return resultat;
    }
}
