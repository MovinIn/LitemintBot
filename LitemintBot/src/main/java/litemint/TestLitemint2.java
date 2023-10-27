package litemint;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class TestLitemint2 {
	private static final String TOKEN="token";
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
						String s=reqGivenTrans("");
						System.out.println(s);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			t=new Timer();
			t.scheduleAtFixedRate(task, 3000L, 3000L);
	}
	
	private static String reqGivenTrans(String transactions) throws IOException {
		return sendRequest("\"battleid\": \"mqxkpg\", \"transactions\": ["+transactions+"]}");
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
