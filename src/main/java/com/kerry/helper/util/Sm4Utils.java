package com.kerry.helper.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * @author kerryhe
 * @date 2020/4/21
 */
@Slf4j
public class Sm4Utils {

    private Sm4Utils() { }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ALGORITHM = "SM4";

    private static final String ALGORITHM_ECB_PADDING = "PKCS5Padding";

    /**
     * 128 -- 32位16进制；256 -- 64位16进制
     */
    private static final int DEFAULT_KEY_SIZE = 128;

    /**
     * 生成 ECB 暗号
     * @param padding
     * @param mode 模式
     * @param key
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     */
    private static Cipher generateEcbCipher(String padding, int mode, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(padding, BouncyCastleProvider.PROVIDER_NAME);
        Key keySpec = new SecretKeySpec(key, ALGORITHM);
        cipher.init(mode, keySpec);
        return cipher;
    }

    /**
     * 生成加密key
     * @return
     */
    public static String generateKey() {
        String hexKey = "";
        try {
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
            generator.init(DEFAULT_KEY_SIZE, new SecureRandom());
            byte[] keyData = generator.generateKey().getEncoded();
            hexKey = ByteUtils.toHexString(keyData);
        } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
            log.warn("generateKey error:{}", e.getMessage(), e);
        }
        return hexKey;
    }

    /**
     * ecb加密
     * @param hexKey
     * @param content
     * @return
     */
    public static String encryptEcb(String hexKey, String content) {
        String cipherTxt = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] contentData = content.getBytes(StandardCharsets.UTF_8);
        try {
            Cipher cipher = generateEcbCipher(ALGORITHM_ECB_PADDING, Cipher.ENCRYPT_MODE, keyData);
            byte[] cipherData = cipher.doFinal(contentData);
            cipherTxt = ByteUtils.toHexString(cipherData);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException e) {
            log.warn("encryptEcb error:{}", e.getMessage(), e);
        }
        return cipherTxt;
    }

    /**
     * 解密
     * @param hexKey
     * @param cipherTxt
     * @return
     */
    public static String decryptEcb(String hexKey, String cipherTxt) {
        String decryptTxt = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(cipherTxt);
        try {
            Cipher cipher = generateEcbCipher(ALGORITHM_ECB_PADDING, Cipher.DECRYPT_MODE, keyData);
            byte[] originData = cipher.doFinal(cipherData);
            decryptTxt = new String(originData, StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            log.warn("decryptEcb error:{}", e.getMessage(), e);
        }
        return decryptTxt;
    }

    /**
     * 验证是否一致
     * @param hexKey
     * @param cipherTxt
     * @param content
     * @return
     */
    public static boolean verifyEcb(String hexKey, String cipherTxt, String content) {
        String decryptTxt = decryptEcb(hexKey, cipherTxt);
        return content.equals(decryptTxt);
    }

    public static void main(String[] args) {
        String content = "abc@123";
        String hexKey = generateKey();
        log.info("hexKey:{}", hexKey);
        String encryptEcb = encryptEcb(hexKey, content);
        log.info("encryptEcb:{}", encryptEcb);
        String decryptEcb = decryptEcb(hexKey, encryptEcb);
        log.info("decryptEcb:{}", decryptEcb);
        boolean verifyEcb = verifyEcb(hexKey, encryptEcb, content);
        log.info("verifyEcb:{}", verifyEcb);
    }

}
