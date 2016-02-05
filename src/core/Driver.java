package core;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) throws FileNotFoundException {
//        try {
//            testPhase1();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Parser parser = new Parser();
        Scanner scanner = new Scanner(new File("input"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            parser.parse(line);
        }
    }

//    private static void testPhase1() throws IOException {
//        Parser parser = new Parser();
//        for(int i = 5; i<8; ++i) {
//
//            File cor = new File("output");
//            if(cor.exists())
//                cor.delete();
//            System.setOut(new PrintStream(cor));
//
//            File input = new File("p1/" + String.valueOf(i), "in.txt");
//            Scanner scanner = new Scanner(input);
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine();
//                parser.parse(line);
//            }
//            if(FileUtils.contentEqualsIgnoreEOL(cor, new File("p1/" + String.valueOf(i), "out.txt"), "UTF-8") == false)
//                throw new RuntimeException(""+i);
//        }
//    }
}
