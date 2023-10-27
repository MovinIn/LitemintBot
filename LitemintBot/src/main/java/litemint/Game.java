package litemint;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Game {
	public int turn;
	Player user,enemy;
	public int side;
	Game(){
		user=new Player(0);
		enemy=new Player(1);
		turn=0;
		side=0;
	}
	public void update(JSONObject j) {
		user.updateGivenBattleJSON(j);
		enemy.updateGivenBattleJSON(j);
		turn=j.getInt("turnid");
		side=j.getInt("side");
	}
/*	public void updateCards(JSONObject j) {
		List<Object> list = ((JSONArray)(j.getJSONArray("cards").get(0))).toList();
		cardAL.clear();
		for(int i=0; i<list.size(); i++) {
			JSONObject o = (JSONObject) list.get(i);
			cardAL.add(new Card(o));
		}
	}
	public void updateHist(JSONObject j) {
		hist.clear();
		enemyHist.clear();
		List<Object> list = ((JSONArray)(j.getJSONArray("hist").get(0))).toList();
		for(Object o:list) {
			JSONObject js = (JSONObject)o;
			hist.add(new Card(js));
		}
		list = ((JSONArray)(j.getJSONArray("hist").get(1))).toList();
		for(Object o:list) {
			JSONObject js = (JSONObject)o;
			enemyHist.add(new Card(js));
		}
	}*/
}
