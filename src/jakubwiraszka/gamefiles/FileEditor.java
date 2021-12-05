package jakubwiraszka.gamefiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileEditor {

    public static boolean write(String content, String path) {

        File file = new File(path);
        PrintWriter save = null;
        try {
            save = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            return false;
        }
        save.print(content);
        save.close();

        return true;
    }

    public static String read(String path) {

        StringBuilder message = new StringBuilder();

        File file = new File(path);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            return null;
        }

        while (scanner.hasNextLine()) {
            message.append(scanner.nextLine());
            message.append("\n");
        }

        return message.toString();
    }
}

