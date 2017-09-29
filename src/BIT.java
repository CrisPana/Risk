
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

// put your code here

public class BIT implements Bot {
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects
	// So you can use player.getNumUnits() but you can't use player.addUnits(10000), for example
	
	private BoardAPI board;
	private PlayerAPI player;
	
	private Vector<Integer> countriesControlled;
	private final int[] entryPoints= 	{4,7,8,//N. America
										 10,11,12,14,//Europe
										 16,18,20,22,23,//Asia
										 31,//Australia
										 32,34,//S. America
										 37,39,40};//Africa
	//private boolean attackOngoing;
	//private int attackingCountry;
	private int countryAttacked;
	
	BIT (BoardAPI inBoard, PlayerAPI inPlayer) {
		board = inBoard;	
		player = inPlayer;
		countriesControlled= new Vector<Integer>();
		return;
	}
	
	public String getName () {
		String command = "BIT";
		return command;
	}

	public String getReinforcement () {
		getCountriesControlled();
		String command = "";
		int weakestCountry= getWeakestCountry();
		int countryToReinforce= getCountryToReinforce(weakestCountry);
		// put your code here
		command = GameData.COUNTRY_NAMES[countryToReinforce];
		command = command.replaceAll("\\s", "");
		command += " 1";
		return(command);
	}
	
	public String getPlacement (int forPlayer) {
		String command = "";
		int enemyToPlaceNextToo= getPlaceNextTo(forPlayer);
		int placement= getCountryToPlaceOn(enemyToPlaceNextToo, forPlayer);
		command=GameData.COUNTRY_NAMES[placement].replaceAll("\\s", "");
		return(command);
	}
	
	public String getCardExchange () {
		String command = "";
		int infantryCount = 0;
		int cavalryCount = 0;
		int artilleryCount = 0;
		int wildCount = 0;
		ArrayList<Card> playerCards = new ArrayList<Card>(player.getCards());
		if(playerCards.size() >= 3){
			for(int i =0; i<playerCards.size(); i++){
				if(playerCards.get(i).getInsigniaId() == 0){
					infantryCount++;
				}
				else if(playerCards.get(i).getInsigniaId() == 1){
					cavalryCount++;
				}
				else if(playerCards.get(i).getInsigniaId() == 2){
					artilleryCount++;
				}
				else if(playerCards.get(i).getInsigniaId() == 3){
					wildCount++;
				}
			}
			if(wildCount >= 3){
				command = "www";
			}
			else if(infantryCount >= 3){
				command = "iii";
			}
			else if(cavalryCount >= 3){
					command = "ccc";		
			}	
			else if(artilleryCount >= 3){
					command = "aaa";		
			}
			else if(infantryCount >= 1 && cavalryCount >= 1 && artilleryCount >= 1){
					command = "aci";
			}
			else if(infantryCount >= 1 && cavalryCount >= 1 && wildCount >= 1){
					command = "icw";
			}
			else if(cavalryCount >= 1 && artilleryCount >= 1&&  wildCount >= 1){
					command = "acw";
			}
			else if(artilleryCount >= 1 && infantryCount >= 1 &&  wildCount >= 1 ){
					command = "aiw";
			}
			else if(infantryCount >= 2 && wildCount >= 1)
			{
				command = "iiw";	
			}
			else if(cavalryCount >= 2 && wildCount >= 1)
			{
				command = "ccw";
			}
			else if(artilleryCount >= 2 && wildCount >= 1)
			{
				command = "aaw";
			}
			else if(infantryCount >= 1 && wildCount >= 2)
			{
				command = "iww";		
			}
			else if(cavalryCount >= 1 && wildCount >= 2)
			{
				command = "cww";	
			}
			else if(artilleryCount >= 1 && wildCount >= 2)
			{
				command = "aww";	
			}
			else{
				command = "skip";
			}
		}
		else{
			command = "skip";
		}
		return(command);
	}
	

