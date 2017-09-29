//Team Determination
//Eoghan O'Donnell - 14464082
//Jazheel Luna - 14486752
//Sam Bryan - 14701935

import java.util.ArrayList;

public class Determination implements Bot {
	
	private BoardAPI board;
	private PlayerAPI player;
	
	Determination (BoardAPI inBoard, PlayerAPI inPlayer) {
		board = inBoard;	
		player = inPlayer;
		return;
	}
	
	public String getName () {
		String command = "Determination";
		return(command);
	}

	public String getReinforcement () {
		String command = "";
		int remainingCountries[] = getRemainingCountriesPerContinent();
		int i;
		if(getNumContinentsOwned()==0){	//sequentially go through how many continents we own and apply logic checks depending on how many
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(remainingCountries[4]<=2) command=getReinforcementStringForAttack(4);			//SA check comes first so it's prioritised over other continents
			else if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command = getReinforcementStringForAttack(i);	//if a continent has less than 2 countries left
			else if(remainingCountries[0]<=6) command=getReinforcementStringForAttack(0);		//else if we have a few in NA
			else if(remainingCountries[5]<=3) command=getReinforcementStringForAttack(5);		//--in AF
			else if(remainingCountries[1]<=4) command=getReinforcementStringForAttack(1);		//--in EU
			else if(remainingCountries[2]<=5) command=getReinforcementStringForAttack(2);		//--in AS
			else if(remainingCountries[4]<4) command=getReinforcementStringForAttack(4);		//else if we have any in SA
			else if(remainingCountries[0]<9) command=getReinforcementStringForAttack(0);		//-- in NA
			else if(remainingCountries[5]<6) command=getReinforcementStringForAttack(5);		//-- in AF
			else if(remainingCountries[1]<7) command=getReinforcementStringForAttack(1);		//-- in EU
			else if(remainingCountries[3]<4) command=getReinforcementStringForAttack(3);		//-- in AU
			else command=getReinforcementStringForAttack(2);									//-- in AS
		}
		if(getNumContinentsOwned()==1){
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command = getReinforcementStringForAttack(i);
			else if(remainingCountries[0]<=6&&remainingCountries[0]>0) command=getReinforcementStringForAttack(0);		
			else if(remainingCountries[5]<=3&&remainingCountries[5]>0) command=getReinforcementStringForAttack(5);		
			else if(remainingCountries[1]<=4&&remainingCountries[1]>0) command=getReinforcementStringForAttack(1);		
			else if(remainingCountries[2]<=5&&remainingCountries[2]>0) command=getReinforcementStringForAttack(2);		
			else if(remainingCountries[0]==0){		//if NA, go for SA
				if(remainingCountries[4]==4) command = "CentralAmerica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(4);
			} 
			else if(remainingCountries[4]==0){		//if SA, go for NA
				if(remainingCountries[0]==9) command = "Venezuela " + player.getNumUnits();
				else command = getReinforcementStringForAttack(0);
			}
			else if(remainingCountries[5]==0){		//if AF, go for SA
				if(remainingCountries[4]==4) command = "NAfrica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(4);
			}
			else if(remainingCountries[1]==0){		//if EU, go for NA
				if(remainingCountries[0]==9) command = "Iceland " + player.getNumUnits();
				else command = getReinforcementStringForAttack(0);
			}
			else if(remainingCountries[2]==0){		//if AS, go for AU
				if(remainingCountries[3]==4) command = "Siam " + player.getNumUnits();
				else command = getReinforcementStringForAttack(3);
			}
			else if(remainingCountries[3]==0){		//if AU, go for AS
				if(remainingCountries[2]==12) command = "Indonesia " + player.getNumUnits();
				else if(board.getOccupier(23)!=player.getId()){
					command = "Indonesia " + player.getNumUnits();
				}
				else command = getReinforcementStringForAttack(2);
			}
		}
		if(getNumContinentsOwned()==2){
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command = getReinforcementStringForAttack(i);
			else if(remainingCountries[0]<=6&&remainingCountries[0]>0) command=getReinforcementStringForAttack(0);		
			else if(remainingCountries[5]<=3&&remainingCountries[5]>0) command=getReinforcementStringForAttack(5);		
			else if(remainingCountries[1]<=4&&remainingCountries[1]>0) command=getReinforcementStringForAttack(1);		
			else if(remainingCountries[2]<=5&&remainingCountries[2]>0) command=getReinforcementStringForAttack(2);			
			else if(remainingCountries[0]==0&&remainingCountries[4]==0){			//if NA and SA, attack AF
				if(remainingCountries[5]==6) command = "Brazil " + player.getNumUnits();
				else command = getReinforcementStringForAttack(5);
			}
			else if(remainingCountries[0]==0){			//if NA and not SA, go for SA
				if(remainingCountries[4]==4) command = "CentralAmerica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(4);
			}
			else if(remainingCountries[4]==0){			//if SA and not NA, go for NA
				if(remainingCountries[0]==9) command = "Venezuela " + player.getNumUnits();
				else command = getReinforcementStringForAttack(0);
			}
			else if(remainingCountries[5]==0){			//if AF and not SA, go for SA
				if(remainingCountries[4]==4) command = "NAfrica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(4);;
			}
			else if(remainingCountries[2]==0){			//if AS
				if(remainingCountries[3]!=0){			//if not AU, go for AU
					if(remainingCountries[3]==4) command = "Siam " + player.getNumUnits();
				else command = getReinforcementStringForAttack(3);;
				}
				else{									//if AS/AU,go for NA
					if(remainingCountries[0]==9) command = "Kamchatka " + player.getNumUnits();
				else command = getReinforcementStringForAttack(0);
				}
			}
			else{										//if EU/AU, go for AS
				if(remainingCountries[2]==0) command = "Indonesia " + player.getNumUnits();
				else if(board.getOccupier(23)!=player.getId()){
					command = "Indonesia " + player.getNumUnits();
				}
				else command = getReinforcementStringForAttack(2);
			}
		}
		if(getNumContinentsOwned()==3){
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command=getReinforcementStringForAttack(i);
			else if(remainingCountries[0]<=6&&remainingCountries[0]>0) command=getReinforcementStringForAttack(0);		
			else if(remainingCountries[5]<=3&&remainingCountries[5]>0) command=getReinforcementStringForAttack(5);		
			else if(remainingCountries[1]<=4&&remainingCountries[1]>0) command=getReinforcementStringForAttack(1);		
			else if(remainingCountries[2]<=5&&remainingCountries[2]>0) command=getReinforcementStringForAttack(2);			
			else if(remainingCountries[0]==0&&remainingCountries[4]==0){			//if NA and SA
				if(remainingCountries[5]!=0){		//if not AF, go for AF
					if(remainingCountries[5]==6) command = "Brazil " + player.getNumUnits();
				else command = getReinforcementStringForAttack(5);
				}
				else if(remainingCountries[1]!=0){	//else if not EU, go for EU
					if(remainingCountries[1]==7) command = "Greenland " + player.getNumUnits();
				else command = getReinforcementStringForAttack(1);
				}
			}
			else if(remainingCountries[0]==0){			//if NA and not SA, go for SA
				if(remainingCountries[4]==4) command = "CentralAmerica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(4);
			}
			else if(remainingCountries[4]==0){			//if SA and not NA, go for NA
				if(remainingCountries[0]==9) command = "Venezuela " + player.getNumUnits();
				else command = getReinforcementStringForAttack(0);
			}
			else if(remainingCountries[5]==0){			//if AF and not SA, go for SA
				if(remainingCountries[4]==4) command = "NAfrica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(4);
			}
			else{										//EU/AS/AU is only combination left - go for AF --WE CAN ADD MORE LOGIC HERE IF WE WANT
				if(remainingCountries[5]==6) command = "MiddleEast " + player.getNumUnits();
				else command = getReinforcementStringForAttack(5);
			}
		}
		if(getNumContinentsOwned()==4){
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command=getReinforcementStringForAttack(i);
			else if(remainingCountries[0]<=6&&remainingCountries[0]>0) command=getReinforcementStringForAttack(0);		
			else if(remainingCountries[5]<=3&&remainingCountries[5]>0) command=getReinforcementStringForAttack(5);		
			else if(remainingCountries[1]<=4&&remainingCountries[1]>0) command=getReinforcementStringForAttack(1);		
			else if(remainingCountries[2]<=5&&remainingCountries[2]>0) command=getReinforcementStringForAttack(2);				
			else if(remainingCountries[4]!=0){			//if not SA
				if(remainingCountries[0]==0){			//if NA, go from there
					if(remainingCountries[4]==4) command = "CentralAmerica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(4);
				}
				else{									//otherwise go from AF
					if(remainingCountries[4]==4) command = "NAfrica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(4);
				}
			}
			else if(remainingCountries[0]!=0){			//if not NA, go from SA
				if(remainingCountries[0]==9) command = "Venezuela " + player.getNumUnits();
				else command = getReinforcementStringForAttack(0);;
			}
			else if(remainingCountries[5]!=0){			//if not AF	, go form SA
				if(remainingCountries[5]==6) command = "Brazil " + player.getNumUnits();
				else command = getReinforcementStringForAttack(5);
			}
			else if(remainingCountries[1]!=0){			//if not EU, go from AF
				if(remainingCountries[0]==7) command = "NAfrica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(1);
			}
			else{										//NA/SA/AF/EU is only combination left, go for AS
				if(remainingCountries[2]==12) command = "Alaska " + player.getNumUnits();
				else command = getReinforcementStringForAttack(2);
			}
		}
		if(getNumContinentsOwned()==5){	
			if(remainingCountries[0]!=0){			//if not NA
				if(remainingCountries[0]==9) command = "Venezuela " + player.getNumUnits();
				else command = getReinforcementStringForAttack(0);
			}
			else if(remainingCountries[1]!=0){			//if not EU
				if(remainingCountries[1]==7) command = "Ural " + player.getNumUnits();
				else command = getReinforcementStringForAttack(1);;
			}
			else if(remainingCountries[2]!=0){			//if not AS
				if(remainingCountries[2]==12) command = "Ukraine " + player.getNumUnits();
				else command = getReinforcementStringForAttack(2);
			}
			else if(remainingCountries[3]!=0){			//if not AU
				if(remainingCountries[3]==4) command = "Siam " + player.getNumUnits();
				else command = getReinforcementStringForAttack(3);
			}
			else if(remainingCountries[4]!=0){			//if not SA
				if(remainingCountries[4]==4) command = "CentralAmerica " + player.getNumUnits();
				else command = getReinforcementStringForAttack(4);
			}
			else{										//if not AF
				if(remainingCountries[5]==6) command = "WEurope " + player.getNumUnits();
				else command = getReinforcementStringForAttack(5);
			}
		}	
		
		if (command.equals("")){
			command = GameData.COUNTRY_NAMES[(int)(Math.random() * GameData.NUM_COUNTRIES)];
			command = command.replaceAll("\\s", "");
			command += " 1";
		}
		return(command);
	}
	
