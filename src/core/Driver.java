package core;

import java.util.Scanner;

/**
 * Created by hossein on 11/13/15.
 */
public class Driver {
    public static void main(String[] args) {
        Parser parser = new Parser();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            parser.parse(line);
        }
    }
}