	public String getBattle () {
		Vector<Integer> possiblities= getPossibleTargets();
		int countryToAttack= getCountryToAttack(possiblities);
		
		int countryToAttackfrom= getCountryToAttackFrom(countryToAttack);
		
		String unitsToAttackWith;
		if(board.getNumUnits(countryToAttackfrom)>3){
			unitsToAttackWith=" 3";
		}else if(board.getNumUnits(countryToAttackfrom)==3){
			unitsToAttackWith=" 2";
		}else if(board.getNumUnits(countryToAttackfrom)==2){
			unitsToAttackWith=" 1";
		}else{
			return "skip";
		}
		return GameData.COUNTRY_NAMES[countryToAttackfrom].replaceAll("\\s+","")+" "+GameData.COUNTRY_NAMES[countryToAttack].replaceAll("\\s+","")+
				unitsToAttackWith;
	}

	public String getDefence (int countryId) {
		String command = "";
		if(board.getNumUnits(countryId) > 1){
			command = "2";
		}
		
		else if(board.getNumUnits(countryId)==1){
			command = "1";
		}
		
		else{
			command = "1";
		}
		return(command);
	}

	public String getMoveIn (int attackCountryId) {//attackCountryId is the country the attack was launched from
		countriesControlled.add(countryAttacked);
		String command = "";
		int attackCountryMin=calculateVulnerabilityThreshHold(attackCountryId);
		int moveIntoMin=calculateVulnerabilityThreshHold(countryAttacked);
		if(board.getNumUnits(countryAttacked)<moveIntoMin){//Case where the attacked country is vulnerable
			//Priority is given to reinforcing the newly controlled country
			if(board.getNumUnits(attackCountryId)-1<=moveIntoMin)
				command=Integer.toString(board.getNumUnits(attackCountryId)-1);
			else
				command=Integer.toString(moveIntoMin-board.getNumUnits(countryAttacked));
		}else if(board.getNumUnits(attackCountryId)<attackCountryMin){//If the attacking country is already vulnerable don't move any armies
			command="0";
		}else{//Default case, Move half the armies to the new country
			command=Integer.toString(board.getNumUnits(attackCountryId)/2);
		}
		
		return command;
	}

	public String getFortify () {
		String command = "";
		Vector<Integer> possibleTofortify= getPossibleFortifiactions();
		int continentToFortify= getContinentToFortify(possibleTofortify);
		if(continentToFortify==-1){
			return "skip";
		}
		int countryToFortify=getCountryToFortify(continentToFortify,possibleTofortify);
		if(countryToFortify==-1){
			return "skip";
		}
		String countryToFortifyFrom=getFortifyFrom(countryToFortify);
		if(countryToFortifyFrom==null)
			command = "skip";
		else
			command=GameData.COUNTRY_NAMES[countryToFortify].replaceAll("\\s+","")+" "+countryToFortifyFrom;
		return(command);
	}
	
	//This method calculates the minimum amount of armies we would like to have present in a country
	//in order to defend it adequately i.e. at least equal to the number of armies in bordering
	//opponent's countries
	private int calculateVulnerabilityThreshHold(int countryId){
		int numArmiesAtPresent=board.getNumUnits(countryId);
		int numNeeded=0;//The number of armies we need to increase by to prevent our country from being vulnerable
		for(int i=0; i<GameData.ADJACENT[countryId].length; i++){
			//Check if the bordering country is controlled by the opponent and they have more armies than us on it
			if(board.getOccupier(GameData.ADJACENT[countryId][i])==0 && 
					board.getNumUnits(GameData.ADJACENT[countryId][i])>numArmiesAtPresent){
				numNeeded=board.getNumUnits(GameData.ADJACENT[countryId][i])-numArmiesAtPresent;
			}
		}
		return numNeeded;
	}
	
