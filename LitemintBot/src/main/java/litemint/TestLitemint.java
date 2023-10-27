package litemint;


import java.awt.MouseInfo;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestLitemint {
	private static final String TOKEN="2e39e64c-ec10-4207-9f7d-78898df429cfa7a-8ca1a68461c5";
	private static Timer t;
	public static Game g;
	public static LitemintBot bot;
	public static TimerTask task;
	public static int battleIndex;
	
	public static void main(String[]args) throws IOException {
			task=new TimerTask() {
				@Override
				public void run() {
					try {
						JSONObject result = new JSONObject(reqGivenTrans("")).getJSONObject("battle");
						String spec = result.getJSONArray("spec").getString(1);
						if(spec.equals("card_dex_00052")) {
							System.out.println("Enemy Spec: Survival");
						}
						else if(spec.equals("card_dex_00051")) {
							System.out.println("Enemy Spec: Overload/Antidrain");
						}
						else if(spec.equals("card_dex_00050")) {
							System.out.println("Enemy Spec: Hellfire Spec");
						}
						else {
							System.out.println("Enemy Spec: No Spec LOL");
						}
						JSONArray array = ((JSONArray)(result.getJSONArray("cards").get(1)));
						for(int i=0; i<array.length(); i++) {
							JSONObject js = (JSONObject) array.get(i);
							System.out.println("Enemy Card Energy "+(i+1)+": "+js.getJSONObject("properties").getInt("boost"));
						}
					} 
					catch(JSONException e) {
						System.out.println("Battle hasn't started yet.");
					}
					catch (IOException e) {
						System.out.println("Battle hasn't started yet.");
					}
				}
			};
			t=new Timer();
			t.scheduleAtFixedRate(task, 3000L, 3000L);
	}
	
	private static String reqGivenTrans(String transactions) throws IOException {
		return sendRequest("{\"token\":\""+TOKEN+"\", \"transactions\": ["+transactions+"]}");
	}
	
	public static String sendRequest(String req) throws IOException {
		String result;
		URL url = new URL("https://api.litemint.com:9088/updatedata");
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection)con;
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		byte[] out = req.getBytes(StandardCharsets.UTF_8);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		http.connect();
		OutputStream os = http.getOutputStream();
	    os.write(out);
	    os.flush();
		Scanner s = new Scanner(http.getInputStream()).useDelimiter("\\A");
		result=s.hasNext() ? s.next() : "";
		http.getInputStream().close();
		http.disconnect();
		return result;
	}
}
