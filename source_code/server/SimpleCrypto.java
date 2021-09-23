package server;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.util.Base64;

public class SimpleCrypto {

    private String charsetName = "UTF8";
    private String algorithm = "DES";
    private String encryptionkey;

    public SimpleCrypto(String encryptionkey) {
        this.encryptionkey = encryptionkey;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String encrypt(String data) {
        if (encryptionkey == null || data == null)
            return null;
        try {
            DESKeySpec desKeySpec = new DESKeySpec(encryptionkey.getBytes(charsetName));
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
            byte[] dataBytes = data.getBytes(charsetName);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(dataBytes));
        } catch (Exception e) {
            return null;
        }
    }

    public String decrypt(String data) {
        if (encryptionkey == null || data == null)
            return null;
        try {
            byte[] dataBytes = Base64.getDecoder().decode(data);
            DESKeySpec desKeySpec = new DESKeySpec(encryptionkey.getBytes(charsetName));
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] dataBytesDecrypted = (cipher.doFinal(dataBytes));
            return new String(dataBytesDecrypted);
        } catch (Exception e) {
            return null;
        }
    }
}
