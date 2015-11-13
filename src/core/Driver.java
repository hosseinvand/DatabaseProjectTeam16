package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by hossein on 11/13/15.
 */
public class Driver {
    public static void main(String[] args) {
        Parser parser = new Parser();
        File inputFile = new File("input");
        Scanner scanner = null;
        try {
            scanner = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            parser.parse(line);
        }
    }
}
