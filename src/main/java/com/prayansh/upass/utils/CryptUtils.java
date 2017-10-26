package com.prayansh.upass.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Created by Prayansh on 2017-10-21.
 */
public class CryptUtils {
    private static final String PRIVATE_KEY = "PRIVATE_KEY";
    private static final String PUBLIC_KEY = "PUBLIC_KEY";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CryptUtils() {
        try {
            privateKey = loadPrivateKey();
//            publicKey = loadPublicKey();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        } catch (InvalidKeySpecException e) {
            logger.error("Invalid Private Key: " + e.getMessage());
        }
    }

    public String readPrivateKeyString() {
        return Util.readConfigVar(PRIVATE_KEY);
    }

    private PrivateKey loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyContent = readPrivateKeyString();
        privateKeyContent = privateKeyContent.replaceAll("\\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "");

        KeyFactory kf = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));

        return kf.generatePrivate(keySpecPKCS8);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String readPublicKeyString() {
        return Util.readConfigVar(PUBLIC_KEY);
    }

    private PublicKey loadPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyString = readPublicKeyString();
        publicKeyString = publicKeyString.replaceAll("\\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "");

        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));

        return kf.generatePublic(spec);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String decrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("RSA");
        byte[] encryptedByteData = Base64.getDecoder().decode(data);

        PrivateKey privateKey = getPrivateKey();
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] utf8 = cipher.doFinal(encryptedByteData);
        return new String(utf8, "UTF8");
    }
}
