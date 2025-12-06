package cn.Power.util;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cn.Power.Client;

public class GetBan {
	
	
	public static String BanUUID(String name) {
		String string4 = "https://api.mojang.com/users/profiles/minecraft/" + name;
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(string4);
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36");
		httpGet.setHeader("xf-api-key", "LnM-qSeQqtJlJmJnVt76GhU-SoiolWs9");
        String UUIDJson = "";
        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest)httpGet)){
        	UUIDJson = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
		
    	Gson gson = new Gson();
		JsonObject array = gson.fromJson(UUIDJson.trim(), JsonObject.class);
		
		UUIDJson = array.get("id").getAsString();
		
        return UUIDJson;
	}
	
	public static String BanType(String UUID, String BanID) {
		
		String string4 = "https://hypixel.net/api/players/" + UUID + "/ban/" + BanID.replace("#", "");
		System.err.println(string4);
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
	    HttpGet httpGet = new HttpGet(string4);    
		httpGet.setHeader("user-agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36");
		httpGet.setHeader("xf-api-key", Client.instance.HypixelKey);
        String TypeJson = "null";
        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet)){
        	TypeJson = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        
       	Gson gson = new Gson();
		JsonObject array = gson.fromJson(TypeJson.trim(), JsonObject.class);
		TypeJson = array.get("punishmentCategory").getAsString();


		if(TypeJson.endsWith("hacks")) {
			TypeJson = ("WATCHDOG");
		}else if(TypeJson.endsWith("other")) {
			TypeJson = ("BLACKLISTED_MODIFICATIONS");
//				}else if(string5.endsWith("COMPROMISED_ACCOUNT")) {
//					string5 = ("\247c\247l[IPBAN]");
		}
		return TypeJson;
	}
	
}
