package litemint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Litemint {
	private static String TOKEN="2e39e64c-ec10-4207-9f7d-78898df429cfa7a-8ca1a68461c5";
	private static final String STARTGAME = "{ \"token\": \""+TOKEN+"\", \"transactions\": [ { \"type\": 0 } ] }";
	private static final String NOTHING = "{ \"token\": \""+TOKEN+"\", \"transactions\": [] }";
	private static Timer t;
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toLowerCase();
	private static final String MODE="BOT";
	public static Game g;
	public static LitemintBot bot;
	public static TimerTask task;
	public static int battleIndex;
	boolean tacDeck;
	private static int wins;
	private static String[] tokens;
	public static String getRandomString(int length) {
		String retVal;
		StringBuilder builder = new StringBuilder();
		while (length-- != 0) {
		int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
		builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		retVal=builder.toString();
		return retVal;
	}
	
	public static void main(String[]args) throws IOException {
		t=new Timer();
		Scanner s = new Scanner(System.in);
		System.out.println("Please enter your token (taken from inspect tool):");
		while(s.hasNext()) {
			String token=s.next();
			if(!token.equalsIgnoreCase("Default")) {
				TOKEN=token;
			}
		}
		System.out.println("Your TOKEN = "+TOKEN);
		if(MODE.equals("BOT")) {
			JSONObject data = new JSONObject(sendRequest(NOTHING));
			String[]deck=new String[] {};
			if(data.getInt("seasonwins")>=150) {
				deck = new String[]
					{"card_dex_00001"
					,"card_dex_00018"
					,"card_dex_00029"
					,"card_dex_00026"
					,"card_dex_00033"
					,"card_dex_00005"
					,"card_dex_00016"
					,"card_dex_00002"
					,"card_dex_00051"
					,"card_dex_00068"};
			}
			else if(data.getInt("seasonwins")>=75) {
				deck = new String[]
					{"card_dex_00009"
					,"card_dex_00010"
					,"card_dex_00005"
					,"card_dex_00029"};
			}
			else if(data.getInt("seasonwins")>=5) {
				deck = new String[]
					{"card_dex_00009"
					,"card_dex_00010"
					,"card_dex_00005"
					,"card_dex_00029"};
			}
			else if(data.getInt("seasonwins")>=4) {
				deck = new String[]
					{"card_dex_00009"
					,"card_dex_00010"
					,"card_dex_00005"};
			}
			else if(data.getInt("seasonwins")>=2) {
				deck = new String[]
					{"card_dex_00009"
					,"card_dex_00010"};
			}
			else if(data.getInt("seasonwins")>=1) {
				deck = new String[]
					{"card_dex_00009"};
			}
			createDeck(deck);
			newGame();
			task=new TimerTask() {
				@Override
				public void run() {
					if(battleIndex>=5) {
						try {
							JSONObject battle=new JSONObject(sendRequest(NOTHING)).getJSONObject("battle");
							g.update(battle);
							Card move;
							if(wins>=150) {
								move=bot.tacDefDeck();
							}
							else {
								move=bot.makeMove();
							}
							if(move!=null) {
								System.out.println(new String("Played "+move.name+" with boost "+move.boost).replace("\n", ""));
								JSONArray array=battle.getJSONArray("hist");
								for(int j=0; j<array.length(); j++) {
									for(int i=0; i<array.getJSONArray(j).length(); i++) {
										JSONObject card = array.getJSONArray(j).getJSONObject(i).getJSONObject("properties");
										System.out.print("Name="+card.getString("name").replace("\n", " "));
										System.out.print(", points="+card.getJSONArray("points"));
										System.out.println(", boost="+card.getInt("boost"));
									}
									System.out.println();
								}
								System.out.println();
								g.update(new JSONObject(reqGivenTrans("{\"type\": 2, \"cardId\": \""+move.id+"\"}")).getJSONObject("battle"));
							}
						} 
						catch(JSONException e) {
							battleIndex=0;
							System.out.println("Battle End");
							newGame();
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						battleIndex++;
						try {
							wins=data.getInt("seasonwins");
							JSONObject data = new JSONObject(sendRequest(NOTHING));
							if(battleIndex==1) {
								if(data.getInt("seasonwins")==1) {
									reqGivenTrans("{type: 3, card: \"card_dex_00009\", deck: 1, selected: true}");
								}
								if(data.getInt("seasonwins")==2) {
									reqGivenTrans("{type: 3, card: \"card_dex_00010\", deck: 1, selected: true}");
								}
								if(data.getInt("seasonwins")==4) {
									reqGivenTrans("{type: 3, card: \"card_dex_00005\", deck: 1, selected: true}");
								}
								if(data.getInt("seasonwins")==5) {
									reqGivenTrans("{type: 3, card: \"card_dex_000028\", deck: 1, selected: true}");
								}
								if(data.getInt("seasonwins")==75) {
									reqGivenTrans("{type: 3, card: \"card_dex_000028\", deck: 1, selected: false}");
									reqGivenTrans("{type: 3, card: \"card_dex_000029\", deck: 1, selected: true}");
								}
								if(data.getInt("seasonwins")==150) {
									String[] deck = 
										{"card_dex_00001"
										,"card_dex_00018"
										,"card_dex_00029"
										,"card_dex_00026"
										,"card_dex_00033"
										,"card_dex_00005"
										,"card_dex_00016"
										,"card_dex_00002"
										,"card_dex_00051"
										,"card_dex_00068"};
									createDeck(deck);
								}
								System.out.println("W/L: "+wins+"/"+data.getInt("seasonlosses"));
								System.out.println("kdr: "+wins*1.0/data.getInt("seasonlosses"));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			};
			t.scheduleAtFixedRate(task, 1000L, 1000L);
		}
		else if(MODE.equals("LOSE")) {
			System.out.print(sendRequest(STARTGAME));
		}
		else if(MODE.equals("CREATEBOT")) {
			JSONObject o = new JSONObject(sendRequest("{ \"token\": \"2e39e64c-ec10-4207-9f7d-78898df429cf0000000000000000\", \"transactions\": [ { \"type\": 4 } ] }"));
			System.out.println(o.toString());
		}
	}
	
	private static void newGame() {
		try {
			g=new Game();
			bot=new LitemintBot();
			g.update(new JSONObject(sendRequest(STARTGAME)).getJSONObject("battle"));
		} catch (IOException e) {
			newGame();
			battleIndex=0;
		}
	}
	
	private static String reqGivenTrans(String transactions) throws IOException {
		return sendRequest("{\"token\":\""+TOKEN+"\", \"transactions\": ["+transactions+"]}");
	}
	
	static void createDeck(String[]ids) throws IOException {
		JSONObject j = new JSONObject(reqGivenTrans(""));
		JSONArray deck = (JSONArray) j.getJSONArray("decks").get(0);
		JSONArray fav = (JSONArray) j.getJSONArray("favorites").get(0);
		for(int i=0; i<deck.length(); i++) { //remove all cards from deck
			reqGivenTrans("{type: 3, card: \"card_dex_"+deck.getString(i)+"\", deck: 1, selected: false}");
		}
		for(int i=0; i<fav.length(); i++) { //remove all favorites from deck
			reqGivenTrans("{type: 3, card: \"card_dex_"+fav.getString(i)+"\", deck: 1, favorite: false}");
		}
		for(String i:ids) { //add wanted cards
			reqGivenTrans("{type: 3, card: \"card_dex_"+i+"\", deck: 1, selected: true}");
		}
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