	public String getPlacement (int forPlayer) {
		String command = "";
		int i;
		for(i=41;i>=0&&command.equals("");i--){		//goes through each territory and checks if beside enemy but not us
			if(board.getOccupier(i)==forPlayer){
				int[] adjacentCountries = getSurrounding(i);
				boolean check1=false;
				boolean check2=false;
				for(int j=0;j<adjacentCountries.length;j++){
					if(adjacentCountries[j]==player.getId()) check1=true;
					if(adjacentCountries[j]==(player.getId()+1)%2) check2=true;
				}
				if(!check1&&check2){
					command = GameData.COUNTRY_NAMES[i];
					command = command.replaceAll("\\s", "");
				}
			}
		}
		for(i=41;i>=0&&command.equals("");i--){		//otherwise checks if beside us both
			if(board.getOccupier(i)==forPlayer){
				int[] adjacentCountries = getSurrounding(i);
				boolean check=false;
				for(int j=0;j<adjacentCountries.length;j++){
					if(adjacentCountries[j]==(player.getId()+1)%2) check=true;
				}
				if(check){
					command = GameData.COUNTRY_NAMES[i];
					command = command.replaceAll("\\s", "");
				}
			}
		}
		for(i=41;i>=0&&command.equals("");i--){		//otherwise checks if not beside us
			if(board.getOccupier(i)==forPlayer){	
				int[] adjacentCountries = getSurrounding(i);
				boolean check=false;
				for(int j=0;j<adjacentCountries.length;j++){
					if(adjacentCountries[j]==player.getId()) check=true;
				}
				if(!check){
					command = GameData.COUNTRY_NAMES[i];
					command = command.replaceAll("\\s", "");
				}
			}
		}
		if(command.equals("")){						//otherwise places randomly
			do{
				i = (int)(Math.random() * GameData.NUM_COUNTRIES);
				command = GameData.COUNTRY_NAMES[i];
				command = command.replaceAll("\\s", "");
			}while(board.getOccupier(i)!=forPlayer);
		}
		return(command);
	}
	
