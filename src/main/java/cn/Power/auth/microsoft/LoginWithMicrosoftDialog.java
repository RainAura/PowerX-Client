
package cn.Power.auth.microsoft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cn.Power.native0;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public final class LoginWithMicrosoftDialog implements Runnable{
	private final static String CLIENT_ID = "b54ba392-5eee-460f-af30-8add2ea2eb3e";
	private final static String AUTHORITY = "https://login.microsoftonline.com/consumers/";
	private final static String SCOPE = "XboxLive.signin";
	public boolean status;
	
	@native0
	public String loginUnsafe() throws IOException, URISyntaxException {
		String accessToken = loginToMicrosoftAccount();

		String xboxLiveToken = loginToXboxLive(accessToken);
		JSONObject xstsQueryResponse = loginToXsts(xboxLiveToken);
		String uhs = xstsQueryResponse.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0)
				.getString("uhs");
		String xstsToken = xstsQueryResponse.getString("Token");

		String mcAuthToken = loginToMinecraft(uhs, xstsToken);

		Map<String, String> profileRequestHeader = new HashMap<>();
		profileRequestHeader.put("Authorization", "Bearer " + mcAuthToken);

		
		String profile = sendGet("https://api.minecraftservices.com/minecraft/profile", profileRequestHeader);

		if (profile == null) {
			return "ERROR.DON'T Have GAME (Minecraft)";
		}

		JSONObject profileObject = new JSONObject(profile);
		
		
		Minecraft.getMinecraft().session = new Session(profileObject.getString("name"),
				profileObject.getString("id").toString(), mcAuthToken, "mojang");
		
		status = true;

		
		System.out.println("AccessToken: " + mcAuthToken + " \nUUID: " + profileObject.getString("id").toString() + " \nPlayerName: "+profileObject.getString("name"));


		return "SUCC";
	}

	private String loginToMinecraft(String uhs, String xstsToken) {
		JSONObject mcAuthQuery = new JSONObject();
		mcAuthQuery.put("identityToken", "XBL3.0 x=" + uhs + ";" + xstsToken);
		JSONObject mcAuthQueryResponse = new JSONObject(
				sendPost("https://api.minecraftservices.com/authentication/login_with_xbox", mcAuthQuery));
		
		return mcAuthQueryResponse.getString("access_token");
	}

	private JSONObject loginToXsts(String xboxLiveToken) {
		JSONObject xstsQuery = new JSONObject();
		JSONObject xstsQueryProperties = new JSONObject();
		xstsQuery.put("Properties", xstsQueryProperties);
		xstsQuery.put("RelyingParty", "rp://api.minecraftservices.com/");
		xstsQuery.put("TokenType", "JWT");

		xstsQueryProperties.put("SandboxId", "RETAIL");
		xstsQueryProperties.put("UserTokens", new JSONArray(Arrays.asList(xboxLiveToken)));

		Map<String, String> xstsQueryHeaders = new HashMap<>();
		xstsQueryHeaders.put("Content-Type", "application/json");
		xstsQueryHeaders.put("x-xbl-contract-version", "1");

		return new JSONObject(
				sendPost("https://xsts.auth.xboxlive.com/xsts/authorize", xstsQuery.toString(), xstsQueryHeaders));
	}
	
	@native0
	private String loginToXboxLive(String accessToken) {
		JSONObject xboxLiveQuery = new JSONObject();
		JSONObject xboxLiveQueryProperties = new JSONObject();
		xboxLiveQuery.put("Properties", xboxLiveQueryProperties);
		xboxLiveQuery.put("RelyingParty", "http://auth.xboxlive.com");
		xboxLiveQuery.put("TokenType", "JWT");

		xboxLiveQueryProperties.put("AuthMethod", "RPS");
		xboxLiveQueryProperties.put("SiteName", "user.auth.xboxlive.com");
		xboxLiveQueryProperties.put("RpsTicket", "d=" + accessToken);

		Map<String, String> xboxLiveQueryHeaders = new HashMap<>();
		xboxLiveQueryHeaders.put("Content-Type", "application/json");
		xboxLiveQueryHeaders.put("x-xbl-contract-version", "1");

		JSONObject xboxLiveQueryResponse = new JSONObject(sendPost("https://user.auth.xboxlive.com/user/authenticate",
				xboxLiveQuery.toString(), xboxLiveQueryHeaders));
		return xboxLiveQueryResponse.getString("Token");
	}

	@native0
	private String loginToMicrosoftAccount() throws IOException, URISyntaxException {

		String url = AUTHORITY + "/oauth2/v2.0/authorize" + "?client_id=" + CLIENT_ID + "&response_type=code"
				+ "&redirect_uri=http%3A%2F%2Flocalhost%3A53241%2F" + "&scope=" + URLEncoder.encode(SCOPE, "UTF-8")
				+ "&login_hint=" + URLEncoder.encode("Power Client - Microsoft Authentication Module", "UTF-8");
		String authenticationCode;

		try (ServerSocket serverSocket = new ServerSocket(53241)) {
			// 5 minutes timeout
			int TIMEOUT = 60 * 5 * 1000;
			serverSocket.setSoTimeout(TIMEOUT);

			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
			} else {
				throw new IOException("failed open broser");
			}

			Socket s = serverSocket.accept();
			s.setSoTimeout(TIMEOUT);
			InputStream in = s.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String firstLine = reader.readLine();
			String line = firstLine;
			while (line.length() > 0) {
				line = reader.readLine();
			}

			Pattern requestPattern = Pattern.compile("GET /\\?code=([^\\s]+) (HTTP/[0-9.]+)");
			Matcher matcher = requestPattern.matcher(firstLine);
			if (!matcher.find()) {
				OutputStream out = s.getOutputStream();
				out.close();
				s.close();
				System.out.println("auth failed");
				throw new IOException("Authentication failed");
			}

			authenticationCode = matcher.group(1);
			String httpVersion = matcher.group(2);

			OutputStream out = s.getOutputStream();
			sendResponse("<h2>" + " 登录成功! 请直接关闭页面! " + "</h2>" + "<script>\r\n" + 
					"            function CloseWebPage() {\r\n" + 
					"                if (navigator.userAgent.indexOf(\"MSIE\") > 0) {\r\n" + 
					"                    if (navigator.userAgent.indexOf(\"MSIE 6.0\") > 0) {\r\n" + 
					"                        window.opener = null; window.close();\r\n" + 
					"                    }\r\n" + 
					"                    else {\r\n" + 
					"                        window.open('', '_top'); window.top.close();\r\n" + 
					"                    }\r\n" + 
					"                }\r\n" + 
					"                else if (navigator.userAgent.indexOf(\"Firefox\") > 0) {\r\n" + 
					"                    window.location.href = 'about:blank '; //火狐默认状态非window.open的页面window.close是无效的\r\n" + 
					"                    //window.history.go(-2);\r\n" + 
					"                }\r\n" + 
					"                else {\r\n" + 
					"                    window.opener = null;\r\n" + 
					"                    window.open('', '_self', '');\r\n" + 
					"                    window.close();\r\n" + 
					"                }\r\n" + 
					"            }\r\n" + 
					"        </script>", httpVersion, out);
			out.close();
			s.close();
		}

		Map<String, String> param = new HashMap<>();
		param.put("client_id", CLIENT_ID);
		param.put("code", authenticationCode);
		param.put("grant_type", "authorization_code");
		param.put("scope", SCOPE);
		param.put("redirect_uri", "http://localhost:53241/");
		param.put("client_secret","3cfRrk2u_--jY00jeZnlkYQU1m._OpGDAG");
		
		String result = sendPost(AUTHORITY + "oauth2/v2.0/token", param);


		JSONObject resultObj = new JSONObject(result);
		return resultObj.getString("access_token");
	}

	/**
	 * Get content from the url, using POST method.
	 *
	 * @param url    The url
	 * @param params The map contains post params
	 * @return The content. If exception occurs, <i>null</i> will be returned.
	 */
	public static String sendPost(String url, Map<String, String> params) {
		return sendPost(url, mapToParamString(params), "application/x-www-form-urlencoded");
	}

	public static String mapToParamString(Map<String, String> map) {

		StringBuilder sb = new StringBuilder();

		for (Entry<String, String> entry : map.entrySet()) {

			try {
				sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				sb.append('=');
				sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				sb.append('&');
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}

		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	/**
	 * Get content from the url, using POST method.
	 *
	 * @param url  The url
	 * @param json The JSON object to send
	 * @return The content. If exception occurs, <i>null</i> will be returned.
	 */
	public static String sendPost(String url, JSONObject json) {
		return sendPost(url, json.toString(), "application/json");
	}

	/**
	 * Get content from the url, using POST method.
	 *
	 * @param url    The url
	 * @param params The string contains post params
	 * @return The content. If exception occurs, <i>null</i> will be returned.
	 */
	public static String sendPost(String url, String params) {
		return sendPost(url, params, "application/x-www-form-urlencoded");
	}

	/**
	 * Get content from the url, using POST method.
	 *
	 * @param url    The url
	 * @param params The string contains post params
	 * @param type   The param mime type
	 * @return The content. If exception occurs, <i>null</i> will be returned.
	 */
	public static String sendPost(String url, String params, String type) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", type + "; charset=utf-8");
		return sendPost(url, params, headers);
	}

	public static String sendPost(String url, String param, Map<String, String> headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

//		for (Map.Entry<String, String> entry : data.entrySet()) {
//			//给参数赋值
//			formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//		}
//		formparams.add(new StringEntity(param));

//		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(new StringEntity(param, "UTF-8"));

		if (headers != null && headers.size() > 0) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				// 请求体

				httppost.addHeader(entry.getKey(), entry.getValue());
			}
		}
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity1 = response.getEntity();
		String result = null;
		try {
			result = EntityUtils.toString(entity1);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	 public static String sendGet(String url, Map<String, String> header) throws UnsupportedEncodingException, IOException {
	        String result = "";
	        BufferedReader in = null;
	        String urlNameString = url;
	        URL realUrl = new URL(urlNameString);
	        // 打开和URL之间的连接
	        URLConnection connection = realUrl.openConnection();
	        //设置超时时间
	        connection.setConnectTimeout(5000);
	        connection.setReadTimeout(15000);
	        // 设置通用的请求属性
	        if (header!=null) {
	            Iterator<Map.Entry<String, String>> it =header.entrySet().iterator();
	            while(it.hasNext()){
	                Map.Entry<String, String> entry = it.next();
	                connection.setRequestProperty(entry.getKey(), entry.getValue());
	            }
	        }

	        connection.setRequestProperty("accept", "*/*");
	        connection.setRequestProperty("connection", "Keep-Alive");
	        connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:10.0) Gecko/20100101 Firefox/10.0");

	  
	        connection.connect();

	        in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
	        String line;
	        while ((line = in.readLine()) != null) {
	            result += line;
	        }
	        if (in != null) {
	            in.close();
	        }
	        return result;
	    }


	private void sendResponse(String stringContent, String httpVersion, OutputStream out) throws IOException {
		byte[] content = stringContent.getBytes(StandardCharsets.UTF_8);
		out.write((httpVersion + " 200 OK\r\nContent-type: text/html; charset=utf-8\r\nContent-length: ").getBytes());
		out.write(String.valueOf(content.length).getBytes());
		out.write("\r\n\r\n".getBytes());
		out.write(content);
		out.flush();
	}

	@Override
	public void run() {
		try {
			this.loginUnsafe();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}	
	}
}
