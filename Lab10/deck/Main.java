package deck;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.shuffle();

        System.out.println("Сдаем 5 карт:");
        List<Card> hand = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Card card = deck.deal();
            if (card != null) {
                hand.add(card);
                System.out.println(card);
            }
        }

        System.out.println("\nОстаток в колоде: " + deck.remainingCards());

        System.out.println("\nВозвращаем первую карту обратно в колоду:");
        deck.returnCard(hand.get(0));
        System.out.println("Остаток в колоде: " + deck.remainingCards());

        System.out.println("\nПопытка вернуть эту же карту снова:");
        deck.returnCard(hand.get(0));
    }
}