	public String getCardExchange () {
		String command = "";
		int artillery=0, cavalry=0, infantry=0, wildcard=0;	// These hold a count of how many cards of each the bot has.
		if (player.isForcedExchange()){
			// trade in
			ArrayList<Card> cards = player.getCards();	// This initialises a new ArrayList with the bots cards
			int[] cardids = new int[cards.size()];	// And then we just store the ids of the cards in an integer array.
			for(int i=0;i<cards.size();i++){
				cardids[i] = cards.get(i).getInsigniaId();
			}
			for(int i=0;i<cardids.length;i++){	// This for loop just counts the number of each card type.
				if(cardids[i]==0){
					infantry++;
				}
				else if(cardids[i]==1){
					cavalry++;
				}
				else if(cardids[i]==2){
					artillery++;
				}
				else if(cardids[i]==3){
					wildcard++;
				}
			}
			if(infantry>=3){	// If we have 3 or more of any type, use them.
				command = "iii";
			}
			else if(cavalry>=3){
				command = "ccc";
			}
			else if(artillery>=3){
				command = "aaa";
			}
			else if(wildcard==0){	// Otherwise if we have no wildcards (since we have to trade in this is the only case left) use one of each.
				command = "ica";
			}
			else if(wildcard>=1){	// If we do have wildcards and not three of any specific type.
				if(infantry==2){	// If we have two of a type use those and the wildcard.
					command = "iiw";
				}
				else if(cavalry==2){
					command = "ccw";
				}
				else if(artillery==2){
					command = "aaw";
				}
				else if(infantry>=1 && cavalry>=1){	//Or if we have one of two different types use the wildcard as the third type.
					command = "icw";
				}
				else if(infantry>=1 && artillery>=1){
					command = "iaw";
				}
				else if(artillery>=1 && cavalry>=1){
					command = "caw";
				}
			}			
		}
		else if (player.isOptionalExchange()) {	
			int remainingCountries[] = getRemainingCountriesPerContinent();
			int lowest = 100;
			for(int i=0;i<remainingCountries.length;i++){	//	This calculates the lowest number of countries left to conquer on a continent.
				if(remainingCountries[i]<lowest && remainingCountries[i]!=0){
					lowest = remainingCountries[i];
				}
			}
			if(lowest>2){	// If we haven't nearly conquered a continent, skip trading in until later.
				command = "skip";
			}
			else{		// This is the same code as before.
				// trade in
				ArrayList<Card> cards = player.getCards();
				int[] cardids = new int[cards.size()];
				for(int i=0;i<cards.size();i++){
					cardids[i] = cards.get(i).getInsigniaId();
				}
				for(int i=0;i<cardids.length;i++){
					if(cardids[i]==0){
						infantry++;
					}
					else if(cardids[i]==1){
						cavalry++;
					}
					else if(cardids[i]==2){
						artillery++;
					}
					else if(cardids[i]==3){
						wildcard++;
					}
				}
				if(infantry>=3){
					command = "iii";
				}
				else if(cavalry>=3){
					command = "ccc";
				}
				else if(artillery>=3){
					command = "aaa";
				}
				else if(wildcard==0){
					command = "ica";
				}
				else if(wildcard>=1){
					if(infantry==2){
						command = "iiw";
					}
					else if(cavalry==2){
						command = "ccw";
					}
					else if(artillery==2){
						command = "aaw";
					}
					else if(infantry>=1 && cavalry>=1){
						command = "icw";
					}
					else if(infantry>=1 && artillery>=1){
						command = "iaw";
					}
					else if(artillery>=1 && cavalry>=1){
						command = "caw";
					}
				}
			}
		}
		else{
			command = "skip";
		}
		return(command);
	}

