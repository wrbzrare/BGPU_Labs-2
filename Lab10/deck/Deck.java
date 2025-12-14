package deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card deal() {
        if (cards.isEmpty()) {
            System.out.println("Колода пуста!");
            return null;
        }
        return cards.remove(0);
    }

    public boolean returnCard(Card card) {
        if (!cards.contains(card)) {
            cards.add(card);
            return true;
        } else {
            System.out.println("Эта карта уже в колоде!");
            return false;
        }
    }

    public void showDeck() {
        for (Card card : cards) {
            System.out.println(card);
        }
    }

    public int remainingCards() {
        return cards.size();
    }
}
