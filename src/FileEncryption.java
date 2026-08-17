import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;


public class FileEncryption {

    private static final String ALGORITHM = "AES/GCM/NoPadding";

    static void encryptor(File fileToEncrypt, String password) throws Exception{
        //Generating or retrieving SecretKey
        SecretKey key = getSecretKey(password);

        //Generating Unique Initialization Vector for this file
        byte[] ivBytes = new byte[12];
        new SecureRandom().nextBytes(ivBytes);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, ivBytes);


        //Creating Cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        String outputFileName = fileToEncrypt.getName() + ".enc";
        File outputFile = new File(fileToEncrypt.getParent(), outputFileName);

        try (FileInputStream inputStream = new FileInputStream(fileToEncrypt);
             FileOutputStream outputStream = new FileOutputStream(outputFile)){

            outputStream.write(ivBytes);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead  = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) outputStream.write(output);
            }

            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) outputStream.write(outputBytes);
        }
    }

    static void decryptor(File fileToDecrypt, String password, JLabel text){
        try {
            SecretKey key = getSecretKey(password);

            try (FileInputStream inputStream = new FileInputStream(fileToDecrypt)) {
                byte[] ivBytes = new byte[12];
                int ivBytesRead = inputStream.read(ivBytes);
                if (ivBytesRead != 12) {
                    System.out.println("Something went wrong");
                    return;
                }

                GCMParameterSpec parameterSpec = new GCMParameterSpec(128, ivBytes);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

                String outputFileName = fileToDecrypt.getName().replace(".enc", "");
                if (outputFileName.equals(fileToDecrypt.getName())) {
                    outputFileName = "decrypted_" + outputFileName;
                }
                File outputFile = new File(fileToDecrypt.getParent(), outputFileName);

                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byte[] output = cipher.update(buffer, 0, bytesRead);
                        if (output != null) outputStream.write(output);
                    }
                    byte[] outputBytes = cipher.doFinal();
                    if (outputBytes != null) outputStream.write(outputBytes);
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (outputFile.exists()) desktop.open(outputFile);
                    }

                }
                text.setText("File has been Decrypted");
            }
        } catch (AEADBadTagException e) {
            text.setText("Incorrect Password");
        } catch (Exception e) {
            System.out.println("Darn it: " + e.getMessage());
        }
    }

    private static SecretKey getSecretKey(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, "AES");
    }

}
