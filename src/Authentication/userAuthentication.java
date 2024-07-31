package Authentication;

import constant.Constant;
import QueryManagment.Query;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.security.*;

import constant.Utils;
import fileOperations.userAuthenticationFilehandler;
import jdk.jshell.execution.Util;

public class userAuthentication {
    userAuthenticationFilehandler fileoperatiom = new userAuthenticationFilehandler();
    PasswordHashing passwordHashing = new PasswordHashing();
    Query q = new Query();
    Scanner sc = new Scanner(System.in);


    /**
     * Register new user
     */
    public void registerUser() {
        try {
            System.out.println("====Registering New User====");

            // Get User ID
            System.out.println("Enter New user ID");
            String userId = sc.next();

            // Get User Password
            System.out.println("Enter your password");
            String password = sc.next();

            // Ask user for captcha and validate them
            Utils.askForCaptcha();

            // Get hashed password with MD5 Hashing
            String hashpassword = passwordHashing.generateHashPassword(password);

            // write in file
            fileoperatiom.registerUser(userId, hashpassword);
            System.out.println("Congratulations " + userId + " Your account has created");
            System.out.println("========================================================");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @return login credentials are correct or not
     */
    public boolean login() {
        try {
            // Get User ID
            System.out.println("Enter user ID");
            String userId = sc.next();

            // Get User Password
            System.out.println("Enter your password");
            String password = sc.next();

            // fetch all users from file
            List<String> users = fileoperatiom.fetchFileData();
            for (String user : users) {
                String[] userInfo = user.split(Constant.DELIMITER);
                // USER ID matched
                if (userId.equals(userInfo[0])) {
                    // Password matches
                    if (passwordHashing.checkPassword(password, userInfo[1])) {
                        System.out.println(userId + " Loggedin successfully!");
                        System.out.println("========================================================");

                        // Handle further queries
                        Constant.CURRENT_USER = userId;
                        Utils.addLogs("Does Login");
                        q.handleUserQuery();
                    }
                }
            }
            System.out.println("Invalid username or password!");
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
