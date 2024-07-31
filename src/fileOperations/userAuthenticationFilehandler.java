package fileOperations;

import constant.Constant;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class userAuthenticationFilehandler {
    /**
     * write to user file which is independent of our database folder
     * @param userName - username of user
     * @param hashedPassword - hashed password of user
     */
    public void registerUser(String userName, String hashedPassword) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Constant.USER_FILE, true));
            writer.write(userName + Constant.DELIMITER + hashedPassword);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            System.out.println("EXCEPTIONN?" + e.getMessage());
        }
    }

    /**
     * Fetch all data from user file which will be used at login time
     * @return - list of strings containing all lines written in file before
     */
    public List<String> fetchFileData() {
        List<String> users = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Constant.USER_FILE));
            String nextline = reader.readLine();
            while (nextline != null && !nextline.isEmpty()) {
                users.add(nextline);
                nextline = reader.readLine();
            }
            reader.close();
            return users;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return users;
    }
}