	public String getBattle () {  //same logic as reinforce
		String command = "";
		int remainingCountries[] = getRemainingCountriesPerContinent();
		int i;
		if(getNumContinentsOwned()==0){
			if(remainingCountries[4]<=2) command=getAttackString(4);
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command=getAttackString(i);		//if a continent has few countries left attack
			else if(remainingCountries[0]<=6) command=getAttackString(0);		//else if we have a few in NA
			else if(remainingCountries[5]<=3) command=getAttackString(5);		//--in AF
			else if(remainingCountries[1]<=4) command=getAttackString(1);		//--in EU
			else if(remainingCountries[2]<=5) command=getAttackString(2);		//--in AS
			else if(remainingCountries[4]<4) command=getAttackString(4);		//else if we have any in SA
			else if(remainingCountries[0]<9) command=getAttackString(0);		//-- in NA
			else if(remainingCountries[5]<6) command=getAttackString(5);		//-- in AF
			else if(remainingCountries[1]<7) command=getAttackString(1);		//-- in EU
			else if(remainingCountries[3]<4) command=getAttackString(3);		//-- in AU
			else command=getAttackString(2);									//-- in AS
		}	
		if(getNumContinentsOwned()==1){
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command=getAttackString(i);
			else if(remainingCountries[0]<=6&&remainingCountries[0]>0) command=getAttackString(0);		
			else if(remainingCountries[5]<=3&&remainingCountries[5]>0) command=getAttackString(5);		
			else if(remainingCountries[1]<=4&&remainingCountries[1]>0) command=getAttackString(1);		
			else if(remainingCountries[2]<=5&&remainingCountries[2]>0) command=getAttackString(2);				
			else if(remainingCountries[0]==0){
				if(remainingCountries[4]==4) command = getNewContinentAttackString(0,4);
				else command = getAttackString(4);
			} 
			else if(remainingCountries[4]==0){
				if(remainingCountries[0]==9) command = getNewContinentAttackString(4,0);
				else command = getAttackString(0);
			}
			else if(remainingCountries[5]==0){
				if(remainingCountries[4]==4) command = getNewContinentAttackString(5, 4);
				else command = getAttackString(4);
			}
			else if(remainingCountries[1]==0){
				if(remainingCountries[0]==9) command = getNewContinentAttackString(1, 0);
				else command = getAttackString(0);
			}
			else if(remainingCountries[2]==0){
				if(remainingCountries[3]==4) command = getNewContinentAttackString(2, 3);
				else command = getAttackString(3);
			}
			else if(remainingCountries[3]==0){
				if(remainingCountries[2]==12) command = getNewContinentAttackString(3, 2);
				else if(board.getOccupier(23)!=player.getId()){
					command = getNewContinentAttackString(3, 2);
				}
				else command = getAttackString(2);
			}
		}
		if(getNumContinentsOwned()==2){
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command=getAttackString(i);
			else if(remainingCountries[0]<=6&&remainingCountries[0]>0) command=getAttackString(0);		
			else if(remainingCountries[5]<=3&&remainingCountries[5]>0) command=getAttackString(5);		
			else if(remainingCountries[1]<=4&&remainingCountries[1]>0) command=getAttackString(1);		
			else if(remainingCountries[2]<=5&&remainingCountries[2]>0) command=getAttackString(2);				
			else if(remainingCountries[0]==0&&remainingCountries[4]==0){			//if NA and SA, attack AF
				if(remainingCountries[5]==6) command = getNewContinentAttackString(4,5);
				else command = getAttackString(5);
			}
			else if(remainingCountries[0]==0){			//if NA and not SA, attack SA
				if(remainingCountries[4]==4) command = getNewContinentAttackString(0,4);
				else command = getAttackString(4);
			}
			else if(remainingCountries[4]==0){			//if SA and not NA, attack NA
				if(remainingCountries[0]==9)command = getNewContinentAttackString(4,0);
				else command = getAttackString(0);
			}
			else if(remainingCountries[5]==0){			//if AF and not SA, attack SA
				if(remainingCountries[4]==4)command = getNewContinentAttackString(5,4);
				else command = getAttackString(4);
			}
			else if(remainingCountries[2]==0){			//if Asia
				if(remainingCountries[3]!=0){			//if not AU, attack AU
					if(remainingCountries[3]==4)command = getNewContinentAttackString(2,3);
					else command = getAttackString(3);
				}
				else{									//if Asia/AU, attack NA
					if(remainingCountries[0]==9)command = getNewContinentAttackString(2,0);
					else command = getAttackString(0);
				}
			}
			else{										//EU/AU is only combination left - attack AS
				if(remainingCountries[2]==0)command = getNewContinentAttackString(3,2);	
				else if(board.getOccupier(23)!=player.getId()){
					command = getNewContinentAttackString(3, 2);
				}
				else command = getAttackString(2);
			}
		}
		if(getNumContinentsOwned()==3){
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command=getAttackString(i);
			else if(remainingCountries[0]<=6&&remainingCountries[0]>0) command=getAttackString(0);		
			else if(remainingCountries[5]<=3&&remainingCountries[5]>0) command=getAttackString(5);		
			else if(remainingCountries[1]<=4&&remainingCountries[1]>0) command=getAttackString(1);		
			else if(remainingCountries[2]<=5&&remainingCountries[2]>0) command=getAttackString(2);					
			else if(remainingCountries[0]==0&&remainingCountries[4]==0){			//if NA and SA
				if(remainingCountries[5]!=0){			//if not AF, attack AF
					if(remainingCountries[5]==6) command = getNewContinentAttackString(4,5);	
					else command = getAttackString(5);
				}
				else if(remainingCountries[1]!=0){		//otherwise if not EU, attack EU
					if(remainingCountries[1]==7) command = getNewContinentAttackString(0,1);	
					else command = getAttackString(1);
				}
			}
			else if(remainingCountries[0]==0){			//if NA and not SA, attack SA
				if(remainingCountries[4]==4) command = getNewContinentAttackString(0,4);
				else command = getAttackString(4);
			}
			else if(remainingCountries[4]==0){			//if SA and not NA, attack NA
				if(remainingCountries[0]==9)command = getNewContinentAttackString(4,0);
				else command = getAttackString(0);
			}
			else if(remainingCountries[5]==0){			//if AF and not SA, attack SA
				if(remainingCountries[4]==4)command = getNewContinentAttackString(5,4);
				else command = getAttackString(4);
			}
			else{										//EU/AS/AU is only combination left - attack AF --WE CAN ADD MORE LOGIC HERE IF WE WANT
				if(remainingCountries[5]==6)command = getNewContinentAttackString(2,5);
				else command = getAttackString(5);
			}
		}
		if(getNumContinentsOwned()==4){
			for(i=0;i<6;i++)
				if(remainingCountries[i]<=2&&remainingCountries[i]>0) break;
			if(i<6&&remainingCountries[i]<=2&&remainingCountries[i]>0) command=getAttackString(i);
			else if(remainingCountries[0]<=6&&remainingCountries[0]>0) command=getAttackString(0);		
			else if(remainingCountries[5]<=3&&remainingCountries[5]>0) command=getAttackString(5);		
			else if(remainingCountries[1]<=4&&remainingCountries[1]>0) command=getAttackString(1);		
			else if(remainingCountries[2]<=5&&remainingCountries[2]>0) command=getAttackString(2);		
			else if(remainingCountries[4]!=0){			//if not SA
				if(remainingCountries[0]==0){			//if we have NA, go from there
					if(remainingCountries[4]==4) command = getNewContinentAttackString(0,4);	
					else command = getAttackString(4);
				}
				else{									//otherwise go from AF
					if(remainingCountries[4]==4) command = getNewContinentAttackString(5,4);	
					else command = getAttackString(4);
				}
			}
			else if(remainingCountries[0]!=0){			//if not NA
				if(remainingCountries[0]==9) command = getNewContinentAttackString(4,0);		//attack from SA
				else command = getAttackString(0);
			}
			else if(remainingCountries[5]!=0){			//if not AF
				if(remainingCountries[5]==6) command = getNewContinentAttackString(4,5);		//attack from SA
				else command = getAttackString(5);
			}
			else if(remainingCountries[1]!=0){			//if not EU
				if(remainingCountries[0]==7) command = getNewContinentAttackString(5,1);		//attack from AF
				else command = getAttackString(1);
			}
			else{										//NA/SA/AF/EU is only combination left
				if(remainingCountries[2]==12) command = getNewContinentAttackString(0,2);		//attack AS from NA
				else command = getAttackString(2);
			}
		}
		if(getNumContinentsOwned()==5){
			if(remainingCountries[0]!=0){				//if not NA
				if(remainingCountries[0]==9) command = getNewContinentAttackString(4,0);		//attack from SA
				else command = getAttackString(0);
			}
			else if(remainingCountries[1]!=0){			//if not EU
				if(remainingCountries[1]==7) command = getNewContinentAttackString(2,1);		//attack from AS
				else command = getAttackString(1);
			}
			else if(remainingCountries[2]!=0){			//if not AS
				if(remainingCountries[2]==12) command = getNewContinentAttackString(1,2);		//attack from EU
				else command = getAttackString(2);
			}
			else if(remainingCountries[3]!=0){			//if not AU
				if(remainingCountries[3]==4) command = getNewContinentAttackString(2,3);		//attack from AS
				else command = getAttackString(3);
			}
			else if(remainingCountries[4]!=0){			//if not SA
				if(remainingCountries[4]==4) command = getNewContinentAttackString(0,4);		//attack from NA
				else command = getAttackString(2);
			}
			else{										//if not AF
				if(remainingCountries[5]==6) command = getNewContinentAttackString(1,5);		//attack from EU
				else command = getAttackString(5);
			}
		}

		if (command.equals("")){
			command = "skip";
		}
		return(command);
	}

