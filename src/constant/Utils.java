package constant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
public class Utils {
    public static final String CAPTCHA_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generate captcha
     * @return - The randomly generated captcha string, combination of alphanumeric with 6 characters
     */
    public static String generateCaptcha() {
        Random random = new SecureRandom();
        char[] newCaptcha = new char[6];
        char[] captchaSet = CAPTCHA_SET.toCharArray();
        for (int i = 0; i < newCaptcha.length; i++) {
            int randomIndex = random.nextInt(captchaSet.length);
            newCaptcha[i] = captchaSet[randomIndex];
        }
        return new String(newCaptcha);
    }

    /**
     * get captcha from user and verify, loop through until user does not give correct captcha
     */
    public static void askForCaptcha() {
        Scanner sc = new Scanner(System.in);
        // giving default false as user can given wrong captcha
        boolean isCaptchavalid = false;
        // loop until captcha is wrong
        while(!isCaptchavalid) {
            // generate captcha from constant file
            String captcha = generateCaptcha();
            System.out.println("Please Verify Captcha by rewriting text written in red color : ");
            System.out.println(Constant.RED_COLOR + captcha +Constant.RESET_COLOR);
            System.out.println("Please enter captcha : - ");
            String userCaptcha = sc.next();

            // if captcha matches exit from while loop
            if(userCaptcha.equals(captcha)) {
                isCaptchavalid = true;
            }
            // else ask user to reenter
            else {
                System.out.println("Sorry! you have entered invalid captcha , please try again");
            }
        }
    }

    /**
     * Add logs after each operation has been executed
     * @param content : to be added in logs file
     */

    public static void addLogs(String content) {
        try{
        BufferedWriter writer = new BufferedWriter(new FileWriter(Constant.LOG_FILE, true));
        writer.write(Constant.CURRENT_USER + " " + content + " " + getTimeString());
        writer.newLine();
        writer.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get the current datetime
     * @return : - return current date time in string format
     */
    static String getTimeString() {
        SimpleDateFormat logDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date now = new Date();
        return logDateFormat.format(now);
    }
}
