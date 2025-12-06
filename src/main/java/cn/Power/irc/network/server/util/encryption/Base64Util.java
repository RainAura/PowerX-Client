package cn.Power.irc.network.server.util.encryption;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    final static Base64.Encoder encoder = Base64.getEncoder();
    final static Base64.Decoder decoder = Base64.getDecoder();

    /**
     * 给字符串加密
     * @param text
     * @return
     */
    public static String encode(String text) {
        byte[] textByte = new byte[0];
        textByte = text.getBytes(StandardCharsets.UTF_8);
        String encodedText = encoder.encodeToString(textByte);
        return encodedText;
    }

    /**
     * 将加密后的字符串进行解密
     * @param encodedText
     * @return
     */
    public static String decode(String encodedText) {
        String text = null;
        text = new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
        return text;
    }
}