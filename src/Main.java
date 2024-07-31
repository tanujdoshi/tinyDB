import Authentication.userAuthentication;
import QueryManagment.Query;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        userAuthentication userauth = new userAuthentication();
        while(true) {
            // Ask user what they wanted to do
            System.out.println("=======Welcome to Assignment 1 by Tanuj Doshi(B00979829)=======");
            System.out.println("What would you like to do?");
            System.out.println("1. User Registration");
            System.out.println("2. Login as existing user");
            System.out.println("3. Exit");
            System.out.print("Please Enter your choice: ");
            Scanner sc = new Scanner(System.in);
            int userInput = sc.nextInt();
            if(userInput == 1) {
                // Registering new User
                userauth.registerUser();
            } else if(userInput == 2) {
                // Login existing user
                userauth.login();
            } else if (userInput == 3){
                // user chose to exit from system
                System.out.println("Thank you for visit");
                System.exit(0);
            } else {
                //Input does not match
                System.out.println("Your input is incorrect!, please try again");
            }
        }


    }
}