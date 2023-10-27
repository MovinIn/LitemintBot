package litemint;

import java.util.Arrays;

public class LitemintBot {
	
	LitemintBot(){
		
	}
	private int getBaseEnemyDmg() {
		int dmg;
		if(Litemint.g.enemy.spec.equals("card_dex_00052")) {
			dmg=10; //hf 5
		}
		else { //Any other spec can have bio
			dmg=15;
		}
		for(Card c:Litemint.g.enemy.hist) {
			if(c.name.equals("Biohazard")) { //if he already played bio
				dmg=10;
			}
		}
		return dmg;
	}
	
	double getActualDrainPow(double d) {
		double reduce=0.0;
		if(Litemint.g.enemy.spec.equals("ov spec")) {
			reduce+=0.4;
		}
		for(Card x:Litemint.g.enemy.hist) {
			if(x.name.equals("Firewall")) { //if he already played bio
				reduce+=0.4;
			}
			return reduce;
		}
		return d<0 ? reduce : d*reduce;
	}
	
	
	private int turnsUntilDeath(int dmg,double drainPow) {
		int turns=30;
		if(dmg==-1) {
			dmg=getBaseEnemyDmg();
		}
		for(Card c:Litemint.g.enemy.cardAL) {
			int boost=c.boost;
			if(drainPow<1) {
				boost*=(getActualDrainPow(drainPow)); //truncate
			}
			else if(drainPow<0) {
				boost-=getActualDrainPow(drainPow);
			}
			turns = Math.min(turns, (Litemint.g.user.hp+Litemint.g.user.armor+2-(Math.max(0, boost)+dmg))/2);
		}
		return Math.max(0, turns);
	}
	
	public Card tacDefDeck() {
		// Alg: 
		// If can one shot play the card
		// If die in 1 turn use tactical def if you can live a bio, else use highest def card
		// If die in 2 turn use overload>energy catalyzer>tactical def
		// Otherwise
		// If collective power can two tap, play energy card first > damage card / lowest dmg card > damage card
		// If two energy cards with combined total of 25 energy and a card with def, damage, or energy:
		// Play highest energy card, or if tac def is highest play other energy card if energy is less than 5 lower
		//cycle charge>repair>hellfire>overload>energy catalyzer(if energy>9)>armor catalyzer>tactical def
		if(Litemint.g.side==1) {
			return null;
		}
		Player user=Litemint.g.user;
		System.out.println("Def: "+(user.hp+user.armor)+"   Enemy Def: "+(Litemint.g.enemy.hp+Litemint.g.enemy.armor));
		System.out.print("Hand: ");
		user.cardAL.forEach(x->System.out.print(x.name.replace("\n", "")+" boost "+x.boost+" points "+Arrays.toString(x.points)+", "));
		System.out.println();
		String[] cycle = {"repair","charge","hellfire","overload","energy","armor","battle","vanguard","tactical"}; //Cards that are cyclable
		Card cycleC=user.cardAL.get(0);
		Card tacDef=null,defC=null,trojanC=null,siphon=null,remote=null;
		for(Card c:user.cardAL) {
			if((defC==null&&getDef(c,user.hp)>0)||((defC!=null)&&(getDef(defC,user.hp)<getDef(c,user.hp)))) {
				defC=c;
			}
			if(c.name.toLowerCase().contains("tactical")) {
				tacDef=c;
			}
			else if(c.name.toLowerCase().contains("trojan")) {
				trojanC=c;
			}
			else if(c.name.toLowerCase().contains("remote")) {
				remote=c;
			}
			else if(c.name.toLowerCase().contains("siphon")) {
				siphon=c;
			}
			for(int i=0; i<cycle.length; i++) {
				if(c.name.toLowerCase().contains(cycle[i])) {
					cycleC=c;
					break;
				}
				if(cycleC.name.toLowerCase().contains(cycle[i])) {
					break;
				}
			}
			if(c.points[1]!=0&&(c.points[1]+c.boost)>=Litemint.g.enemy.hp+Litemint.g.enemy.armor) {
				System.out.println("Played one shot");
				return c;
			}
		}
		int tud=turnsUntilDeath(-1,1.0);
		if(tud<=1) {
			if(remote!=null&&turnsUntilDeath(-1,-15)>1) {
				System.out.println("Dying in 1 turn, playing remote");
				return remote;
			}
			if(siphon!=null&&turnsUntilDeath(-1,0.0)>1) {
				System.out.println("Dying in 1 turn, playing siphon");
				return siphon;
			}
			int power=0;
			for(Card c:Litemint.g.enemy.cardAL) {
				Math.max(power, c.boost);
			}
			power+=(Litemint.g.enemy.droid.equals("card_dex_00030")?1:0)+getBaseEnemyDmg();
			if((tacDef!=null
					&&(Math.min(30, user.hp+(user.droid.equals("card_dex_00028")?2:0))
							+user.armor+tacDef.points[3]+(user.droid.equals("card_dex_00029")?1:0))
					>(power))&&tacDef.boost>12) {
				System.out.println("Dying in 1 turn, playing tactical");
				return tacDef;
			}
			else if(defC!=null&&(Math.min(30, user.hp+(user.droid.equals("card_dex_00028")?2:0))
					+user.armor+defC.points[3]+(user.droid.equals("card_dex_00029")?1:0))
			>(power)&&defC.points[3]+defC.boost>16) {
				System.out.println("Dying in 1 turn, playing defC");
				return defC;
			}
		}
		Card[] cAbove25=new Card[3];
		for(int i=0; i<user.cardAL.size(); i++) {
			for(int j=i+1; j<user.cardAL.size(); j++) {
				int k=i;
				for(int x=0; x<user.cardAL.size(); x++) {
					if(x!=i&&x!=j) {k=x; break;}
				}
				if((user.cardAL.get(k).points[j]!=0||user.cardAL.get(k).points[1]!=0||user.cardAL.get(k).points[3]!=0)) {
					int power=0;
					power+=user.cardAL.get(i).points[1]!=0 ? user.cardAL.get(i).points[1]+user.cardAL.get(i).boost : 0;
					power+=user.cardAL.get(i).points[0]!=0 ? user.cardAL.get(i).points[0]+user.cardAL.get(i).boost : 0;
					power+=user.cardAL.get(j).points[1]!=0 ? user.cardAL.get(j).points[1]+user.cardAL.get(j).boost : 0;
					power+=user.cardAL.get(j).points[0]!=0 ? user.cardAL.get(j).points[0]+user.cardAL.get(j).boost : 0;
					// If collective power of two cards can kill and a card with def, damage, or energy, play energy card first > damage card / lowest dmg card > damage card
					if((user.cardAL.get(i).points[1]!=0||user.cardAL.get(i).points[1]!=0)&&power>(Math.min(30, Litemint.g.enemy.hp+(Litemint.g.enemy.droid.equals("card_dex_00028")?2:0))
							+Litemint.g.enemy.armor+(Litemint.g.enemy.droid.equals("card_dex_00029")?1:0))) {
						System.out.println("collective power kill, playing energy card");
						return user.cardAL.get(i).points[0]+user.cardAL.get(i).boost>user.cardAL.get(j).points[0]+user.cardAL.get(j).boost ? user.cardAL.get(i) : user.cardAL.get(j);
					}
					//If two energy cards with combined total of 25 energy and a card with def, damage, or energy:
					if(user.cardAL.get(i).points[0]!=0&&user.cardAL.get(j).points[0]!=0&&
							user.cardAL.get(i).points[0]+user.cardAL.get(i).boost
							+user.cardAL.get(j).points[0]+user.cardAL.get(j).boost
							>=25) {
							if((user.cardAL.get(k).points[j]!=0||user.cardAL.get(k).points[1]!=0||user.cardAL.get(k).points[3]!=0)) {
								cAbove25[0]=user.cardAL.get(i);
								cAbove25[1]=user.cardAL.get(j);
								cAbove25[2]=user.cardAL.get(k);
								Card wanted=cAbove25[0];
								for(Card c:cAbove25) {
									if(wanted.points[0]+wanted.boost-(wanted.name.toLowerCase().contains("tactical")?5:0)
											<c.points[0]+c.boost-(c.name.toLowerCase().contains("tactical")?5:0)) {
										wanted=c;
									}
								}
								System.out.println("2 energy cards w/ combined total of 25, playing greatest energy");
								return wanted;
							}
					}
				}
			}
		}
		if(trojanC!=null&&trojanC.points[1]<15) {
			System.out.println("Played Trojan");
			return trojanC;
		}
		int energyE = Litemint.g.enemy.cardAL.stream().mapToInt(x->x.boost).sum();
		if(remote!=null&&energyE>7) {
			return remote;
		}
		for(int i=0; i<cycle.length; i++) {
			if(cycleC.name.contains(cycle[i])) {
				if(i<=5) break;
				if(siphon!=null) {
					return siphon;
				}
			}
		}
		System.out.println("Played Cycle Card");
		return cycleC;
	}
	