	//This method returns a vector containing all of the countries we own
	//that can possibly be fortified i.e. countries which are adjacent to
	//at least one other country we own
	private Vector<Integer> getPossibleFortifiactions(){
		Vector<Integer> possibilities= new Vector<Integer>();
		
		Iterator<Integer> it= countriesControlled.iterator();
		
		while(it.hasNext()){//Iterate through every country we own
			int countryId= (int) it.next();
			for(int i=0; i<GameData.ADJACENT[countryId].length;i++){//Check every country adjacent to the ones we own
				if(board.getOccupier(GameData.ADJACENT[countryId][i])==1){//If we find two of our countries next to each other add it to the list
					possibilities.add(GameData.ADJACENT[countryId][i]);
					break;
				}
			}
		}
		return possibilities;
	}
	
	private void getCountriesControlled(){
		countriesControlled= new Vector<Integer>();
		
		for(int i=0; i<42; i++){//Get a list of all the countries we own
			if(board.getOccupier(i)==1)
				countriesControlled.add(i);
		}
	}
	
	//This method will decide the continent on which we should fortify a country
	//It does this by analyzing the  ratio of countries owned on each continent
	//to the number of countries on the continent and chooses the highest one
	private int getContinentToFortify(Vector<Integer> possibilities){
		if(possibilities==null){//case where there are no possible countries we wish to fortify
			return -1;
		}
		int countriesOnContinents[]= {0,0,0,0,0};//Counters for the number of countries we own in each continent
		int countryCounts[]= {9, 7, 12, 4, 4, 6};//The number of countries on each continent
		double ratios[]= {0,0,0,0,0};
		
		for(int i=0; i<5; i++){//Go through each of the 5 continents
			Iterator<Integer> it= possibilities.iterator();
			while(it.hasNext()){//Go through every country we own
				int countryId= (int) it.next();
				for(int j=0; j<GameData.ADJACENT[i].length; j++){//Go through every country on each continent to see if we own it
					if(GameData.ADJACENT[i][j]==countryId){//If we do own it increment the counter for that continent
						countriesOnContinents[i]++;
						//possibilities.remove((Integer)countryId);//Remove that country from the vector as it will never be counted again
						break;
					}
						
				}
			}
		}
		
		for(int i=0; i<5; i++){//Calculate the ratios for each country
			ratios[i]= countriesOnContinents[i]/countryCounts[i];
		}
		
		int largest=0;//Largest corresponds to the continentId with the highest ratio
		for(int i=1; i<5; i++){//find the continentId with the highest ratio
			if(ratios[i]>ratios[largest]){
				largest=i;
			}
		}
		
		return largest;
	}
	
	//This method takes in the continentId of the continent on which we want to fortify a country
	//It also takes in a Vector containing all possible countries that can be fortified i.e.
	//all countries which have another one which we own adjacent to it and can move armies from
	//The method then compares all the countries on that continent to decide which one to fortify
	private int getCountryToFortify(int continentId, Vector<Integer> possibilities){
		Vector<Integer> countries= new Vector<Integer>();
		Iterator<Integer> it= possibilities.iterator();
		int[] countriesOnContinent= GameData.CONTINENT_COUNTRIES[continentId];
		while(it.hasNext()){
			int countryId=it.next();
			for(int i=0; i<countriesOnContinent.length; i++){//Get all the countries in the chosen continent that we own
				if(countriesOnContinent[i]==countryId){
					countries.add(countryId);
					break;
				}
			}
		}
		
		int[] vulnerabilities= new int[countries.size()];
		//Get the minimum number we would like to have on each country 
		//in order to defend it adequately
		for(int i=0; i<countries.size();i++){
			vulnerabilities[i]=calculateVulnerabilityThreshHold(countries.get(i));
			//In the case where the country already has adequate armies on it to defend
			//itself then set it's vulnerability to 0
			if(vulnerabilities[i]<=board.getNumUnits(countries.get(i)))
				vulnerabilities[i]=0;
		}
		
		int mostVulnerable=0;
		for(int i=1; i<vulnerabilities.length; i++){//compare each country's vulnerability and find the most vulnerable one
			if(vulnerabilities[i]>mostVulnerable)
				mostVulnerable=i;
			else if(vulnerabilities[i]==mostVulnerable){//In the case where 2 countries are equally vulnerable call the compareVulnerabilities method
				//We must enter countryIds to this method rather than the vulnerability figures
				int comparison= compareVulnerabilities(countries.get(mostVulnerable),countries.get(i));
				if(comparison==countries.get(i))
					mostVulnerable=i;
			}
		}
		
		//We must remove all spaces from the string as spaces will not be accepted in the fortify command
		if(countries.size()==0){
			return -1;
		}else{
			return countries.get(mostVulnerable);
		}
	}
	