	public String getDefence (int countryId) {		// For defence we just use as many troops as we can to defend.
		String command = "";
		int units = board.getNumUnits(countryId);
		if(units>=2)
			command = "2";
		else
			command = "1";
		return(command);
	}

	public String getMoveIn (int attackCountryId) {	
		String command = "";
		int[] adjacentcountries = this.getSurrounding(attackCountryId);	//We check the countries surrounding the attacking country.
		int check=0;
		for(int i=0;i<adjacentcountries.length;i++){
			if(adjacentcountries[i]!=player.getId()){	// Checks if any surrounding countries are not ours.
				check=1;
			}
		}
		if(board.getNumUnits(attackCountryId)>=20){
			command = Integer.toString(board.getNumUnits(attackCountryId)/2);
		}
		else if(check==1)	// If there are some enemy countries, don't pass any in.
			command = "0";
		else	// Otherwise if all the countries are ours, pass all troops in.
			command = Integer.toString(board.getNumUnits(attackCountryId)-1);
		return(command);
	}

	public String getFortify () {
		String command = "", temp = "";
		int[] countriesowned = getCountriesOwned();	// Gets the bots countries
		int[] numadjacentenemycountries = new int[countriesowned.length];	// Gets the number of adjacent enemy countries next to each of the bots ones.
		for(int i=0;i<countriesowned.length;i++){
			numadjacentenemycountries[i] = this.getNumAdjacentEnemyCountries(countriesowned[i]);
		}
		for(int i=0;i<numadjacentenemycountries.length;i++){
			if(numadjacentenemycountries[i]==0 && board.getNumUnits(countriesowned[i])>1){
					for(int j=0;j<numadjacentenemycountries.length;j++){	// If we find a country that has more than 1 troop and is surrounded only by more of the bots countries, move all the troops to a connected country with some enemy countries adjacent to it.
						if(j!=i && this.getNumAdjacentEnemyCountries(countriesowned[j])!=0 && board.isConnected(countriesowned[i], countriesowned[j]) && board.getNumUnits(countriesowned[j])<=20){
							command += GameData.COUNTRY_NAMES[countriesowned[i]];
							command = command.replaceAll("\\s", "");
							temp = GameData.COUNTRY_NAMES[countriesowned[j]];
							temp = temp.replaceAll("\\s", "");
							command += " " + temp;
							command += " " + Integer.toString(board.getNumUnits(countriesowned[i])-1);
							return command;
						}
					}
			}
		}
		
		command = "skip";
		return command;
	}
	