	public Card makeMove() {
		Card card=null;
		if(Litemint.g.side==0) {
			card = Litemint.g.user.cardAL.get(0);
				int tud=turnsUntilDeath(-1,1.0);
				Card dmgC,energyC,defC,cycleC=null,charge=null;
				dmgC=card;
				energyC=card;
				defC=card;
				for(Card c:Litemint.g.user.cardAL) {
					if(dmgC.points[1]<c.points[1]) {
						dmgC=c;
					}
					if(energyC.points[0]<c.points[0]) {
						energyC=c;
					}
					if(c.points[0]==0&&c.points[1]==0&&c.points[2]!=0&&c.points[3]==0) { // Its a disgusting repair xd
						cycleC=c;
					}
					if((cycleC==null||!(cycleC.points[0]==0&&cycleC.points[1]==0&&cycleC.points[2]!=0&&cycleC.points[3]==0))
							&&(c.name.equals("Hellfire"))) {
						cycleC=c;
					}
					if(getDef(defC,Litemint.g.user.hp)<getDef(c,Litemint.g.user.hp)) {
						defC=c;
					}
					if(c.name.toLowerCase().contains("charge")) {
						charge=c;
					}
				}
				int enemyDef =Litemint.g.enemy.hp+Litemint.g.enemy.armor;
				int power = dmgC.points[1]+dmgC.boost+(Litemint.g.enemy.droid.equals("card_dex_00030")?1:0);
				if(power>=enemyDef) {
					card=dmgC;
				}
				else if(tud<=1) {
					card=defC;
				}
				else if(tud==2) {
					card=energyC;
				}
				else if(power+energyC.points[0]+energyC.boost+4>=
						Math.min(30, Litemint.g.enemy.hp+(Litemint.g.enemy.droid.equals("card_dex_00028")?2:0))+Litemint.g.enemy.armor
						+(Litemint.g.enemy.droid.equals("card_dex_00029")?1:0)) {
					card=energyC;
				}
				else if(charge!=null) {
					card=charge;
				}
				else if(cycleC==null){
					card=energyC;
				}
				else {
					card=cycleC;
				}
			}
		return card;
	}
	
	public int getDef(Card c,int hp) {
		int power=0;
		if(c.points[3]!=0) {
			power+=Math.min(30-hp, c.points[3]+c.boost);
		}
		if(c.points[2]!=0) {
			power+=c.points[3]+c.boost;
		}
		return power;
	}
}