	//This method takes in a countryId and checks if it is an entry
	//point to a continent or not and returns a boolean corresponding
	//to the result
	private boolean isEntryPoint(int countryId){
		boolean entryPoint=false;
		
		for(int i=0; i<entryPoints.length;i++){
			if(countryId==entryPoints[i]){
				entryPoint=true;
				break;
			}
		}
		return entryPoint;
	}
	
	//This method takes in 2 countryIds which are deemed to be equally as vulnerable and
	//decides which one should be prioritised
	//This decision is made depending on if either country is an entry point to a continent 
	//and which one has more enemy countries surrounding it
	public int compareVulnerabilities(int country1, int country2){
		int mostVulnerable=-1;
		int numEnemiesAdjacent[]={0,0};
		int countries[]= {country1, country2};
		
		for(int i=0; i<2; i++){//Calculate the number of enemy controlled countries (non-neutral) adjacent to each country
			for(int j=0; j<GameData.ADJACENT[countries[i]].length; j++){
				if(board.getOccupier(GameData.ADJACENT[countries[i]][j])==0)
					numEnemiesAdjacent[i]++;
			}
		}
		
		if(isEntryPoint(country1) && !isEntryPoint(country2)){//Check if country1 is an entry point to a continent
			mostVulnerable=country1;
		}else if(!isEntryPoint(country1) && isEntryPoint(country2)){//Check if country2 is an entry point to a continent
			mostVulnerable=country2;
		}else if(numEnemiesAdjacent[0]>numEnemiesAdjacent[1]){//Check if country1 has more enemies surrounding it
			mostVulnerable=country1;
		}else{//Case where country2 has more enemies surrounding it and base case where they are equal
			mostVulnerable=country2;
		}
		
		return mostVulnerable;
	}
	
	//This method takes the country which we wish to fortify as input and returns
	//The most ideal country to fortify it from as well as the number of units to fortify
	//with
	private String getFortifyFrom(int toFortify){
		Vector<Integer> possibilities= new Vector<Integer>();
		Iterator<Integer> it= countriesControlled.iterator();
		String fortifyFrom;
		while(it.hasNext()){
			int countryId=(int) it.next();
			if(board.isAdjacent(countryId, toFortify))
				possibilities.add(countryId);
		}
		fortifyFrom=getMostSuitable(possibilities);
		return fortifyFrom;
	}
	
	//This method takes all possible countries which can fortify as input
	//and returns the one which it is most suitable from and the number
	//of units to fortify with as a string
	//This is done by checking through all of the countries and checking which has the largest
	//number of surplus units i.e. how many more than than the ideal number for defending
	//in the case where none of the countries hav surplus units null is returned
	private String getMostSuitable(Vector<Integer> possibilities){
		String mostSuitable="";
		int surplus[]= new int[possibilities.size()];
		
		for(int i=0; i<surplus.length; i++){//Calculate the amount of armies each country has more than they need
			//Number of units actually on the country- ideal number for defence
			surplus[i]=board.getNumUnits(possibilities.get(i))-calculateVulnerabilityThreshHold(possibilities.get(i));
		}
		
		int largestSurplus=0;
		for(int i=1; i<surplus.length;i++){
			if(surplus[i]>largestSurplus){
				largestSurplus=i;
			}else if(surplus[i]==largestSurplus){
				int comparison= compareVulnerabilities(possibilities.get(largestSurplus),possibilities.get(i));
				if(comparison!=possibilities.get(i))
					largestSurplus=i;
			}
		}
		
		if(surplus[largestSurplus]<1){//Case where no country can afford to move any armies
			mostSuitable=null;
		}else{
			mostSuitable=GameData.COUNTRY_NAMES[possibilities.get(largestSurplus)].replaceAll("\\s+","")+" "+Integer.toString(surplus[largestSurplus]);
		}
		return mostSuitable;
	}
	