	private int[] getRemainingCountriesPerContinent(){	// This calculates the number of countries per continent left to conquer.
		int[] remainingCountries = new int[6];
		
		int count=0;
		
		for(int i=0; i<9;i++){	// North America
			if(player.getId()!=board.getOccupier(i)){
				count++;
			}
		}
		remainingCountries[0] = count;
		count=0;
		
		for(int i=9; i<16;i++){	// Europe
			if(player.getId()!=board.getOccupier(i)){
				count++;
			}
		}
		remainingCountries[1] = count;
		count=0;
		
		for(int i=16; i<28;i++){	// Asia
			if(player.getId()!=board.getOccupier(i)){
				count++;
			}
		}
		remainingCountries[2] = count;
		count=0;
		
		for(int i=28; i<32;i++){	// Australia
			if(player.getId()!=board.getOccupier(i)){
				count++;
			}
		}
		remainingCountries[3] = count;
		count=0;
		
		for(int i=32; i<36;i++){	// South America
			if(player.getId()!=board.getOccupier(i)){
				count++;
			}
		}
		remainingCountries[4] = count;
		count=0;
		
		for(int i=36; i<42;i++){	// Africa
			if(player.getId()!=board.getOccupier(i)){
				count++;
			}
		}
		remainingCountries[5] = count;
		count=0;
		
		return remainingCountries;
	}
	
