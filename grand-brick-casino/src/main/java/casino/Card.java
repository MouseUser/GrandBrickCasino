package casino;

public class Card {
    private int rank;
    private Suit suit;
    private int value;
    private boolean faceDown;

    public Card(int rank, Suit suit) {
        if (rank > 10) {
            value = 10;
            if (rank == 14) {
                value = 11;
            }
        }
        else {
            value = rank;
        }
        this.rank = rank;
        this.suit = suit;
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
    public void setValue(int value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