	//This method returns every possible country which could be attacked from
	//any of the countries we own
	private Vector<Integer> getPossibleTargets(){
		Vector<Integer> targets= new Vector<Integer>();
		for(int i=0; i<countriesControlled.size();i++){
			for(int j=0; j<GameData.ADJACENT[countriesControlled.get(i)].length;j++){
				if(board.getOccupier(GameData.ADJACENT[countriesControlled.get(i)][j])!=1)
					targets.add(GameData.ADJACENT[countriesControlled.get(i)][j]);
			}
		}
		removeDuplicates(targets);//ensure there are no duplications of countries
		return targets;
	}
	
	//This method removes any duplicate values from a vector
	private void removeDuplicates(Vector<Integer> vector){
		for(int i=0;i<vector.size()-1; i++){
			for(int j=i+1;j<vector.size();j++){
				if(vector.get(i)==vector.get(j))
					vector.remove(j);
			}
		}
	}
	
	private int getCountryToAttack(Vector<Integer> possibilities){
		int weakest=possibilities.get(0);
		
		for(int i=1; i<possibilities.size();i++){
			if(board.getNumUnits(possibilities.get(i))<board.getNumUnits(weakest)){
				weakest=possibilities.get(i);
			}
			else if(board.getNumUnits(possibilities.get(i))==board.getNumUnits(weakest)){
				if(isEntryPoint(possibilities.get(i))&&!isEntryPoint(weakest))
					weakest=possibilities.get(i);
			}
		}
		return weakest;
	}
	//This method takes in a vector containing all possible countries that we can attack
	//The method then reduces this list to the countries which we are most likely to
	//to win a battle against should we attack (in this case winning is considered to be
	//that after a number of attacks we take control of the country we are attacking)
	/*private Vector<Integer> reducePossibilities(Vector<Integer> possibilities){
		System.out.println("Printing from reducePossibilities");
		boolean[] possibleToAttackFrom= new boolean[possibilities.size()];
		System.out.println(possibilities.size());
		Iterator<Integer> it= possibilities.iterator();
		int i=0;
		while(it.hasNext()){
			int countryId=(int) it.next();
			possibleToAttackFrom[i]=checkAdjacent(countryId);
			System.out.println(checkAdjacent(countryId));
		}
		
		for(int j=0; j<possibilities.size(); j++){
			System.out.println(possibleToAttackFrom[j]);
		}
		
		Vector<Integer> reducedPossibilities= new Vector<Integer>();
		for(int j=0; j<possibilities.size();j++){
			if(possibleToAttackFrom[j])
				reducedPossibilities.add(j);
		}
		return reducedPossibilities;
	}*/

	//This method takes in an enemy's countryId and checks every country around that to see
	//which ones around it which we own that we would like to attack from
	//This is decided by checking countries which have more armies than that country
	//and ensuring that they have more than 5 armies as in the case where we have less than
	//5 arimes the dfender is more likely to win
	/*private boolean checkAdjacent(int countryId){
		boolean possibleToAttack=false;
		for(int i=0;i<GameData.ADJACENT[countryId].length;i++){
			if(board.getOccupier(GameData.ADJACENT[countryId][i])==1 && 
					board.getNumUnits(GameData.ADJACENT[countryId][i])>board.getNumUnits(countryId)){
				possibleToAttack=true;
			}
		}
		System.out.println("Printing from checkAdjacent");
		System.out.println(countryId);
		System.out.println(possibleToAttack);
		
		
	
		
		return possibleToAttack;
	}*/
	
