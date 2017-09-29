/* Team Name - DMC
 * 14317761 - Marcin Checinski
 * 14337341 - Damian Kluziak
 * 14366596 - Crischelle Pana
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

// put your code here

public class DMC2 implements Bot {
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects
	// So you can use player.getNumUnits() but you can't use player.addUnits(10000), for example
	
	private BoardAPI board;
	private PlayerAPI player;
	
	DMC2 (BoardAPI inBoard, PlayerAPI inPlayer) {
		board = inBoard;	
		player = inPlayer;
		// put your code here
		return;
	}
	
	public String getName () {
		String command = "DMC";
		return(command);
	}
	
public String getReinforcement () {
		
		String command = "";
		int myID = player.getId();
		int playerOne = 0;
		int i, j, k, borderingCountriesOwned = 0;
		int noToReinforce = player.getNumUnits();
		ArrayList<Integer> possibleCountriesToReinforce = new ArrayList<Integer>();
		
		if (myID == 1)
			playerOne = 0;
		else
			playerOne = 1;

		ArrayList<Integer> ownedCountries = new ArrayList<Integer>();
		for (i = 0; i < GameData.NUM_COUNTRIES; i++) {
			if (board.getOccupier(i) == myID){
				ownedCountries.add(i);
			}
		}
		
//		System.out.print("Countries owned before: ");
//		for (i = 0; i < ownedCountries.size(); i++) {
//		    int value = ownedCountries.get(i);
//		    System.out.print(value + ", ");
//		}
//		System.out.println();
		
		//checks if our countries are currounded by our own then we cast them out of the array
		for (i = 0; i < ownedCountries.size(); i++) {
			borderingCountriesOwned = 0;
			for (j = 0; j < GameData.ADJACENT[ownedCountries.get(i)].length; j++) {
				if (board.getOccupier(GameData.ADJACENT[ownedCountries.get(i)][j]) == myID) {
					borderingCountriesOwned++;
				}
			}
			if (borderingCountriesOwned == GameData.ADJACENT[j].length) {
				ownedCountries.remove(i);
			}
		}
		
		
//		System.out.print("Countries owned after: ");
//		for (i = 0; i < ownedCountries.size(); i++) {
//		    int value = ownedCountries.get(i);
//		    System.out.print(value + ", ");
//		}
//		System.out.println();
		
		//reinforceLimit is the lowest army UNit there is in thhe country the bot owns
		int reinforcementLimit = board.getNumUnits(ownedCountries.get(0));
		for (i = 1; i < ownedCountries.size(); i++) {
			if (board.getNumUnits(ownedCountries.get(i)) < reinforcementLimit)
				reinforcementLimit = board.getNumUnits(ownedCountries.get(i));
		}

		//finds the ratio of owned countries to number of countires in continent
		int[] noOfCountriesInCont = new int[6];
		int[] countryRatio = new int[6];
		int n = 0;
		
		//we check the number of our own countries in the continent
		for (; n < GameData.NUM_COUNTRIES; n++) {
			if (board.getOccupier(n) == myID) {
				if (GameData.CONTINENT_IDS[n] == 0)
					noOfCountriesInCont[0]++;	//north america continent
				else if (GameData.CONTINENT_IDS[n] == 1)
					noOfCountriesInCont[1]++;	//europe
				else if (GameData.CONTINENT_IDS[n] == 2)
					noOfCountriesInCont[2]++;	//asia
				else if (GameData.CONTINENT_IDS[n] == 3)
					noOfCountriesInCont[3]++;	//australia
				else if (GameData.CONTINENT_IDS[n] == 4)
					noOfCountriesInCont[4]++;	//South America
				else if (GameData.CONTINENT_IDS[n] == 5)
					noOfCountriesInCont[5]++;	//Africa
			}
		}
		
		//gets ratio of owned countries in each continent
		for (n = 0; n < 6; n++) {
			if (noOfCountriesInCont[n] > 0)
				countryRatio[n] = GameData.CONTINENT_COUNTRIES[0].length / noOfCountriesInCont[n];
			else
				n++;
		}
		
		//gets continent with the biggest ratio of owned countries to total countries in taht continent
		int biggestRatio = countryRatio[0];
		int biggestRatioContinent = 0; // Which continent ID has the highest ratio
		for (n = 1; n < countryRatio.length; n++) {
			if (countryRatio[n] > biggestRatio) {
				biggestRatio = countryRatio[n];
		    	biggestRatioContinent = n;
		    }
		}	
		
		//sort the countries you own so continents are prioritised
		//we shift the countries who has continent id 3 or 4 to the front of the array
		for (i = 0; i < ownedCountries.size(); i++) { 
			if (GameData.CONTINENT_IDS[ownedCountries.get(i)] == 3 || GameData.CONTINENT_IDS[ownedCountries.get(i)] == 4) {
				Collections.rotate(ownedCountries, -i);
			}
		}
		
		//we check the adjacent countries of our owned countries and see if any of them are beside the enemy's
		//then that would be our priority countries to reinforce our army with
		for (i = 0; i < ownedCountries.size(); i++) { 
			j = 0;
			while (j < GameData.ADJACENT.length) {
				//when the number in the playersCoutnries[] correspond to an index in the ADJACENT 2d array
				//then we get them values
				if (ownedCountries.get(i) == j) {
					//for every adjacent country to the country
					for (k = 0; k < GameData.ADJACENT[j].length; k++){
						//System.out.print(GameData.ADJACENT[j][k] + ", ");
						//if the occupier of that adjacent country is Player 1's then we go into this if statement
						if (board.getOccupier(GameData.ADJACENT[j][k]) == playerOne) {
							//System.out.println("ADJACENT TO Player 1.");
							possibleCountriesToReinforce.add(ownedCountries.get(i));
						}
					}
				}
				else {
					//System.out.println("//WHY IS MY CODE GOING HERE?! #1//");	
				}
				j++;
			}
		}
		
		//just in case duplicates are added to our list
		Set<Integer> tempList = new LinkedHashSet<Integer>(possibleCountriesToReinforce);
		possibleCountriesToReinforce.clear();
		possibleCountriesToReinforce.addAll(tempList);
		
		//if all our countries we own are NOT adjacent to a country that our enemy holds then we go here
		if (possibleCountriesToReinforce.isEmpty()) {
			for (n = 0; n < ownedCountries.size(); n++) {
				if (GameData.CONTINENT_IDS[ownedCountries.get(n)] == biggestRatioContinent) {
			//1.)	First Priority
					if ((ownedCountries.get(n) == 31 || ownedCountries.get(n) == 32 || ownedCountries.get(n) == 34 || ownedCountries.get(n) == 37 || ownedCountries.get(n) == 8 || 
							ownedCountries.get(n) == 4 || ownedCountries.get(n) == 7 || ownedCountries.get(n) == 22 || ownedCountries.get(n) == 23 || ownedCountries.get(n) == 14) 
							&& board.getNumUnits(ownedCountries.get(n)) <= reinforcementLimit) {
						//System.out.println("noPossibleCountryToReinforce #1");
						command = GameData.COUNTRY_NAMES[ownedCountries.get(n)];
						command = command.replaceAll("\\s", "");
						if (noToReinforce > 3) {
							noToReinforce /= 2;
							command += " " + noToReinforce;
						}
						else {
							command += " " + noToReinforce;	
						}
						return(command);
					}
			//2.)	Second Priority
					else if (ownedCountries.get(n) == 31 || ownedCountries.get(n) == 32 || ownedCountries.get(n) == 34 || ownedCountries.get(n) == 37 || ownedCountries.get(n) == 8 || 
							ownedCountries.get(n) == 4 || ownedCountries.get(n) == 7 || ownedCountries.get(n) == 22 || ownedCountries.get(n) == 23 || ownedCountries.get(n) == 14) {
						//System.out.println("noPossibleCountryToReinforce #2");
						command = GameData.COUNTRY_NAMES[ownedCountries.get(n)];
						command = command.replaceAll("\\s", "");
						if (noToReinforce > 3) {
							noToReinforce /= 2;
							command += " " + noToReinforce;
						}
						else {
							command += " " + noToReinforce;	
						}
						return(command);
					}
					
			//3.)	Third Priority
					else if (board.getNumUnits(ownedCountries.get(n)) <= reinforcementLimit) {
						//System.out.println("noPossibleCountryToReinforce #3");
						command = GameData.COUNTRY_NAMES[ownedCountries.get(n)];
						command = command.replaceAll("\\s", "");
						if (noToReinforce > 3) {
							noToReinforce /= 2;
							command += " " + noToReinforce;
						}
						else {
							command += " " + noToReinforce;	
						}
						return(command);
					}
				}
			}
		}
		
		//if possibleCountriesToReinforce is not empty, from here until end of code is what happens
		//#1-5 goes through the arraylist that contains all the countries that are adjacent to our owned countries
		//#1
		for (n = 0; n < possibleCountriesToReinforce.size(); n++) {
			if (possibleCountriesToReinforce.get(n) == 31 || possibleCountriesToReinforce.get(n) == 32 || possibleCountriesToReinforce.get(n) == 34 || possibleCountriesToReinforce.get(n) == 37 || 
					possibleCountriesToReinforce.get(n) == 8 || possibleCountriesToReinforce.get(n) == 4 || possibleCountriesToReinforce.get(n) == 7 || possibleCountriesToReinforce.get(n) == 22 || possibleCountriesToReinforce.get(n) == 23 || possibleCountriesToReinforce.get(n) == 14) {
				if (board.getNumUnits(possibleCountriesToReinforce.get(n)) <= reinforcementLimit && GameData.CONTINENT_IDS[possibleCountriesToReinforce.get(n)] == biggestRatioContinent) {
					//System.out.println("PossibleCountryToReinforce #1");
					command = GameData.COUNTRY_NAMES[possibleCountriesToReinforce.get(n)];
					command = command.replaceAll("\\s", "");
					if (noToReinforce > 3) {
						noToReinforce /= 2;
						command += " " + noToReinforce;
					}
					else {
						command += " " + noToReinforce;	
					}
					return(command);	
				}
				else if (GameData.CONTINENT_IDS[possibleCountriesToReinforce.get(n)] == biggestRatioContinent) {
					//System.out.println("PossibleCountryToReinforce #2");
					command = GameData.COUNTRY_NAMES[possibleCountriesToReinforce.get(n)];
					command = command.replaceAll("\\s", "");
					if (noToReinforce > 3) {
						noToReinforce /= 2;
						command += " " + noToReinforce;
					}
					else {
						command += " " + noToReinforce;	
					}
					return(command);	
				}
				else if (board.getNumUnits(possibleCountriesToReinforce.get(n)) <= reinforcementLimit) {
					//System.out.println("PossibleCountryToReinforce #3");
					command = GameData.COUNTRY_NAMES[possibleCountriesToReinforce.get(n)];
					command = command.replaceAll("\\s", "");
					if (noToReinforce > 3) {
						noToReinforce /= 2;
						command += " " + noToReinforce;
					}
					else {
						command += " " + noToReinforce;	
					}
					return(command);	
				}
			}
		}
		
		 //#2
		for (n = 0; n < possibleCountriesToReinforce.size(); n++) {
			if (possibleCountriesToReinforce.get(n) == 31 || possibleCountriesToReinforce.get(n) == 32 || possibleCountriesToReinforce.get(n) == 34 || possibleCountriesToReinforce.get(n) == 37 || possibleCountriesToReinforce.get(n) == 8 
					|| possibleCountriesToReinforce.get(n) == 4 || possibleCountriesToReinforce.get(n) == 7 || possibleCountriesToReinforce.get(n) == 22 || possibleCountriesToReinforce.get(n) == 23 || possibleCountriesToReinforce.get(n) == 14) {
				//System.out.println("PossibleCountryToReinforce #4");
				command = GameData.COUNTRY_NAMES[possibleCountriesToReinforce.get(n)];
				command = command.replaceAll("\\s", "");
				if (noToReinforce > 3) {
					noToReinforce /= 2;
					command += " " + noToReinforce;
				}
				else {
					command += " " + noToReinforce;	
				}
				return(command);
			}
		}
		
		//#3
		for (n = 0; n < possibleCountriesToReinforce.size(); n++) {
			if (GameData.CONTINENT_IDS[possibleCountriesToReinforce.get(n)] == biggestRatioContinent) {
				if (board.getNumUnits(possibleCountriesToReinforce.get(n)) <= reinforcementLimit) {
					//System.out.println("PossibleCountryToReinforce #5");
					command = GameData.COUNTRY_NAMES[possibleCountriesToReinforce.get(n)];
					command = command.replaceAll("\\s", "");
					if (noToReinforce > 3) {
						noToReinforce /= 2;
						command += " " + noToReinforce;
					}
					else {
						command += " " + noToReinforce;	
					}
					return(command);	
				}
			}
		}		
		
		//#4
		for (n = 0; n < possibleCountriesToReinforce.size(); n++) {
			if (GameData.CONTINENT_IDS[possibleCountriesToReinforce.get(n)] == biggestRatioContinent) {
				//System.out.println("PossibleCountryToReinforce #6");
				command = GameData.COUNTRY_NAMES[possibleCountriesToReinforce.get(n)];
				command = command.replaceAll("\\s", "");
				if (noToReinforce > 3) {
					noToReinforce /= 2;
					command += " " + noToReinforce;
				}
				else {
					command += " " + noToReinforce;	
				}
				return(command);	
			}
		}	
		
		//-#6
		for (n = 0; n < ownedCountries.size(); n++) {
			if (ownedCountries.get(n) == 31 || ownedCountries.get(n) == 32 || ownedCountries.get(n) == 34 || ownedCountries.get(n) == 37 || 
					ownedCountries.get(n) == 8 || ownedCountries.get(n) == 4 || ownedCountries.get(n) == 7 || ownedCountries.get(n) == 22 || ownedCountries.get(n) == 23 || ownedCountries.get(n) == 14) {
				if (board.getNumUnits(ownedCountries.get(n)) <= reinforcementLimit && GameData.CONTINENT_IDS[ownedCountries.get(n)] == biggestRatioContinent) {
					//System.out.println("PossibleCountryToReinforce #7");
					command = GameData.COUNTRY_NAMES[ownedCountries.get(n)];
					command = command.replaceAll("\\s", "");
					if (noToReinforce > 3) {
						noToReinforce /= 2;
						command += " " + noToReinforce;
					}
					else {
						command += " " + noToReinforce;	
					}
					return(command);	
				}
				
				else if (GameData.CONTINENT_IDS[ownedCountries.get(n)] == biggestRatioContinent) {
					//System.out.println("PossibleCountryToReinforce #8");
					command = GameData.COUNTRY_NAMES[ownedCountries.get(n)];
					command = command.replaceAll("\\s", "");
					if (noToReinforce > 3) {
						noToReinforce /= 2;
						command += " " + noToReinforce;
					}
					else {
						command += " " + noToReinforce;	
					}
					return(command);	
				}
				else if (board.getNumUnits(ownedCountries.get(n)) <= reinforcementLimit) {
					//System.out.println("PossibleCountryToReinforce #9");
					command = GameData.COUNTRY_NAMES[ownedCountries.get(n)];
					command = command.replaceAll("\\s", "");
					if (noToReinforce > 3) {
						noToReinforce /= 2;
						command += " " + noToReinforce;
					}
					else {
						command += " " + noToReinforce;	
					}
					return(command);	
				}
			}
		}		
		
		//#5
		for (n = 0; n < possibleCountriesToReinforce.size(); n++) {
			if (board.getNumUnits(possibleCountriesToReinforce.get(n)) <= reinforcementLimit) {
				//System.out.println("PossibleCountryToReinforce #10");
				command = GameData.COUNTRY_NAMES[possibleCountriesToReinforce.get(n)];
				command = command.replaceAll("\\s", "");
				if (noToReinforce > 3) {
					noToReinforce /= 2;
					command += " " + noToReinforce;
				}
				else {
					command += " " + noToReinforce;	
				}
				return(command);	
			}
		}		
		
		//System.out.println(">command: " + command);
		//if all else fails
		command = GameData.COUNTRY_NAMES[ownedCountries.get(0)];
		command = command.replaceAll("\\s", "");
		if (noToReinforce > 3) {
			noToReinforce /= 2;
			command += " " + noToReinforce;
		}
		else {
			command += " " + noToReinforce;	
		}
		return(command);	
	}
	
	//for reinforcing for neutrals
	public String getPlacement (int forPlayer) {
		String command = "";
		
		int myID = player.getId();
		int playerOne = 0;
		int i, j, counter = 0;
		
		if (myID == 1)
			playerOne = 0;
		else
			playerOne = 1;

//		//gets countries current neutral
//		for (i = 0; i < GameData.NUM_COUNTRIES; i++) {
//			if (board.getOccupier(i) == forPlayer)
//				counter++;
//		}
//		int[] countriesOwned = new int[counter];
//		j = 0;
//		for (i = 0; i < GameData.NUM_COUNTRIES; i++) {
//			if (board.getOccupier(i) == forPlayer){
//				countriesOwned[j] = i;
//				j++;
//			}
//		}
//		
//		counter = 0;
//		//gets countries of enemies
//		for (i = 0; i < GameData.NUM_COUNTRIES; i++) {
//			if (board.getOccupier(i) == playerOne) {
//				counter++;
//			}
//		}
//		int[] countriesOwnedbyEnemy = new int[counter];
//		j = 0;
//		for (i = 0; i < GameData.NUM_COUNTRIES; i++) {
//			if (board.getOccupier(i) == playerOne){
//				countriesOwnedbyEnemy[j] = i;
//				j++;
//			}
//		}
//		
//		//Counts number of enemy adj countries to countries this neutral own
//		int[] noOfEnemAdjCountOfOwn = new int[countriesOwned.length];
//		int noOfAdjEnem = 0, p = 0;
//		i = 0;
//		for (j = 0; j < countriesOwned.length; j++) {
//			noOfAdjEnem = 0;
//			for(p = 0; p < GameData.ADJACENT[countriesOwned[j]].length; p++) {
//				if (board.getOccupier(GameData.ADJACENT[countriesOwned[j]][p]) == playerOne) {
//					noOfAdjEnem++;
//				}
//			}
//			noOfEnemAdjCountOfOwn[i] = noOfAdjEnem;
//			i++;
//		}
//		
//		//Counts number of own adj countries to countries this neutral own
//		int[] noOfOwnAdjCountOfOwn = new int[countriesOwned.length];
//		int noOfAdjOwn = 0;
//		i = 0;
//		for (j = 0; j < countriesOwned.length; j++) {
//			noOfAdjOwn = 0;
//			for(p = 0; p < GameData.ADJACENT[countriesOwned[j]].length; p++) {
//				if (board.getOccupier(GameData.ADJACENT[countriesOwned[j]][p]) == myID) {
//					noOfAdjOwn++;
//				}
//			}
//			noOfOwnAdjCountOfOwn[i] = noOfAdjOwn;
//			i++;
//		}
//		
//		//if any of our countries is fully surrounded or equal or more than half by enemy then reinforce on taht.
//		for (i = 0; i < countriesOwned.length; i++) {
//			if (noOfEnemAdjCountOfOwn[i] >= (GameData.ADJACENT[countriesOwned[i]].length/2) || noOfEnemAdjCountOfOwn[i] > noOfOwnAdjCountOfOwn[i]) {
//				command = GameData.COUNTRY_NAMES[countriesOwned[i]];
//				command = command.replaceAll("\\s", "");
//				return(command);
//			}
//		}
//
//		//find the enemy country that holds the lowest army units
//		for (i = 0; i < countriesOwnedbyEnemy.length; i++) {
//			for (j = i; j < countriesOwnedbyEnemy.length; j++) {
//				if (board.getNumUnits(countriesOwnedbyEnemy[j]) < board.getNumUnits(countriesOwnedbyEnemy[i])) {
//					int temp = countriesOwnedbyEnemy[i];
//					countriesOwnedbyEnemy[i] = countriesOwnedbyEnemy[j];
//					countriesOwnedbyEnemy[j] = temp;
//				}
//			}
//		}
//		
//		//and check if it's adjacent to any of the neutral's armies then reinforce that country
//		for (i = 0; i < countriesOwned.length ; i++) { 
//			j = 0;
//			while (j < countriesOwnedbyEnemy.length) {
//				if (noOfEnemAdjCountOfOwn[i] == noOfOwnAdjCountOfOwn[i] || noOfOwnAdjCountOfOwn[i] >= (GameData.ADJACENT[countriesOwned[i]].length/2)) {
//					j++;
//					//do not do anything.
//				}
//				else if (board.isAdjacent(countriesOwned[i], countriesOwnedbyEnemy[j])) {
//					j++;
//					command = GameData.COUNTRY_NAMES[countriesOwned[i]];
//					command = command.replaceAll("\\s", "");
//					return(command);
//				}
//			}
//		}
		
		
			for (i = 0; i < GameData.NUM_COUNTRIES; i++) {
				if (board.getOccupier(i) == forPlayer) {
					command = GameData.COUNTRY_NAMES[i];
					command = command.replaceAll("\\s", "");
					return(command);
				}
			}
		
		
		return(command);
	}
	
	public String getCardExchange () {
		String command = "";
		int infantry = 0, cavalry = 0, artillery = 0, wild = 0;
		boolean availableStandardSet = false, availableWildSet = false;
		ArrayList<Card> cards = player.getCards();
		
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getInsigniaId() == 0)
				infantry++;
			else if (cards.get(i).getInsigniaId() == 1)
				cavalry++;
			else if (cards.get(i).getInsigniaId() == 2)
				artillery++;
			else if (cards.get(i).getInsigniaId() == 3)
				wild++;
		}
		
		if(player.isForcedExchange()){
			if (infantry >= 1 && cavalry >= 1 && artillery >=1)
			{
				command ="ica";
				availableStandardSet = true;
			}
			else if (infantry >= 3)
			{
				command = "iii";
				availableStandardSet = true;
			}
			else if (cavalry >= 3)
			{
				command = "ccc";
				availableStandardSet = true;
			}
			else if (artillery >= 3)
			{
				command = "aaa";
				availableStandardSet = true;
			}
			else if (infantry == 2 && wild >= 1)
			{
				command = "iiw";
				availableWildSet = true;
			}
			else if (infantry == 1 && wild >= 2)
			{
				command = "iww";
				availableWildSet = true;
			}
			else if (cavalry == 2 && wild >= 1)
			{
				command = "ccw";
				availableWildSet = true;
			}
			else if (cavalry == 1 && wild >= 2)
			{
				command = "cww";
				availableWildSet = true;
			}
			else if (artillery == 2 && wild >= 1)
			{
				command = "aaw";
				availableWildSet = true;
			}
			else if (artillery == 1 && wild >= 2)
			{
				command = "aww";
				availableWildSet = true;
			}
			
			return command;
		}
		
		// Exchange cards if there is something to exchange. If optional, exchange it anyway.
		if (infantry >= 1 && cavalry >= 1 && artillery >=1)
		{
			command ="ica";
			availableStandardSet = true;
		}
		else if (infantry >= 3)
		{
			command = "iii";
			availableStandardSet = true;
		}
		else if (cavalry >= 3)
		{
			command = "ccc";
			availableStandardSet = true;
		}
		else if (artillery >= 3)
		{
			command = "aaa";
			availableStandardSet = true;
		}

		// If forced to exchange and we have not enough insignias, but we have wild cards
		boolean safetyCheckEnoughCards = true; // If the cards are higher than other insignias, but they don't match to make command
		
		if (!availableStandardSet && wild >= 1) // if forced to exchange and no insignias except wild cards
		{
			if (infantry >= cavalry || infantry >= artillery || !safetyCheckEnoughCards)
			{
				if (infantry == 2 && wild >= 1)
				{
					command = "iiw";
					availableWildSet = true;
					safetyCheckEnoughCards = true;
				}
				else if (infantry == 1 && wild >= 2)
				{
					command = "iww";
					availableWildSet = true;
					safetyCheckEnoughCards = true;
				}
				else
					safetyCheckEnoughCards = false;
					
					
			}
			else if (cavalry >= infantry || cavalry >= artillery || !safetyCheckEnoughCards)
			{
				if (cavalry == 2 && wild >= 1)
				{
					command = "ccw";
					availableWildSet = true;
					safetyCheckEnoughCards = true;
				}
				else if (cavalry == 1 && wild >= 2)
				{
					command = "cww";
					availableWildSet = true;
					safetyCheckEnoughCards = true;
				}
				else
					safetyCheckEnoughCards = false;
			}
			else if (artillery >= infantry || artillery >= cavalry || !safetyCheckEnoughCards)
			{
				if (artillery == 2 && wild >= 1)
				{
					command = "aaw";
					availableWildSet = true;
					safetyCheckEnoughCards = true;
				}
				else if (artillery == 1 && wild >= 2)
				{
					command = "aww";
					availableWildSet = true;
					safetyCheckEnoughCards = true;
				}
				else
					safetyCheckEnoughCards = false;
			}
		}
		
		// Skip exchange if we don't have insignias or wild cards to use
		if (!safetyCheckEnoughCards || !availableStandardSet) 
			command = "skip";
		
		return(command);
	}

	public String getBattle () {
		String command = "";

		// ----------------------------------------------------------------------------
		// Make a list of all countries that could be attacked
		//   - check for your country
		//   - check for any adjacent country to our country
		//   - remove any countries with 1 unit (leave only countries that can attack)
		// ----------------------------------------------------------------------------
		
		int botID = player.getId();
		int[] botCountries = new int[GameData.NUM_COUNTRIES]; 
		int[][] validCountriesToAttack  = { 
				{4,1,5,6,3,2},    // 0
				{4,5,0},
				{4,0,3,8},
				{2,0,6,8},
				{14,1,0,2},
				{0,1,7,6}, 
				{3,0,5,7},
				{6,5,32},
				{2,3,22},
				{14,15,13,10},    
				{9,13,11,37},     // 10
				{13,12,18,39,10,37},
				{20,16,18,11,13,15},
				{15,12,11,10,9},
				{15,9,4},
				{12,13,14,9},
				{20,27,17,18,12}, 
				{16,27,23,18},
				{12,16,17,40,39,11},
				{26,22},
				{25,27,16,12},    // 20
				{22,24,25},        
				{8,19,26,24,21},
				{27,31,17},
				{21,22,26,25},
				{21,24,26,27,20},
				{24,22,19,27,25},
				{26,23,17,16,20,25},
				{29,30},          
				{28,30,31},
				{29,28,31},      // 30
				{23,29,30},
				{7,34,33},       
				{32,34,35},
				{32,37,35,33},
				{33,34},
				{37,40,38},      
				{10,11,39,40,36,34},
				{36,40,41},
				{11,18,40,37},
				{39,18,41,38,36,37},  //40
				{38,40}
			};
		for (int i = 0; i < GameData.NUM_COUNTRIES; i++)
		{
			botCountries[i] = -1; // fill every position with -1, to know how many OUR countries there are
			if ((board.getOccupier(i) == botID) && (board.getNumUnits(i) > 1))
				botCountries[i] = board.getNumUnits(i);	
		}
		
		// At this point we have an array with countries that are able to attack. Get the array with countries that we can attack
		for (int i = 0; i < GameData.NUM_COUNTRIES; i++)
		{
			if (botCountries[i] > 0)
			{
				for (int j = 0; j < validCountriesToAttack[i].length; j++)
				{
					if (board.getOccupier(validCountriesToAttack[i][j]) == botID) // If our country, then we cannot attack it
						validCountriesToAttack[i][j] = -1;
				}
			}
			else // fill array with -1, to know which countries can we can attack - anything greater than 0 attackable
			{
				for (int j = 0; j < validCountriesToAttack[i].length; j++)
					validCountriesToAttack[i][j] = -1;
			}
		}
		
		// Now we have: An array with our countries, that contain number of armies AND an array with countries that we can attack
		// Arrays correspond to each other, based on the countryID
		
		// ----------------------------------------------------------------------------
		// Select a country that will attack
		// Attack its weakest neighbour, attack with the maximum number of armies
		// Select a pair of countries with the highest difference in armies. Decrease difference if not found
		// MAKE SURE to account for situation when the won't be enough to attack (negative difference)
		// ----------------------------------------------------------------------------
		int attackerCountryID = -1, attackedCountryID = -1, attackWithArmies = -1;
		int highestDifference = 0, differenceAllowed = 2;
		
		for (int i = 0; i < GameData.NUM_COUNTRIES; i++)
		{
			for (int j = 0; j < validCountriesToAttack[i].length; j++)
			{
				if (validCountriesToAttack[i][j] != -1 && highestDifference < botCountries[i] - board.getNumUnits(validCountriesToAttack[i][j]))
				{
					attackerCountryID = i;
					attackedCountryID = validCountriesToAttack[i][j];
					highestDifference = botCountries[i] - board.getNumUnits(validCountriesToAttack[i][j]);
				}
			}
		}
		
		// if the difference isn't high enough, we will use different strategy, and attack any country with 3 armies
		int lowestDefender = 0;
		
		if (highestDifference < differenceAllowed)
		{
			for (int i = 0; i < GameData.NUM_COUNTRIES; i++)
			{
				for (int j = 0; j < validCountriesToAttack[i].length; j++)
				{
					if (botCountries[i] >= 4)
					{
						if (validCountriesToAttack[i][j] != -1 && lowestDefender > board.getNumUnits(validCountriesToAttack[i][j]))
						{
							attackerCountryID = i;
							attackedCountryID = validCountriesToAttack[i][j];
							lowestDefender = board.getNumUnits(validCountriesToAttack[i][j]);
						}
					}
				}
			}
		}
		
		// Check if more than for armies on any country, if so, attack. Last resort.
		if (attackerCountryID == -1 || attackedCountryID == -1)
		{
			for (int i = 0; i < GameData.NUM_COUNTRIES; i++)
			{
				for (int j = 0; j < validCountriesToAttack[i].length; j++)
				{
					if (botCountries[i] >= 4 && validCountriesToAttack[i][j] != -1)
					{
						attackerCountryID = i;
						attackedCountryID = validCountriesToAttack[i][j];
					}
				}
			}
		}
		
		
		if (attackerCountryID == -1 || attackedCountryID == -1)
				command = "skip";
		else
		{
			if (board.getNumUnits(attackerCountryID) > 3) // Always attack with maximum
				attackWithArmies = 3;
			else if (board.getNumUnits(attackerCountryID) == 3)
				attackWithArmies = 2;
			else if (board.getNumUnits(attackerCountryID) == 2)
				attackWithArmies = 1;
//			else // Should never happen, but take care of that case
//				attackWithArmies = 0;
			
			String command1 = GameData.COUNTRY_NAMES[attackerCountryID];
			command1 = command1.replaceAll("\\s", "");
			String command2 = GameData.COUNTRY_NAMES[attackedCountryID];
			command2 = command2.replaceAll("\\s", "");
			command = command1 + " " + command2 + " " + attackWithArmies;
		}
		
		return(command);
	}

	public String getDefence (int countryId) {
		String command = "";

		if (board.getNumUnits(countryId) > 1) {
			command = "2";
		}
		else if (board.getNumUnits(countryId) == 1) {
			command = "1";
		}
		else {
			command = "1";
		}

		return(command);
	}

	//
	public String getMoveIn (int attackCountryId) {
		String command = "";
		int myID = player.getId();
		int numberOfCountriesOwned = 0;
		int numberOfCountries = 0;
		//check which countries are adjacent to the country that was just passed in by value
		for(int i = 0; i < GameData.ADJACENT[attackCountryId].length; i++) {
			if (board.getOccupier(GameData.ADJACENT[attackCountryId][i]) == myID) {			//if the country adjacent belongs to the bot, increase the counters
				numberOfCountriesOwned++;				
				numberOfCountries++;
			}
			else
				numberOfCountries++;
		}
		//STRATEGY - check the number of countries owned
		//if the amount of the countries the bot owns is less than the amount of countries he doesn't own, or if the amounts are equal, then don't move in
		if (numberOfCountriesOwned < (numberOfCountries - numberOfCountriesOwned) || (numberOfCountries - numberOfCountriesOwned) == numberOfCountriesOwned) {
			command = "0";
		}
		//if the bot own more countries than the amount of adjacent countries the reinforce according to the units i have in the attacking country
		else if (numberOfCountriesOwned > (numberOfCountries - numberOfCountriesOwned)) {
			int units = board.getNumUnits(attackCountryId);
			if (units <= 4)
				command = "0";
			else if (units == 5)
				command = "1";
			else if (units == 6)
				command = "2";
			else if (units == 8 || units == 7)
				command = "3";
			else if (units >= 9)
				command = "4";
		}
		return(command);
	}

	public String getFortify () {
		String command = "";
		int myID = player.getId();
		int numberOfCountriesOwned = 0;
		int i, j = 0;
		//check how many countries the bot occupies
		for (i = 0; i < GameData.NUM_COUNTRIES; i++) {
			if (board.getOccupier(i) == myID)
				numberOfCountriesOwned++;
		}
		
		int[] playersCountries = new int[numberOfCountriesOwned];
		
		//check which countries the bot occupies
		for (i = 0; i < GameData.NUM_COUNTRIES; i++) {
			if (board.getOccupier(i) == myID){
				playersCountries[j] = i;
				j++;
			}
		}
		
//		System.out.print("COUNTRIES: ");
//		for (i = 0; i < playersCountries.length; i++) {
//			System.out.print(playersCountries[i] + ", ");
//		}
//		System.out.println();
		
		//gets possible fortifiers
		int fortifiers = 0;
		for (i = 0; i < playersCountries.length; i++) {
			if (board.getNumUnits(playersCountries[i]) > 1) {
				fortifiers++;
			}
		}	
		int[] possFortifiers = new int[fortifiers];
		j = 0;
		for (i = 0; i < playersCountries.length; i++) {
			if (board.getNumUnits(playersCountries[i]) > 1) {
				possFortifiers[j] = playersCountries[i];
				j++;
			}
		}	
		
		int borderingCountriesOwned = 0;
		//gets possible fortifees
		int fortifees = 0;
		for (i = 0; i < playersCountries.length; i++) {
			for (j = 0; j < GameData.ADJACENT[playersCountries[i]].length; j++) {
				if (board.getOccupier(GameData.ADJACENT[playersCountries[i]][j]) != myID) {
					fortifees++;
				}
			}
		}
		int[] possCountToFort = new int[fortifees];
		int m = 0;
		for (i = 0; i < playersCountries.length; i++) {
			for (j = 0; j < GameData.ADJACENT[playersCountries[i]].length; j++) {
				if (board.getOccupier(GameData.ADJACENT[playersCountries[i]][j]) != myID) {
					possCountToFort[m] = playersCountries[i];
					m++;
				}
			}
		}

		//sort possible fortifiers in terms of highest armny units	
//		System.out.print("FORTIFEES: ");
//		for (i = 0; i < possCountToFort.length; i++) {
//			System.out.print(possCountToFort[i] + ", ");
//		}
//		System.out.println();
			
//		System.out.print("FORTIFIERSBefore sort: ");
//		for (i = 0; i <possFortifiers.length; i++) {
//			System.out.println(possFortifiers[i] + ", ");
//		}
//		System.out.println();
		
		//sort possible fortifiers in terms of highest armny units
		for (i = 0; i < possFortifiers.length; i++) {
			for (j = i + 1; j < possFortifiers.length; j++) {
				if (board.getNumUnits(possFortifiers[j]) > board.getNumUnits(possFortifiers[i])) {
					int temp = possFortifiers[i];
					possFortifiers[i] = possFortifiers[j];
					possFortifiers[j] = temp;
				}
			}
		}		
		
		//sort possible fortifees in terms of lowest army units
		for (i = 0; i < possCountToFort.length; i++) {
			for (j = i + 1; j < possCountToFort.length; j++) {
				if (board.getNumUnits(possCountToFort[j]) < board.getNumUnits(possCountToFort[i])) {
					int temp = possCountToFort[i];
					possCountToFort[i] = possCountToFort[j];
					possCountToFort[j] = temp;
				}
			}
		}	

		int fortifyWithArmies;
		String command2 = "";
		boolean isFortify = false;
		for (i = 0; i < possFortifiers.length; i++) {
			for (j = 0; j < possCountToFort.length; j++) {
				if (possCountToFort[j] == possFortifiers[i]) {
					command = "";
					isFortify = true;
					command = "skip";
				}
				else if (board.isAdjacent(possFortifiers[i], possCountToFort[j]) || board.isConnected(possFortifiers[i], possCountToFort[j])) {
					command = "";
					fortifyWithArmies = board.getNumUnits(possFortifiers[i]) - 1;
					command = GameData.COUNTRY_NAMES[possFortifiers[i]].replaceAll("\\s", "");
					command2 = GameData.COUNTRY_NAMES[possCountToFort[j]].replaceAll("\\s", "");
					command = command + " " + command2 + " " + fortifyWithArmies;
					return(command);
				}
				else {
					command = "";
					isFortify = true;
					command = "skip";
				}
			}
		}
		
		if (!isFortify) {
			command = "";
			command = "skip";
		}

		return(command);
	}

}
