package co.haslo.cop;

import android.app.Activity;
import android.util.Log;
import android.webkit.CookieManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PHPComm extends Activity {

	// serverURL : JSON 요청을 받는 서버의 URL
    // postParams : POST 방식으로 전달될 입력 데이터
    // 반환 데이터 : 서버에서 전달된 JSON 데이터

    public static String getJson(String serverUrl, String postParams) throws Exception {

		BufferedReader bufferedReader = null;
		try {  
			Thread.sleep(100);
			URL url = new URL(serverUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 세션 쿠키 전달
			String cookieString = CookieManager.getInstance().getCookie(Value.IPADDRESS);
			
			StringBuilder sb = new StringBuilder();
			sb.setLength(0);

			if(conn != null){ // 연결되었으면
				//add request header
				conn.setRequestMethod("POST");
				conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				if (cookieString != null) {
				   conn.setRequestProperty("Cookie", cookieString);
				   Log.e("PHP_getCookie", cookieString);
				 }
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				conn.setUseCaches(false);
				conn.setDefaultUseCaches(false);
				conn.setDoOutput(true); // POST 로 데이터를 넘겨주겠다는 옵션
				conn.setDoInput(true);

                // APNSend post request
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes(postParams);
				wr.flush();
				wr.close();

                int responseCode = conn.getResponseCode();
				System.out.println("GET Response Code : " + responseCode);
				if(responseCode == HttpURLConnection.HTTP_OK){ // 연결 코드가 리턴되면
					bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String json;
					while((json = bufferedReader.readLine())!= null){ 
						sb.append(json + "\n"); 
					}      
				}
				bufferedReader.close();
			}
			System.out.println("PHP Comm Out Size : " + sb.length()); // 서버에서 보내준 결과의 길이
			System.out.println("PHP Comm Out : " + sb); // 서버에서 보내준 내용
			return sb.toString().trim();
			
		} catch(Exception e){
			return new String("Exception: " + e.getMessage());
		} 
  
    }
}