	//This method takes in a list of possible countries for us to attack
	//the method then checks through the list to decide the best country to attack
	//depending on which country would give us the most reinforcements on our
	//next turn should we take it over
	/*private int getCountryToAttack(Vector<Integer> possibilities){
		int countryToAttack;
		int possibleReinforcementsGained[]= new int[possibilities.size()];
		for(int i=0; i<possibilities.size();i++){
			possibleReinforcementsGained[i]=getPossibleReinforcementsIncrease(possibilities.get(i));
		}
		
		int largest=0;
		for(int i=1; i<possibleReinforcementsGained.length;i++){
			if(possibleReinforcementsGained[i]>largest){
				largest=i;
			}else if(possibleReinforcementsGained[i]==largest){
				if(isEntryPoint(possibilities.get(i)))
					largest=i;
			}
		}
		countryToAttack=possibilities.get(largest);
		return countryToAttack;
	}*/
	
	//This method takes in a countryId as input and calculates how much our reinforcements
	//would increase by if we took over that country
	/*private int getPossibleReinforcementsIncrease(int countryId){
		int possibleIncrease;
		int continentId=GameData.CONTINENT_IDS[countryId];
		boolean wouldControlContinent=true;
		for(int i=0; i<GameData.CONTINENT_COUNTRIES[continentId].length;i++){
			//Check to see if there are any other countries on the continent other than the one we are attacking
			//which we do not own
			if(board.getOccupier(GameData.CONTINENT_COUNTRIES[continentId][i])!=1 && 
					GameData.CONTINENT_COUNTRIES[continentId][i]!=countryId){
				wouldControlContinent=false;
			}
		}
		
		//Check if the number of reinforcements after the attack (excluding continent bonus)
		//would be greater than the number of reinforcements received after the attack
		int reinforcementsBefore =countriesControlled.size()/3;
		int reinforcementsAfter = (countriesControlled.size()+1)/3;
		
		if(wouldControlContinent){
			possibleIncrease= GameData.CONTINENT_VALUES[continentId]+reinforcementsBefore-reinforcementsAfter;
		}else{
			possibleIncrease= reinforcementsBefore-reinforcementsAfter;
		}
		
		return possibleIncrease;
	}*/
	
	//This method takes in the countryId of the country we would like to attack
	//The method then decides which is the most suitable country to attack with
	//This is decided by choosing the adjacent country which we own that
	//has the highest number of units on it
	private int getCountryToAttackFrom(int countryId){
		int countryToAttackFrom;
		Vector<Integer> possibilities= new Vector<Integer>();
		
		for(int i=0; i<GameData.ADJACENT[countryId].length;i++){
			if(board.getOccupier(GameData.ADJACENT[countryId][i])==1)
				possibilities.add(GameData.ADJACENT[countryId][i]);
		}
		
		int mostArmies=0;
		for(int i=0; i<possibilities.size();i++ ){
			if(board.getNumUnits(possibilities.get(i))>board.getNumUnits(possibilities.get(mostArmies)))
				mostArmies=i;
				
		}
		
		countryToAttackFrom=possibilities.get(mostArmies);
		return countryToAttackFrom;
	}
	
