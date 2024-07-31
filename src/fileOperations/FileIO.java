package fileOperations;

import QueryManagment.Query;
import constant.Constant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileIO {

    /**
     * check if directory exists or not
     * @param url - url to be checked
     * @return - boolean based on correct or not
     */
    public static boolean isDirectoryExists(String url) {
        File file = new File(Constant.ROOT_FOLDER + "/" + url);
        return file.exists() && file.isDirectory();
    }

    /**
     * check file exists or not
     * @param url - to be checked
     * @return - is file exists or not
     */
    public static boolean isFileExists(String url) {
        File file = new File(url);
        return file.exists();
    }

    /**
     * create new database , which will be creating directory
     * @param url
     */
    public static void createDirectory(String url) {
        try {
            Files.createDirectory(Paths.get(url));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get list of directories
     * @return - list of string of names of directory
     */
    public static List<String> listDatabases() {
        File directory = new File(Constant.ROOT_FOLDER);
        File[] allDatabases = directory.listFiles();
        List<String> showDatabase = new ArrayList<>();
        for (File file : allDatabases) {
            if(file.isDirectory()) {
                showDatabase.add(file.getName());
            }
        }
        return showDatabase;
    }

    /**
     * write in table which will be write into file
     * @param path - path to write a file
     * @param content - what to write in a file
     */
    public static void writeToFile(String path, String content){
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(path, true));
            writer.write(content);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
