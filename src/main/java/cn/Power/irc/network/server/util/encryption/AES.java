package cn.Power.irc.network.server.util.encryption;

import java.security.Key;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AES {

	 private static final char[] DIGITS_HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	 
	/**
	 * 根据密钥对指定的明文plainText进行加密.
	 *
	 * @param plainText 明文
	 * @return 加密后的密文.
	 */
	public static String encrypt(String plainText) {
		Key secretKey = getKey(getKey());
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] p = plainText.getBytes("UTF-8");
			byte[] result = cipher.doFinal(p);
			BASE64Encoder encoder = new BASE64Encoder();
			String encoded = encoder.encode(result);
			return encoded;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

   /**
    * 根据密钥对指定的密文cipherText进行解密.
    *
    * @param cipherText 密文
    * @return 解密后的明文.
    */
   public static String decrypt(String cipherText) {
   	Key secretKey = getKey(getKey());
   	try {
   		Cipher cipher = Cipher.getInstance("AES");
   		cipher.init(Cipher.DECRYPT_MODE, secretKey);
   		BASE64Decoder decoder = new BASE64Decoder();
   		byte[] c = decoder.decodeBuffer(cipherText);
   		byte[] result = cipher.doFinal(c);
   		String plainText = new String(result, "UTF-8");
   		return plainText;
   	} catch (Exception e) {
   		throw new RuntimeException(e);
   	}
   }
   
	public static Key getKey(String keySeed) {
		if (keySeed == null) {
			keySeed = System.getenv("AES_SYS_KEY");
		}
		if (keySeed == null) {
			keySeed = System.getProperty("AES_SYS_KEY");
		}
		if (keySeed == null || keySeed.trim().length() == 0) {
			keySeed = "abcd1234!@#$";// 默认种子
		}
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(keySeed.getBytes());
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			generator.init(secureRandom);
			return generator.generateKey();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getKey() {
		return "fendo888";
	}
	
   public static String toHex(String str) {
       byte[] data = str.getBytes();
       int outLength = data.length;
       char[] out = new char[outLength << 1];
       for (int i = 0, j = 0; i < outLength; i++) {
           out[j++] = DIGITS_HEX[(0xF0 & data[i]) >>> 4];
           out[j++] = DIGITS_HEX[0x0F & data[i]];
       }
       return new String(out);
   }
   
   public static String genKey() {
       SimpleDateFormat sdf = new SimpleDateFormat("dd:HH:mm");
       String date = sdf.format(new Date());
       return toHex(date);
   }
   
}
