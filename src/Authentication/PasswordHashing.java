package Authentication;

import java.security.MessageDigest;

public class PasswordHashing {
    /**
     *
     * @param password = to be hash
     * @return hashedpassword
     */
    public static String generateHashPassword(String password) {
        try {
            // Md5 hash algorithm for password
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            StringBuilder encryptedPassword = new StringBuilder();

            for (byte tempByte : digest) {
                encryptedPassword.append(String.format("%02x", tempByte & 0xff));
            }

            return encryptedPassword.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    /**
     *
     * @param userPassword - entered pasword to be checked
     * @param DBPassword - password to match
     * @return - is password match or not
     */
    public Boolean checkPassword(String userPassword, String DBPassword) {
        try {
            String encryptedPassword = generateHashPassword(userPassword);

            return encryptedPassword.equals(DBPassword);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}


