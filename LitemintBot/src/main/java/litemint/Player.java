package litemint;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Player {
	int hp,armor; //armor=ap
	ArrayList<Card> hist;
	int index;
	ArrayList<Card> cardAL;
	String spec;
	String droid;
	Player(int i){
		index=i;
		hp=30;
		armor=0;
		hist=new ArrayList<Card>();
		spec="";
		droid="";
		cardAL=new ArrayList<Card>();
	}
	public void updateGivenBattleJSON(JSONObject j) {
		hp=j.getJSONArray("hp").getInt(index);
		armor=j.getJSONArray("ap").getInt(index);
		spec=j.getJSONArray("spec").getString(index);
		droid = j.getJSONArray("droid").getString(0);
		JSONArray array = ((JSONArray)(j.getJSONArray("hist").get(index)));
		for(int i=0; i<array.length(); i++) {
			JSONObject js = array.getJSONObject(i);
			hist.add(new Card(js));
		}
		array = ((JSONArray)(j.getJSONArray("cards").get(index)));
		cardAL.clear();
		for(int i=0; i<array.length(); i++) {
			JSONObject js = (JSONObject) array.get(i);
			cardAL.add(new Card(js));
		}
	}
}
