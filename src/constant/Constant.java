package constant;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

public class Constant {
    public static final String DELIMITER = "##";
    public static final String HEADER_SEPARATOR = "==========";
    //Red COLOR FOR CAPTCHA
    public static final String RED_COLOR = "\033[0;31m";
    //TEXT RESET FOR CAPTCHA
    public static final String RESET_COLOR = "\033[0m";

    public static final String USER_FILE = "./user.txt";
    public static final String LOG_FILE = "./logs.txt";
    public static final String ROOT_FOLDER = "./Database";

    public static final String BEGIN_TRANSACTION = "begin transaction";
    public static final String COMMIT = "commit";
    public static final String ROLLBACK = "rollback";

    public static String CURRENT_USER = "";


}