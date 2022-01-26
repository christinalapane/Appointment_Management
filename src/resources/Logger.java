package resources;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * To track successful login attempts
 */
public class Logger {

    private static final String loginPath = "login_activity.txt";


    public static void auditLogin(String userName, Boolean successBool) throws IOException {
        try {

            BufferedWriter logger = new BufferedWriter(new FileWriter(loginPath, true));
            logger.append(ZonedDateTime.now(ZoneOffset.UTC).toString() + " UTC-LOGIN ATTEMPT-USERNAME: " + userName +
                    " LOGIN SUCCESSFUL: " + successBool.toString() + "\n");
            logger.flush();
            logger.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
}
}