	private int[] getSurrounding(int countryId){	// This returns an array of the owners of the countries surrounding the one entered.
		int[] adjacentcountries = new int[GameData.ADJACENT[countryId].length];
		for(int i=0;i<GameData.ADJACENT[countryId].length;i++){
			adjacentcountries[i] = board.getOccupier(GameData.ADJACENT[countryId][i]);
		}
		return adjacentcountries;
	}
	
	private int getNumAdjacentEnemyCountries(int countryId){	// This returns the number of countries surrounding the one selected that aren't yours.
		int count=0;
			int[] temp = this.getSurrounding(countryId);
			for(int j=0;j<temp.length;j++){
				if(temp[j] != player.getId()){
					count++;
				}
			}
		return count;
	}
	
	private int[] getCountriesOwned(){	// This returns an array with all your owned countries.
		int countcountries=0;
		for(int i=0;i<42;i++){
			if(board.getOccupier(i) == player.getId()){
				countcountries++;
			}
		}
		int[] countriesowned = new int[countcountries];
		int place=0;
		for(int i=0;i<42;i++){
			if(board.getOccupier(i) == player.getId()){
				countriesowned[place] = i;
				place++;
			}
		}
		return countriesowned;
	}
	
	private int getNumContinentsOwned(){	//returns the number of continents the bot owns
		int count = 0;
		int countries[] = getRemainingCountriesPerContinent();
		for(int i=0;i<6;i++)
			if(countries[i]==0) count++;
		return count;
	}
	
