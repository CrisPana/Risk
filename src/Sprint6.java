import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.io.*;

public class Sprint6 {
	
	public static void main (String args[]) {	   
		int winner1 = 0, winner2 = 0;
		long startTimetotal = System.currentTimeMillis();
		for (int run = 0; run < 50; run++)
		{
		long startTime = System.currentTimeMillis();
		
		Board board = new Board();
		UI ui = new UI(board);
		Player[] players = new Player[GameData.NUM_PLAYERS_PLUS_NEUTRALS];
		Player currPlayer, otherPlayer, defencePlayer;
		Deck deck;
		Card card;
		ArrayList<Card> cards;
		int playerId, otherPlayerId, numUnits, numCards, attackUnits, defenceUnits;
		int countryId, attackCountryId, defenceCountryId, countriesInvaded;
		String name;
		Bot[] bots = new Bot[GameData.NUM_PLAYERS];

		// INPUT CHECK
		if (args.length<2) {
			System.out.println("Not enough Bot names");
			return;
		}
		
		// START GAME
		ui.displayString("ENTER PLAYER NAMES");
		ui.displayMap();
		for (playerId=0; playerId<GameData.NUM_PLAYERS_PLUS_NEUTRALS; playerId++) {
			players[playerId] = new Player (playerId);
			if (playerId < GameData.NUM_PLAYERS) {
				try {
					Class<?> botClass = Class.forName(args[playerId]);
					Constructor<?> botCons = botClass.getDeclaredConstructor(BoardAPI.class, PlayerAPI.class);
					bots[playerId] = (Bot) botCons.newInstance(board,players[playerId]);
				} catch (IllegalAccessException ex) {
					System.out.println("Error: Bot instantiation fail (IAE)");
				    Thread.currentThread().interrupt();
				} catch (InstantiationException ex) {
					System.out.println("Error: Bot instantiation fail (IE)");
				    Thread.currentThread().interrupt();
				} catch (ClassNotFoundException ex) {
					System.out.println("Error: Bot instantiation fail (CNFE)");
				    Thread.currentThread().interrupt();
				} catch (InvocationTargetException ex) {
					System.out.println("Error: Bot instantiation fail (ITE)");
				    Thread.currentThread().interrupt();
				} catch (NoSuchMethodException ex) {
					System.out.println("Error: Bot instantiation fail (NSME)");
				    Thread.currentThread().interrupt();
				} 
				players[playerId].setBot(bots[playerId]);
				players[playerId].setName(args[playerId]);
			} else {
				name = "Neutral " + (playerId - GameData.NUM_PLAYERS + 1);
				ui.displayName(playerId,name);
				players[playerId].setName(name);
			}
		}
		
		// STOP BOT CHAT
		PrintStream originalStream = System.out;
		PrintStream dummyStream    = new PrintStream(new OutputStream(){
		    public void write(int b) {
		        //NO-OP
		    }
		});
		System.setOut(dummyStream);
		
		ui.displayString("\nDRAW TERRITORY CARDS FOR STARTING COUNTRIES");
		deck = new Deck(Deck.NO_WILD_CARD_DECK);
		for (playerId=0; playerId<GameData.NUM_PLAYERS_PLUS_NEUTRALS; playerId++) {
			currPlayer = players[playerId];
			if (playerId < GameData.NUM_PLAYERS) {
				numCards = GameData.INIT_COUNTRIES_PLAYER;
			} else {
				numCards = GameData.INIT_COUNTRIES_NEUTRAL;
			}
			for (int i=0; i<numCards; i++) {
				card = deck.getCard();
				ui.displayCardDraw(currPlayer, card);
				board.occupy(card.getCountryId(), currPlayer.getId());
				board.addUnits(card.getCountryId(), 1);
			}
		}
		ui.displayMap();
		
		ui.displayString("\nROLL DICE TO SEE WHO REINFORCES THEIR COUNTRIES FIRST");
		do {
			for (int i=0; i<GameData.NUM_PLAYERS; i++) {
				players[i].rollDice(1);
				ui.displayDice(players[i]);
			}
		} while (players[0].getDie(0) == players[1].getDie(0)); 
		if (players[0].getDie(0) > players[1].getDie(0)) {
			playerId = 0;
		} else {
			playerId = 1;
		}
		currPlayer = players[playerId];
		ui.displayRollWinner(currPlayer);
		
		ui.displayString("\nREINFORCE INITIAL COUNTRIES");
		for (int r=0; r<2*GameData.NUM_REINFORCE_ROUNDS; r++) {
			currPlayer.addUnits(3);
			ui.displayReinforcements(currPlayer);
			do {
				ui.inputReinforcement(currPlayer);
				currPlayer.subtractUnits(ui.getNumUnits());
				board.addUnits(ui.getCountryId(), ui.getNumUnits());
				ui.displayMap();
			} while (currPlayer.getNumUnits() > 0);
			ui.displayMap();
			for (int p=GameData.NUM_PLAYERS; p<GameData.NUM_PLAYERS_PLUS_NEUTRALS; p++) {
				ui.inputPlacement(currPlayer, players[p]);
				countryId = ui.getCountryId();
				board.addUnits(countryId, 1);	
				ui.displayMap();
			}
			playerId = (++playerId)%GameData.NUM_PLAYERS;
			currPlayer = players[playerId];
		}
			
		ui.displayString("\nROLL DICE TO SEE WHO TAKES THE FIRST TURN");
		do {
			for (int i=0; i<GameData.NUM_PLAYERS; i++) {
				players[i].rollDice(1);
				ui.displayDice(players[i]);
			}
		} while (players[0].getDie(0) == players[1].getDie(0)); 
		if (players[0].getDie(0) > players[1].getDie(0)) {
			playerId = 0;
		} else {
			playerId = 1;
		}
		currPlayer = players[playerId];
		ui.displayRollWinner(currPlayer);
		
		deck = new Deck(Deck.WILD_CARD_DECK);		
		
		// TEST CODE TO GIVE PLAYERS 6 CARDS TO START WITH
//		for (int i=0; i<GameData.NUM_PLAYERS; i++) {
//			for (int j=0; j<6; j++) {
//				card = deck.getCard();
//				players[i].addCard(card);
//				ui.displayCardDraw(players[i],card);
//			}
//		}
		
		ui.displayString("\nSTART TURNS");
		do {
			otherPlayerId = (playerId+1)%GameData.NUM_PLAYERS;
			otherPlayer = players[otherPlayerId];
			
			// 1. Reinforcements from occupied countries & continents
			numUnits = board.calcReinforcements(currPlayer);
			currPlayer.addUnits(numUnits);
			ui.displayReinforcements(currPlayer);
			// 1. Reinforcements from cards
			if (!currPlayer.isOptionalExchange()) {	
				ui.displayCards(currPlayer);
				ui.displayCannotExchange(currPlayer);
			} else {
				do {
					ui.displayCards(currPlayer);
					ui.inputCardExchange(currPlayer);		
					if (!ui.isTurnEnded()) {
						board.calcCardExchange(currPlayer);
						cards = currPlayer.subtractCards(ui.getInsigniaIds());
						deck.addCards(cards);
						ui.displayReinforcements(currPlayer);
					}
				} while (currPlayer.isOptionalExchange() && !ui.isTurnEnded());
				if (!currPlayer.isOptionalExchange() && !ui.isTurnEnded()) {
					ui.displayCannotExchange(currPlayer);					
				}
			} 
			do {
				ui.displayReinforcements(currPlayer);
				ui.inputReinforcement(currPlayer);
				currPlayer.subtractUnits(ui.getNumUnits());
				board.addUnits(ui.getCountryId(),ui.getNumUnits());	
				ui.displayMap();
			} while (currPlayer.getNumUnits() > 0);

			// 2. Combat
			countriesInvaded = 0;
			do {
				ui.inputBattle(currPlayer);
				if (!ui.isTurnEnded()) {
					attackUnits = ui.getNumUnits();
					attackCountryId = ui.getFromCountryId();
					defenceCountryId = ui.getToCountryId();
					defencePlayer = players[board.getOccupier(defenceCountryId)];
					if (board.getNumUnits(defenceCountryId) > 1) {
						ui.inputDefence(otherPlayer,defenceCountryId);
						defenceUnits = ui.getNumUnits();
					} else {
						defenceUnits = 1;
					}
					board.calcBattle(currPlayer,defencePlayer,attackCountryId,defenceCountryId,attackUnits,defenceUnits);
					ui.displayBattle(currPlayer,defencePlayer);
					ui.displayMap();
					if (board.isInvasionSuccess()) {
						countriesInvaded++;						
					}
					if ( board.isInvasionSuccess() && (board.getNumUnits(attackCountryId) > 1) ) {
						ui.inputMoveIn(currPlayer,attackCountryId);
						board.subtractUnits(attackCountryId, ui.getNumUnits());
						board.addUnits(defenceCountryId, ui.getNumUnits());
						ui.displayMap();
					}
					if ( board.isInvasionSuccess() && (board.isEliminated(defencePlayer.getId())) ) {
						cards = defencePlayer.removeCards();
						currPlayer.addCards(cards);
						ui.displayCardsWon(currPlayer,defencePlayer,cards);  // No cards received from passive neutrals
						while (currPlayer.isForcedExchange()) {
							ui.displayCards(currPlayer);
							ui.inputCardExchange(currPlayer);
							board.calcCardExchange(currPlayer);
							cards = currPlayer.subtractCards(ui.getInsigniaIds());
							deck.addCards(cards);
							ui.displayReinforcements(currPlayer);
						}
					}
				} 
				
			} while (!ui.isTurnEnded() && !board.isGameOver());

			// 3. Fortify
			if (!board.isGameOver()) {
				ui.inputFortify(currPlayer);
				if (!ui.isTurnEnded()) {
					board.subtractUnits(ui.getFromCountryId(), ui.getNumUnits());
					board.addUnits(ui.getToCountryId(), ui.getNumUnits());
					ui.displayMap();
				}
			}			
			
			// 4. Territory Card
			if (countriesInvaded > 0) {
				card = deck.getCard();
				currPlayer.addCard(card);
				ui.displayCardDraw(currPlayer,card);
			}

			playerId = (playerId+1)%GameData.NUM_PLAYERS;
			currPlayer = players[playerId];			

		} while (!board.isGameOver());
		
		ui.displayWinner(players[board.getWinner()]);
		ui.displayString("GAME OVER");
		
		System.setOut(originalStream);
		System.out.println("GAME OVER");
		if (board.getWinner() == 0) {
			System.out.println(players[0].getName() + " WINS");
			System.out.println(players[1].getName() + " LOSES");
			winner1++;
		} else {
			System.out.println(players[0].getName() + " LOSES");
			System.out.println(players[1].getName() + " WINS");	
			winner2++;
		}
		
		ui.closeWindow();
		System.out.println("Winner1 (OUR): " + winner1);
		System.out.println("Winner2: " + winner2);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		long endTimetotal   = System.currentTimeMillis();
		long totalTimetotal = endTimetotal - startTimetotal;
		double secondstotal = totalTimetotal / 1000.0;
		long minutestotal = (totalTimetotal / 1000)  / 60;
		double seconds = totalTime / 1000.0;
		double average = secondstotal/run;
		System.out.println("Game run time: " + seconds + " seconds.");
		System.out.println("Total run time: " + secondstotal + " seconds.");
		System.out.println("Total run time: " + minutestotal + " minutes.");
		System.out.println("Average: " + average + " seconds.");
		}
		return;
	}

}
