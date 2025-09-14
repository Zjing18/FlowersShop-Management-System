package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupService {
    public static boolean backupDatabase() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFile = "backup_" + timeStamp + ".sql";

        try {
            Process process = Runtime.getRuntime().exec(
                    "mysqldump -u username -ppassword FlowerShop > " + backupFile);
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}