	private String getAttackString(int continent){	//gets a valid attack string, taking a continent as input
		int countriesOwned[] = getCountriesOwned();
		int i,j;
		String command = "", temp;
		boolean check;
		boolean check2 = false;
		for(i=0;i<countriesOwned.length&&!check2;i++){	//looks for territory to attack from
			check = false;
			if(GameData.CONTINENT_IDS[countriesOwned[i]]==continent&&getNumAdjacentEnemyCountries(countriesOwned[i])>0){
				command = GameData.COUNTRY_NAMES[countriesOwned[i]];
				command = command.replaceAll("\\s", "");
				check = true;
			}
			if(check){									//looks for territory to attack
				int adjacentCountries[] = getSurrounding(countriesOwned[i]);
				for(j=0;j<adjacentCountries.length&&!check2;j++){
					if((adjacentCountries[j]!=player.getId())&&(GameData.CONTINENT_IDS[countriesOwned[i]]==GameData.CONTINENT_IDS[GameData.ADJACENT[countriesOwned[i]][j]])&&((board.getNumUnits(countriesOwned[i])-board.getNumUnits(GameData.ADJACENT[countriesOwned[i]][j]))>=2)){
						temp = GameData.COUNTRY_NAMES[GameData.ADJACENT[countriesOwned[i]][j]];
						temp = temp.replaceAll("\\s", "");
						command += " " + temp;
						if(board.getNumUnits(countriesOwned[i])>=4) command += " 3";
						else command += " 2";
						check2 = true;
					}
				}
			}
		}
		if(check2) return command;
		else return "skip";
	}
	
	private String getNewContinentAttackString(int con1, int con2){	//same as above but for attacking a territory in a different continent
		int countriesOwned[] = getCountriesOwned();
		int i,j;
		String command = "", temp;
		boolean check;
		boolean check2 = false;
		for(i=0;i<countriesOwned.length&&!check2;i++){
			check = false;
			if(GameData.CONTINENT_IDS[countriesOwned[i]]==con1&&getNumAdjacentEnemyCountries(countriesOwned[i])>0){
				command = GameData.COUNTRY_NAMES[countriesOwned[i]];
				command = command.replaceAll("\\s", "");
				check = true;
			}
			if(check){
				int adjacentCountries[] = getSurrounding(countriesOwned[i]);
				for(j=0;j<adjacentCountries.length&&!check2;j++){
					if((adjacentCountries[j]!=player.getId()&&con2==GameData.CONTINENT_IDS[GameData.ADJACENT[countriesOwned[i]][j]])&&((board.getNumUnits(countriesOwned[i])-board.getNumUnits(GameData.ADJACENT[countriesOwned[i]][j]))>=2)){
						temp = GameData.COUNTRY_NAMES[GameData.ADJACENT[countriesOwned[i]][j]];
						temp = temp.replaceAll("\\s", "");
						command += " " + temp;
						if(board.getNumUnits(countriesOwned[i])>=4) command += " 3";
						else command += " 2";
						check2 = true;
					}
				}
			}
		}
		if(check2) return command;
		else return "skip";
	}
	
	private String getReinforcementStringForAttack(int continent){		//takes a continent as input and finds a territory to reinforce using same logic as attack calculation
		int countriesOwned[] = getCountriesOwned();
		int i, j;
		String command = "";
		boolean check = false;
		boolean check2 = false;
		for(i=0;i<countriesOwned.length&&!check2;i++){
			if(GameData.CONTINENT_IDS[countriesOwned[i]]==continent&&getNumAdjacentEnemyCountries(countriesOwned[i])>0){
				command = GameData.COUNTRY_NAMES[countriesOwned[i]];
				command = command.replaceAll("\\s", "");
				command += " " + player.getNumUnits();
				check = true;
				if(check){
					int adjacentCountries[] = getSurrounding(countriesOwned[i]);
					for(j=0;j<adjacentCountries.length;j++){
						if((adjacentCountries[j]!=player.getId())&&(GameData.CONTINENT_IDS[countriesOwned[i]]==GameData.CONTINENT_IDS[GameData.ADJACENT[countriesOwned[i]][j]]))
							check2 = true;
					}
				}
			}
		}
		return command;
	}
}
