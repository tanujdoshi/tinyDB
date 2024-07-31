package QueryManagment;

import constant.Constant;
import fileOperations.FileIO;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseQueries {
    /**
     * separate all database related quries to different methods
     * @param query - query to check
     */
    public void handleDatabaseQuery(String query){
        if(query.startsWith("create database")) {
            createDatabase(query);
        }else if(query.startsWith("use")) {
            useDatabase(query);
        } else if(query.startsWith("show")) {
            showDatabases();
        }
    }


    /**
     * create new database based on name of that
     * @param query
     */
    public static void createDatabase(String query) {
        Pattern pattern = Pattern.compile("^create database (\\w+);$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);
        if(matcher.find()) {
            //Checking if database is exist or not
            boolean isDatabaseExists = FileIO.isDirectoryExists(matcher.group(1));
            if(isDatabaseExists) {
                System.out.println("Database Already Exists");
            } else {
                List<String> databases = FileIO.listDatabases();
                // We are allowing creation of only one database
                if(!databases.isEmpty()) {
                    System.out.println("Query cannot be performed!, as there is already one database creation supported");
                    System.out.println("Try command SHOW DATABASES; to get database");
                } else {
                    // create new database
                    FileIO.createDirectory(Constant.ROOT_FOLDER + "/" + matcher.group(1));
                    System.out.println("Database Created Successfully!");
                }
            }
        } else {
            System.out.println("Query does not matchhhhhh");
        }
    }


    /**
     * check and use that database
     * store into global variable called currentDatabase
     * @param query
     */
    public static void useDatabase(String query) {
        String regex = "^use (\\w+);$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);
        if(matcher.matches()){
            boolean isDatabaseExists = FileIO.isFileExists(Constant.ROOT_FOLDER + "/" + matcher.group(1));
            if(isDatabaseExists) {
                Query.currentDatabase = Constant.ROOT_FOLDER + "/" +matcher.group(1);
                System.out.println("Database " +matcher.group(1) +" used");

            } else {
                System.out.println("No database available");
            }
        } else {
            System.out.println("Use does not matchh");
        }
    }

    /**
     * list of directories available inside database folder
     */
    public static void showDatabases() {
        List<String> databases = FileIO.listDatabases();
        if(databases.isEmpty()) {
            System.out.println("NO DATABASE AVAILABLE!");
        }
        for(String database: databases) {
            System.out.println(database);
        }
    }
}
