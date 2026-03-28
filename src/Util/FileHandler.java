package Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String DATA_FOLDER = "data";

    // Ensure Data folder exists
    private static void ensureDataFolder() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // Ensure a specific file exists
    private static File ensureFileExists(String fileName) {
        ensureDataFolder();
        File file = new File(DATA_FOLDER + File.separator + fileName);

        try {
            if (!file.exists()) {
                boolean created = file.createNewFile();
                if (created) {
                    System.out.println("File created: " + file.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + file.getAbsolutePath());
        }

        return file;
    }

    // Read File
    public static List<String> readFile(String fileName) {
        List<String> lines = new ArrayList<>();
        File file = ensureFileExists(fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + fileName);
        }

        return lines;
    }

    // Write File
    public static void writeFile(String fileName, List<String> data) {
        File file = ensureFileExists(fileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line : data) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + file.getAbsolutePath());
        }
    }

    // Append File
    public static void appendToFile(String fileName, String data) {
        File file = ensureFileExists(fileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();

        } catch (IOException e) {
            System.out.println("Error appending file: " + file.getAbsolutePath());
        }
    }
}