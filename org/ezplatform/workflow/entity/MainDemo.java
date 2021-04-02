package org.ezplatform.workflow.entity;
import java.util.Arrays;
import java.security.MessageDigest;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

public class MainDemo {

	public static void main(String[] args) throws Exception {
		//打开云星空Silverlight界面
		String k3cloudUrl = "http://192.168.2.211/K3Cloud/Silverlight/index.aspx?ud=";
		//打开云星空HTML5界面
		//String k3cloudUrl = "http://172.17.3.93/K3Cloud/HTML5/Index.aspx?ud=";
		String ud=CreateUd();
		// 第三方系统展示信息中心界面的地址
		String url=k3cloudUrl+ud;
		System.out.println(url);
		// 浏览器打开url
		//openURL(url);
	}

	private static String CreateUd() throws Exception {
		// 多语言ID
		int lcId = 2052;
		// 数据中心ID
		String dbId = "5fa39cf119acbc";
		// 用户名称
		String userName = "钱江";
		// 第三方系统应用Id
		String appId = "209675_3fdD0wDJ4rk+79Ut71Xr2/+qVsWYRNtE";
		// 第三方系统应用秘钥
		String appSecret = "48df4cfb71fc4664a9b81186b6ed6d96";
		// 时间戳
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		// 签名
		String[] arr = new String[] { dbId, userName, appId, appSecret, timestamp };
		Arrays.sort(arr);
		String arrStr = "";
		for (int i = 0; i < arr.length; i++) {
			arrStr = arrStr + arr[i];
		}
		String sign = getSha1(arrStr);
		JSONObject data = new JSONObject();
		data.put("appid", appId);
		data.put("dbid", dbId);
		data.put("lcid", lcId);
		data.put("origintype", "SimPas");
		data.put("signeddata", sign);
		data.put("timestamp", timestamp);
		data.put("username", userName);
		data.put("entryrole", "");
		data.put("formid", "WF_Worklist_Main");
		data.put("formtype", "list");
		data.put("otherargs", "{'Status':'UnCompleted'}");
		data.put("pkid", "");
		String argJosn = data.toString();
		String argJsonBase64 = Base64.encodeBase64String(argJosn.getBytes());
		return argJsonBase64;
	}

	public static String getSha1(String str) {

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes("UTF-8"));
			byte[] md = mdTemp.digest();
			int j = md.length;
			char buf[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}
	public static void openURL(String url) {
		 
		String os = System.getProperty("os.name");
		// Linux
		if (os.indexOf("Linux") != -1) {
			try {
				String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser != null) {
					Runtime.getRuntime().exec(new String[] { browser, url });
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		// Windows
		} else {
			String cmd = "rundll32 url.dll,FileProtocolHandler " + url;
			try {
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