	//This method takes in the playerId of the player we want to place armies for 
	//and chooses the enemy country beside which we would like to place these armies
	//i.e. the stronges one
	private int getPlaceNextTo(int playerId){
		
		Vector<Integer> playerControls= new Vector<Integer>();
		for(int i=0; i<42; i++){
			if(board.getOccupier(i)==playerId)
				playerControls.add(i);
		}
		
		Vector<Integer> enemiesAdjacent= new Vector<Integer>();
		Iterator<Integer> it= playerControls.iterator();
		while(it.hasNext()){
			int countryId=it.next();
			for(int i= 0; i<GameData.ADJACENT[countryId].length;i++){
				if(board.getOccupier(GameData.ADJACENT[countryId][i])==0)
					enemiesAdjacent.add(GameData.ADJACENT[countryId][i]);
			}
		}
		
		//Check for the rare case where none of that player's armies are adjacent to
		//One of the enemy's countries
		if(!enemiesAdjacent.isEmpty()){
			it= enemiesAdjacent.iterator();
			it.next();//Skip the first element in the vector
		}
		else{
			for(int i=0; i<42; i++){
				if(board.getOccupier(i)==playerId)
					return GameData.ADJACENT[i][0];
			}
		}
		
		int strongest=0;//the strongest enemy country we can place next to
		int i=1;
		while(it.hasNext()){
			int countryId=it.next();
			if(board.getNumUnits(countryId)>board.getNumUnits(enemiesAdjacent.get(strongest)))
				strongest=i;
			else if(board.getNumUnits(countryId)==board.getNumUnits(enemiesAdjacent.get(strongest))){
				if(isEntryPoint(countryId)&& !isEntryPoint(enemiesAdjacent.get(strongest)))
					strongest=i;
			}
				
			i++;
		}
		
		return enemiesAdjacent.get(strongest);
	}
	
	//This method takes in the enemy country we would like to place armies next to
	//and the Player for whom we want to place armies
	//The method then finds all possible positions we could place these armies
	//and the chooses one at random
	private int getCountryToPlaceOn(int nextTo, int playerFor){
		Vector<Integer> possibilities= new Vector<Integer>();
		for(int i=0; i<GameData.ADJACENT[nextTo].length;i++){
			if(board.getOccupier(GameData.ADJACENT[nextTo][i])==playerFor)
				possibilities.add(GameData.ADJACENT[nextTo][i]);
		}
		
		return possibilities.get((int)(Math.random() * possibilities.size()));
		
	}
	
	private int getWeakestCountry(){
		int weakestCountry;
		Vector<Integer> possibilities =new Vector<Integer>();
		Iterator<Integer> it= countriesControlled.iterator();
		
		//Get every adjacent country to each country we own that is not controlled by us
		while(it.hasNext()){
			int countryId=it.next();
			for(int i=0; i<GameData.ADJACENT[countryId].length;i++){
				if(board.getOccupier(GameData.ADJACENT[countryId][i])!=1)
					possibilities.add(GameData.ADJACENT[countryId][i]);
			}
		}
		
		
		removeDuplicates(possibilities);
		
		//Find the lowest number of units present on a country
		it=possibilities.iterator();
		int lowest=board.getNumUnits(it.next());
		while(it.hasNext()){
			int units=board.getNumUnits(it.next());
			if(units<lowest)
				lowest=units;
		}
		
		//Remove any countries which have more units on them than the lowest value
		for(int i=0; i<possibilities.size(); i++){
			if(board.getNumUnits(possibilities.get(i))>lowest){
				possibilities.remove(i);
				i--;
			}
		}
		weakestCountry=getCountryToAttack(possibilities);
		return weakestCountry;
	}
	
	private int getCountryToReinforce(int countryId){
		
		Vector<Integer> possibilities= new Vector<Integer>();
		//Get all possible countries we can reinforce adjacent to
		//the weakest enemy country
		for(int i=0; i<GameData.ADJACENT[countryId].length;i++){
			if(board.getOccupier(GameData.ADJACENT[countryId][i])==1){
				possibilities.add(GameData.ADJACENT[countryId][i]);
			}
		}
		
		int mostUnits=possibilities.get(0);
		for(int i=1; i<possibilities.size();i++){
			if(board.getNumUnits(possibilities.get(i))>board.getNumUnits(mostUnits))
				mostUnits=possibilities.get(i);
		}
		
		//In the case where the desired country is already strong enough i.e.
		//has 2 or more more armies than those adjacent to it choose a country at random
		if(board.getNumUnits(mostUnits)-board.getNumUnits(countryId)>1){
			mostUnits=countriesControlled.get((int)(Math.random() * countriesControlled.size()));
		}
		return mostUnits;
	}
	/*public static void main(String[] args){
	
	}*/
}