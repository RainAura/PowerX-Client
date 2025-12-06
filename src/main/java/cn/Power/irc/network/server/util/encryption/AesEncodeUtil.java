package cn.Power.irc.network.server.util.encryption;
import javax.crypto.Cipher;  
import javax.crypto.spec.IvParameterSpec;  
import javax.crypto.spec.SecretKeySpec;  
  
import sun.misc.BASE64Decoder;  
import sun.misc.BASE64Encoder;  
  
public class AesEncodeUtil {  
  
    //初始向量（偏移）  
    public static final String VIPARA = "aabbccddeeffgghh";   //AES 为16bytes. DES 为8bytes  
      
    //编码方式  
    public static final String bm = "UTF-8";  
      
    //私钥  （密钥）
    private static final String ASE_KEY="aabbccddeeffgghh";   //AES固定格式为128/192/256 bits.即：16/24/32bytes。DES固定格式为128bits，即8bytes。  
      
    /** 
     * 加密 
     *  
     * @param cleartext 加密前的字符串
     * @return 加密后的字符串
     */  
    public static String encrypt(String cleartext) {  
      
        //------------------------------------------AES加密-------------------------------------

        //加密方式： AES128(CBC/PKCS5Padding) + Base64, 私钥：aabbccddeeffgghh  
        try {  
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());  
            //两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES  
            SecretKeySpec key = new SecretKeySpec(ASE_KEY.getBytes(), "AES");  
            //实例化加密类，参数为加密方式，要写全  
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密  
             //初始化，此方法可以采用三种方式，按加密算法要求来添加。（1）无第三个参数（2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)（3）采用此代码中的IVParameterSpec  
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);  

             //------------------------------------------base64编码-------------------------------------

            //加密操作,返回加密后的字节数组，然后需要编码。主要编解码方式有Base64, HEX, UUE,7bit等等。此处看服务器需要什么编码方式  
            //byte[] encryptedData = cipher.doFinal(cleartext.getBytes(bm));  
            //return new BASE64Encoder().encode(encryptedData);  

             //上下等同，只是导入包不同

             //加密后的字节数组
            byte[] encryptedData = cipher.doFinal(cleartext.getBytes(bm));
            //对加密后的字节数组进行base64编码
            byte[] base64Data = Base64Util.encode(encryptedData.toString()).getBytes();
            //将base64编码后的字节数组转化为字符串并返回
            return new String(base64Data);

            //------------------------------------------/base64编码-------------------------------------

        } catch (Exception e) {  
            e.printStackTrace();  
            return "";   
        }  
       //------------------------------------------/AES加密-------------------------------------
    }  
  
    /** 
     * 解密 
     *  
     * @param encrypted 解密前的字符串（也就是加密后的字符串）
     * @return 解密后的字符串（也就是加密前的字符串）
     */  
    public static String decrypt(String encrypted) {  

        //---------------------------------------AES解密----------------------------------------

        try {  

            //---------------------------------------base64解码---------------------------------------

            //byte[] byteMi = new BASE64Decoder().decodeBuffer(encrypted); 
 
            //上下等同，只是导入包不同

            //将字符串转化为base64编码的字节数组
            byte[] encryptedBase64Bytes = encrypted.getBytes();
            //将base64编码的字节数组转化为在加密之后的字节数组
            byte[] byteMi = Base64Util.encode(encryptedBase64Bytes.toString()).getBytes();
            
           //---------------------------------------/base64解码---------------------------------------
     
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());  
            SecretKeySpec key = new SecretKeySpec(  
                    ASE_KEY.getBytes(), "AES");  
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  
            //与加密时不同MODE:Cipher.DECRYPT_MODE  
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);  
            byte[] decryptedData = cipher.doFinal(byteMi);  
            return new String(decryptedData, bm);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return "";  
        }  
       //---------------------------------------/AES解密----------------------------------------
    }  
  
    /** 
     * 测试 
     *  
     * @param args 
     * @throws Exception 
     */  
    public static void main(String[] args) throws Exception {  
  
  
        String content = "98.5674";  
        // 加密  
        System.out.println("加密前：" + content);  
        String encryptResult = encrypt(content);  
          
        System.out.println("加密后：" + new String(encryptResult));  
        // 解密  
        String decryptResult = decrypt(encryptResult);  
        System.out.println("解密后：" + new String(decryptResult));  
  
  
    }  
}  
