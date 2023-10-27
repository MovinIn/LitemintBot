package litemint;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class RacerBot {
	public static void main(String args[]) throws Exception {
		String result = sendRequest("https://play.typeracer.com/gameserv;jsessionid=4B6D7E74B5CD98466C6EFC7BEA12B069","7|1|7|https://play.typeracer.com/com.typeracer.redesign.Redesign/|91D61BE2E7EA7401CC89A9A5F8636346|_|joinStandaloneGame|y|Z|1x|1|2|3|4|2|5|6|5|1082380|1|0|6|7|0gYOtf9Q|0|");
		System.out.println(result);
		JSONArray j=new JSONArray(result.replace("//OK", ""));
		int ychIndex = -1;
		int wc=-1;
		for(int i=0; i<j.length(); i++) {
			if(j.get(i) instanceof JSONArray) {
				System.out.println(i);
				JSONArray a=j.getJSONArray(i);
				String essay=a.getString(a.length()-3);
				wc=wordcount(essay);
			}
			else if(j.get(i) instanceof String && j.getString(i).contains("YcH")) {
				ychIndex=i;
			}
		}
		int gameId = j.getInt(ychIndex+3);
		System.out.println(gameId);
		Thread.sleep(4000);
		String r=sendRequest("https://play.typeracer.com/gameserv;jsessionid=4B6D7E74B5CD98466C6EFC7BEA12B069","7|1|9|https://play.typeracer.com/com.typeracer.redesign.Redesign/|91D61BE2E7EA7401CC89A9A5F8636346|_|updatePlayerProgress|y|I|D|2s|1x|1|2|3|4|5|5|6|6|7|8|5|"+gameId+"|1|0|0|9|a2ni5NOE|"+gameId+"|"+wc+"|1679456338814|0|");
		System.out.println(r);
	}
	public static String sendRequest(String uStr,String payload) throws Exception {
		URL url = new URL(uStr);
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection)con;
		http.setRequestMethod("POST"); // PUT is another valid option
		http.setDoOutput(true);
		byte[] out = payload.getBytes(StandardCharsets.UTF_8);
		http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36");
		http.setRequestProperty("Content-Type", "text/x-gwt-rpc; charset=UTF-8");
		http.setRequestProperty("x-gwt-permutation", "5D1AB7BEF03DFEB2199269BB65296BA9");
		http.setRequestProperty("x-gwt-module-base","https://play.typeracer.com/com.typeracer.redesign.Redesign");
		http.connect();
		try(OutputStream os = http.getOutputStream()) {
		    os.write(out);
		}
		Scanner s = new Scanner(http.getInputStream()).useDelimiter("\\A");
		String result=s.hasNext() ? s.next() : "";
		return result;
	}
	
    static int wordcount(String string)  
    {  
      int count=0;  
  
        char ch[]= new char[string.length()];     
        for(int i=0;i<string.length();i++)  
        {  
            ch[i]= string.charAt(i);  
            if( ((i>0)&&(ch[i]!=' ')&&(ch[i-1]==' ')) || ((ch[0]!=' ')&&(i==0)) )  
                count++;  
        }  
        return count;  
    }  
	
}
