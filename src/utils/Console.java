package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console {

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
