package casino;

import java.util.Random;

public class Card {
    private static final Suit[] SUITS = {Suit.CLUBS, Suit.SPADES, Suit.CLUBS, Suit.DIAMONDS};
    private int rank;
    private Suit suit;
    private int value;
    private boolean faceDown;

    public Card(boolean faceDown) {
        Random random = new Random();
        rank = random.nextInt(14) + 1;
        if (rank > 10) {
            value = 10;
            if (rank == 14) {
                value = 11;
            }
        }
        suit = SUITS[random.nextInt(SUITS.length)];
        this.faceDown = faceDown;
    }
    @Override
    public boolean equals(Object obj) {
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Card other = (Card)obj;
        return this.rank == other.rank;
    }
    public int getRank() {
        return rank;
    }
    public Suit getSuit() {
        return suit;
    }
    public boolean getFaceDown() {
        return faceDown;
    }
    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
    }
    public int getValue() {
        return value;
    }
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
