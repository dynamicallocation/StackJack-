import java.util.*;

public class Player {
    //variables
    private String name;
    private int moneyAmount;
    private int bet;
    ArrayList<ArrayList<Card>> hands = new ArrayList<ArrayList<Card>>();
    private boolean bust = false;
    private boolean turnOver = false;
    private boolean blackjack = false;
    private boolean specialAce = false;
    
    // constructor
    public Player(String nameChoice) {
        hands.add(new ArrayList<Card>());
        hands.add(new ArrayList<Card>());
        moneyAmount = 100;
        name = nameChoice;
    }
    
    // access methods
    public boolean getBustStatus() {return bust;}
    public boolean getTurnOver() {return turnOver;}
    public boolean getBlackjack() {return blackjack;}
    public boolean getAceAsOne() {return specialAce;}
    public int getMoney() {return moneyAmount;}
    public int getBet() {return bet;}
    public String getName() {return name;}
    
    public Card getCard(int pos, int hand) {
        return hands.get(hand).get(pos);
    }
    
    // input verifier (Credit: adarshr on Stack Overflow )
    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isNumber(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    
    // hand methods
    public void hit(Card drawn, int hand) {
        hands.get(hand).add(drawn);
    }
    
    public int countHand(int hand) {
        int num = 0;
        boolean checkAce = false;
        for(int i = 0; i < hands.get(hand).size(); i++) {
            if(hands.get(hand).get(i).getValue() == 1) {
                checkAce = true;
            }
            num += hands.get(hand).get(i).getValue();
        }
        if(checkAce) {
            if((num + 10) <= 21) {
                specialAce = true;
                return num + 10;
            }
            else {
                specialAce = false;
            }
        }
        return num;
    }
    
    public boolean checkPair() {
        if(hands.get(0).get(0).getNumber() == hands.get(0).get(1).getNumber()) {
            return true;
        }
        else return false;
    }
    
    public void resetHand() {
        hands.get(0).clear();
        hands.get(1).clear();
        bust = false;
        turnOver = false;
        blackjack = false;
        specialAce = false;
    }
    
    //  change value methods
    public void changeBust(boolean status) {
        bust = status;
    }
    
    public void changeTurnOver(boolean status) {
        turnOver = status;
    }
    
    public void changeBlackjack(boolean status) {
        blackjack = status;
    }
    
    public void changeMoney(int amount) {
        moneyAmount += amount;
        if(moneyAmount < 0) {
            moneyAmount = 0;
        }
    }
    
    //gameplay methods
    public int makeBet() {
        System.out.println("[   "+ name + "'s money: $" + moneyAmount + "   ]");
        Scanner scanner = new Scanner(System.in);
        String input = "0";
        if(bet == 0 || bet > moneyAmount) {
            System.out.print("How much would you like to bet? $"); 
            input = scanner.nextLine();
        }
        else {
            System.out.print("How much would you like to bet? $" + bet + "\n(enter to confirm, or type new amount here) >$"); 
            input = scanner.nextLine();
        }
        System.out.println();
        int number = 0;
        if(isNumber(input) && !input.equals("")) {
            number = Integer.parseInt(input);
        }
        else if(bet > moneyAmount) {
            return makeBet();
        }
        else if(input.equals("") && bet > 0) {
            return bet;
        }
        else {
            System.out.println("    ! Not a number !\n");
            return makeBet();
        }
        if((moneyAmount - number >= 0) && number > 0) {
            bet = number;
            return number;
        }
        else {
            System.out.println("    ! Invalid bet !\n");
            return makeBet();
        }
    }
    
    public void playHand(Deck deck) {
        //Player is dealt two cards
        System.out.println("(" + name + "'s Cards)");
        for(int i = 0; i <= 1; i++) {
            Card draw = deck.popStack();
            hit(draw, 0);
            System.out.println("> " + draw.getCardString());
        }
        printTotal(0);
        
        // Player plays
        if(countHand(0) == 21) {
            //win
            System.out.println("Blackjack!\n\n----------");
            changeBlackjack(true);
            changeTurnOver(true);
            changeMoney(bet + (bet/2));
        }
        
        while(!bust && !turnOver) {
            System.out.print("~~ Type \"H\" to Hit, \"S\" to Stand, \"T\" to Split, \"D\" to Double Down ~~");
            Scanner scanner = new Scanner(System.in);
            String keyboard = scanner.nextLine();
            System.out.println();
            if(keyboard.equals("H") || keyboard.equals("h")) {
                Card draw = deck.popStack();
                hit(draw, 0);
                System.out.println("> " + draw.getCardString());
                printTotal(0);
                if(countHand(0) == 21) {
                    changeTurnOver(true);
                    System.out.println("\n----------");
                }
                if(countHand(0) > 21) {
                    System.out.println("  *** Bust ***\n");
                    changeMoney(-bet);
                    changeBust(true);
                }
            }
            else if(keyboard.equals("S") || keyboard.equals("s")) {
                changeTurnOver(true);
                System.out.print("\n----------\n");
            }
            else if(keyboard.equals("T") || keyboard.equals("t")) {
                if(checkPair() && bet * 2 <= moneyAmount) {
                    Card first = getCard(0, 0);
                    Card second = getCard(1, 0);
                    System.out.println("@@   Left Hand   @@");
                    System.out.println("> " + first.getCardString());
                    playSplitHand(deck, first, 0);
                    System.out.println();
                    System.out.println("@@   Right Hand   @@");
                    System.out.println("> " + second.getCardString());
                    playSplitHand(deck, second, 1);
                    changeTurnOver(true);
                    System.out.print("\n----------\n");
                }
                else if(!checkPair()) {
                    System.out.println(" ?? Cannot split this hand. You can only split if dealt a pair.\n");
                }
                else {
                    System.out.println(" ?? Cannot split this hand. You don't have enough money to double your bet.\n");
                }
            }
            else if(keyboard.equals("D") || keyboard.equals("d")) {
                if(countHand(0) >= 9 && countHand(0) <= 11 && bet * 2 <= moneyAmount) {
                    bet = bet * 2;
                    System.out.println("## Your bet is now $" + bet + " ##");
                    Card draw = deck.popStack();
                    hit(draw, 0);
                    System.out.println("> " + draw.getCardString());
                    printTotal(0);
                    changeTurnOver(true);
                    System.out.print("\n----------\n");
                }
                else {
                    if(bet * 2 > moneyAmount) {
                        System.out.println(" ?? Cannot double down on this hand. You don't have enough money to double your bet.\n");
                    }
                    else {
                        System.out.println(" ?? Cannot double down on this hand. First two cards must add up to 9, 10, or 11.\n");
                    }
                }
            }
        }
    }
    
    public void playSplitHand(Deck deck, Card card, int hand) {
        hands.get(hand).clear();
        hit(card, hand);
        printTotal(hand);
        while(!bust && !turnOver) {
            System.out.print("~~ Type \"H\" to Hit, \"S\" to Stand ~~ <Split Pair>");
            Scanner scanner = new Scanner(System.in);
            String keyboard = scanner.nextLine();
            System.out.println();
            if(keyboard.equals("H") || keyboard.equals("h")) {
                Card draw = deck.popStack();
                hit(draw, hand);
                System.out.println("> " + draw.getCardString());
                printTotal(hand);
                if(countHand(hand) == 21) {
                    changeTurnOver(true);
                }
                if(countHand(hand) > 21) {
                    System.out.println("  *** Bust ***\n");
                    changeMoney(-bet);
                    changeBust(true);
                }
            }
            else if(keyboard.equals("S") || keyboard.equals("s")) {
                changeTurnOver(true);
            }
        }
        changeBust(false);
        changeTurnOver(false);
    }
    
    public void playAutoHand(Deck deck) {
        // Dealer deals two cards
        System.out.println("================");
        System.out.println("(Dealer's Cards)");
        for(int i = 0; i <= 1; i++) {
            Card draw = deck.popStack();
            hit(draw, 0);
            if(i == 0) System.out.println("> " + draw.getCardString());
            else System.out.println("> [Card Face Down]");
        }
        if(countHand(0) == 21) blackjack = true;
        System.out.println("================\n");
    }
    
    public void revealHand(Deck deck) {
        // Dealer shows
        System.out.println("================");
        System.out.println("(Dealer's Cards)");
        for(int i = 0; i <= 1; i++) {
            Card card = getCard(i, 0);
            System.out.println("> " + card.getCardString());
        }
        printTotal(0);
        
        if(countHand(0) == 21) {
            System.out.println("Blackjack!");
        }
        
        while(!bust && countHand(0) < 17) {
            Card draw = deck.popStack();
            hit(draw, 0);
            System.out.println("> " + draw.getCardString());
            printTotal(0);
            if(countHand(0) > 21) {
                System.out.println("Dealer Busts");
                changeBust(true);
            }
        }
        System.out.println("================");
    }
    
    public void compareToDealer(Player dealer, int hand) {
        //evaluations
        if(blackjack) {
            System.out.print("*** (!) " + name + " got Blackjack! ***");
        }
        else if(dealer.getBustStatus() && countHand(hand) <= 21 && !blackjack) {
            System.out.print("*** (+) " + name + " won! ***");
            changeMoney(bet);
        }
        else if(countHand(hand) > dealer.countHand(0) && countHand(hand) <= 21) {
            System.out.print("*** (+) " + name + " won! ***");
            changeMoney(bet);
        }
        
        else if(countHand(hand) > 21) {
            System.out.print("*** (-) " + name + " bust ***");
        }
        
        else if(countHand(hand) == dealer.countHand(0) && !blackjack) {
            System.out.print("*** (~) " + name + " pushed ***");
        }
        
        else {
            System.out.print("*** (-) " + name + " lost ***");
            changeMoney(-bet);
        }
        if(hand == 1) {
            System.out.println(" (Second Hand)");
        }
        else {
            System.out.println();
        }
    }
    
    public void playTwoCards(Deck deck) {
        System.out.println("(" + name + "'s Cards)");
        for(int i = 0; i <= 1; i++) {
            Card draw = deck.popStack();
            hit(draw, 0);
            System.out.println("> " + draw.getCardString());
        }
        printTotal(0);
    }
    
    public void printTotal(int hand) {
        countHand(hand);
        if(specialAce) {
            System.out.println("    /// TOTAL: " + (countHand(hand) - 10) + " or " + countHand(hand));
        }
        else {
            System.out.println("    /// TOTAL: " + countHand(hand));
        }
    }
}