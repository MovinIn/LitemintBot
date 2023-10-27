package litemint;

import org.json.JSONArray;
import org.json.JSONObject;

public class Card {
	public String id;
	public String name;
	public int[] points; // {energy, damage, health, armor}
	public int type;
	public int boost;
	public boolean cloaked=true;
	Card(JSONObject o) {
		id=o.getString("id");
		JSONObject j = o.getJSONObject("properties");
		name = j.getString("name");
		cloaked = name.equals("cloaked");
		JSONArray a;
		if(name.toLowerCase().contains("catalyzer")) {
			a = j.getJSONArray("modifiers");
		}
		else {
			a = j.getJSONArray("points");
		}
		points=new int[4];
		for(int i=0; i<4; i++) {
			points[i]=a.getInt(i);
		}
		boost=j.getInt("boost");
		type=j.getInt("type");
	}
}
