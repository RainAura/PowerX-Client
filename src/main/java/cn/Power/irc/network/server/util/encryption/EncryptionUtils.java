package cn.Power.irc.network.server.util.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.java_websocket.WebSocket;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class EncryptionUtils {

    private static final IvParameterSpec IV_PARAMETER_SPEC = new IvParameterSpec("0000000000000000".getBytes());


    /**
     * 加密成十六进制字符串
     *
     * <p>
     * 使用AES加密，并将Cipher加密后的byte数组转换�?16进制字符�?
     * </p>
     *
     * @author Cr
     * @date 2020-03-22
     */
    public static String encryptIntoHexString(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"), IV_PARAMETER_SPEC);
            return bytesConvertHexString(cipher.doFinal(Arrays.copyOf(data.getBytes(), 16 * ((data.getBytes().length / 16) + 1))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将加密后的十六进制字符串进行解密
     *
     * @author Cr
     * @date 2020-03-22
     **/
    public static String decryptByHexString(String data, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), IV_PARAMETER_SPEC);
        return new String(cipher.doFinal(hexStringConvertBytes(data.toLowerCase())), StandardCharsets.UTF_8);
    }


    /**
     * byte数组转换成十六进制字符串
     *
     * <p>
     * 先对每个byte数�?�补码成十进�?,
     * 然后在将十进制转换成对应的十六进�?.
     * 如果单次转换, 十六进制只有�?位时�? 将在前面追加0变成两位.
     * </p>
     *
     * @author Cr
     * @date 2020-03-22
     */
    private static String bytesConvertHexString(byte[] data) {
        StringBuffer result = new StringBuffer();
        String hexString = "";
        for (byte b : data) {
            // 补码成正十进制后转换�?16进制
            hexString = Integer.toHexString(b & 255);
            result.append(hexString.length() == 1 ? "0" + hexString : hexString);
        }
        return result.toString().toUpperCase();
    }

    /**
     * 十六进制字符串转换成byte数组
     *
     * <p>
     * 在加密时, 十六进制数�?�和byte字节的对应关�? �?:  2个十六进制数值对�?  1个byte字节  (2: 1)
     * �?以byte数组的长度应该是十六进制字符串的�?�?, 并且在转换时
     * 应是两个十六进制数�?�转换成�?个byte字节  (2�?2个十六进制数值进行转�?)
     * 这也是为�?么可�?*2的原因， 例如: 0, 2, 4, 6, 8, 10, 12 依次遍历
     * </p>
     *
     * @author Cr
     * @date 2020-04-22
     */
    private static byte[] hexStringConvertBytes(String data) {
        int length = data.length() / 2;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            int first = Integer.parseInt(data.substring(i * 2, i * 2 + 1), 16);
            int second = Integer.parseInt(data.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (first * 16 + second);
        }
        return result;
    }

    
    public static String Encode(String Text) {
    	String code = null;
//    	code = Base64Util.encode(Ascii85.encode(Text.getBytes()));
    	code = Base64Util.encode(AES.encrypt(Text));
    
		return code;
    }
    
    public static String Decode(String Text) {
        String code = null;
//        code = new String(Ascii85.decode(Base64Util.decode(Text)), StandardCharsets.UTF_8);
        code = AES.decrypt(Base64Util.decode(Text));
        return code;
    }
    
}
