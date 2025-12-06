import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.optifine.util.EntityUtils;


public class Loader {

	public static void main(String[] args) throws Exception {
		
		
		new Thread(()->{
			DeNick("redbanana");
		}).start();
		System.err.println("asd");
	}
	
//	 public static void main(String[] args) {
//	        // This is the path where the file's name you want to take.
//	        String path = "E://电脑桌面//users";
//	        getFile(path);
//	    }
//
//	    private static void getFile(String path) {
//	        // get file list where the path has
//	        File file = new File(path);
//	        // get the folder list
//	        File[] array = file.listFiles();
//
//	        for (int i = 0; i < array.length; i++) {
//	            if (array[i].isFile()) {
//	                // only take file name
//	                System.out.println("^^^^^" + array[i].getName());
//	                // take file path and name
//	                System.out.println("#####" + array[i]);
//	                // take file path and name
//	                System.out.println("*****" + array[i].getPath());
//	            } else if (array[i].isDirectory()) {
//	                getFile(array[i].getPath());
//	            }
//	        }
//	    }
	    
//	static String ID,UUID;
//	
//
//
	public static String deNickKey = "3e5a89c7-6e8f-41ac-a04a-e11799893802";
	
	public static String DeNick(String Nick) {
		
		String string4 = "https://api.antisniper.net/denick?nick=" + Nick;
//		String string4 = "https://api.antisniper.net/player?name=" + Nick;
		
		System.err.println(string4);
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
	    HttpGet httpGet = new HttpGet(string4);    
		httpGet.setHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36");
		httpGet.setHeader("Apikey", deNickKey);
        String TypeJson = "null";
        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet)){
        	TypeJson = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        
       	Gson gson = new Gson();
		JsonObject array = gson.fromJson(TypeJson.trim(), JsonObject.class);

		 
//		TypeJson = array.get("punishmentCategory").getAsString();
		System.err.println(array);
		
		System.err.println(array.getAsJsonObject("player").get("ign").getAsString());
		
		System.err.println(array.getAsJsonObject("player").get("nick").getAsString());
		
		
		 if (array.get("success").getAsBoolean()) {
	            if (array.has("player")) {
	                //时间
	                String data = array.getAsJsonObject("player").get("date").getAsString();
	                //查询的Nick
	                String nick = array.getAsJsonObject("player").get("nick").getAsString();
	                //真名
	                String ign = array.getAsJsonObject("player").get("ign").getAsString();

	                System.err.println("\2473Find \247e"+ nick + " \2477= \247a" +ign);
	            } else {
	            	System.err.println("\2473c No Find \247d" + "Might take a while to refresh!");
	            }
	        }else {
	        	System.err.println("\247cPost Connect Error!");
	        }
		 
		return TypeJson;
		
	}


public class Person {
    private String name;
    private String sex;
    private int age;
    public Person(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
    public String getName() {
        return name;
    }
    public String getSex() {
        return sex;
    }
    public int getAge() {
        return age;
    }
    @Override
    public String toString() {
        return "Person{name='" + name + '\'' + ", sex='" + sex + '\'' + ", age=" + age + '}';
    }

}
//	public static void main(String[] args) throws AWTException {
//		
//		System.out.println(UUID = JOptionPane.showInputDialog(null, "", "输入玩家名", 0).trim());
//
//        String string4 = "https://hypixel.net/api/players/" + UUID;
//        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(string4);
//        httpGet.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36");
//        httpGet.setHeader("xf-api-key", "kB3PlymjFqMbQA-KhdJ5N5DcxBajLziW");
//        String string5 = "";
//        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest)httpGet)){
//            
//            string5 = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
//        }
//        catch (IOException iOException) {
//            iOException.printStackTrace();
//        }
//        
//        JOptionPane.showMessageDialog(null, string5, "按确定继续查询 Ban Reason", 0);
//        
//		System.out.println(ID = JOptionPane.showInputDialog(null, "", "输入狗ban ID", 0).trim());
//		Gson gson = new Gson();
//		JsonObject array = gson.fromJson(string5.trim(), JsonObject.class);
//		
//        String string2 = ID.replace("#", "");
//
//        String string41 = "https://hypixel.net/api/players/" + array.get("uuid").getAsString() + "/ban/" + string2;
//        CloseableHttpClient closeableHttpClient1 = HttpClients.createDefault();
//        HttpGet httpGet1 = new HttpGet(string41);
//        httpGet1.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36");
//        httpGet1.setHeader("xf-api-key", "kB3PlymjFqMbQA-KhdJ5N5DcxBajLziW");
//
//        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient1.execute((HttpUriRequest)httpGet1)){
//            
//            string5 = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
//        }
//        catch (IOException iOException) {
//            iOException.printStackTrace();
//        }
//        
//        
//        while(string5.contains("too_many_request") && 
//        		JOptionPane.showConfirmDialog(null, "访问频繁, 按确定继续尝试获取!") == 1) {
//        	;
//        	
//        	 String string411 = "https://hypixel.net/api/players/" + array.get("uuid").getAsString() + "/ban/" + string2;
//             CloseableHttpClient closeableHttpClient11 = HttpClients.createDefault();
//             HttpGet httpGet11 = new HttpGet(string411);
//             httpGet11.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36");
//             httpGet11.setHeader("xf-api-key", "kB3PlymjFqMbQA-KhdJ5N5DcxBajLziW");
//
//             try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient11.execute((HttpUriRequest)httpGet11)){
//                 
//                 string5 = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
//             }
//             catch (IOException iOException) {
//                 iOException.printStackTrace();
//             }
//        }
//        
//        
//        
//        JOptionPane.showMessageDialog(null, string5, "Result", 0);
//        System.out.println(string5);
//	}

}
