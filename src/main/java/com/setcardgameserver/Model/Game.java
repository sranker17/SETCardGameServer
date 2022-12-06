package com.setcardgameserver.Model;

import lombok.Data;

import java.util.*;

@Data
public class Game {

    private final int BOARD_SIZE = 9;
    private int gameId;
    private UUID player1;
    private UUID player2;
    private GameStatus status;
    private ArrayList<Card> board = new ArrayList<>();
    private UUID winner;
    private ArrayList<Card> cardDeck = new ArrayList<>();
    private UUID blockedBy;
    private ArrayList<Integer> selectedCardIndexes = new ArrayList<>();
    private Map<UUID, Integer> points = new HashMap<>();
    private ArrayList<Integer> nullCardIndexes = new ArrayList<>();

    public void createGame() {
        do {
            for (Color color : Color.values()) {
                for (Shape shape : Shape.values()) {
                    for (Quantity quantity : Quantity.values()) {
                        cardDeck.add(new Card(color, shape, quantity));
                    }
                }
            }

            Collections.shuffle(cardDeck);

            for (int i = 0; BOARD_SIZE > i; i++) {
                board.add(cardDeck.get(0));
                cardDeck.remove(0);
            }
        } while (!hasSet(board));
    }

    public boolean hasSet(ArrayList<Card> cards) {
        if (nullCardIndexes.size() == 9) {
            System.out.println("doesn't have any more cards");
            return false;
        }

        if (cards.size() >= 3) {
            ArrayList<Boolean> propertyChecks = new ArrayList<>();
            for (int i = 0; 3 > i; i++) propertyChecks.add(false);

            for (int i = 0; cards.size() > i; i++) {
                if (!nullCardIndexes.contains(i) || cards.size() == 3) {
                    for (int j = i + 1; cards.size() > j; j++) {
                        if (!nullCardIndexes.contains(j) || cards.size() == 3) {
                            for (int k = j + 1; cards.size() > k; k++) {
                                if (!nullCardIndexes.contains(k) || cards.size() == 3) {
                                    for (int x = 0; 3 > x; x++) propertyChecks.set(x, false);
                                    if (cards.get(i).getColor() == cards.get(j).getColor() && cards.get(i).getColor() == cards.get(k).getColor())
                                        propertyChecks.set(0, true);
                                    if (cards.get(i).getColor() != cards.get(j).getColor() && cards.get(i).getColor() != cards.get(k).getColor() && cards.get(j).getColor() != cards.get(k).getColor())
                                        propertyChecks.set(0, true);
                                    if (cards.get(i).getShape() == cards.get(j).getShape() && cards.get(i).getShape() == cards.get(k).getShape())
                                        propertyChecks.set(1, true);
                                    if (cards.get(i).getShape() != cards.get(j).getShape() && cards.get(i).getShape() != cards.get(k).getShape() && cards.get(j).getShape() != cards.get(k).getShape())
                                        propertyChecks.set(1, true);
                                    if (cards.get(i).getQuantity() == cards.get(j).getQuantity() && cards.get(i).getQuantity() == cards.get(k).getQuantity())
                                        propertyChecks.set(2, true);
                                    if (cards.get(i).getQuantity() != cards.get(j).getQuantity() && cards.get(i).getQuantity() != cards.get(k).getQuantity() && cards.get(j).getQuantity() != cards.get(k).getQuantity())
                                        propertyChecks.set(2, true);

                                    if (!propertyChecks.contains(false)) {
                                        propertyChecks.clear();
                                        System.out.println("i: " + i + " j: " + j + " k: " + k);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("doesn't have SET");
        return false;
    }

    public void addToSelectedCardIndexes(int index) {
        if (selectedCardIndexes.size() < 3) {
            selectedCardIndexes.add(index);
        }
    }

    public void removeFromSelectedCardIndexes(int index) {
        if (selectedCardIndexes.contains(index)) {
            selectedCardIndexes.remove((Integer) index);
        }
    }

    public int getSelectedCardIndexSize() {
        return selectedCardIndexes.size();
    }

    public void clearSelectedCardIndexes() {
        selectedCardIndexes.clear();
    }

    public ArrayList<Card> getCardsFromIndex(ArrayList<Integer> indexes) {
        ArrayList<Card> selectedCards = new ArrayList<>();

        for (int i = 0; indexes.size() > i; i++) {
            selectedCards.add(board.get(indexes.get(i)));
        }

        return selectedCards;
    }

    public void changeCardsOnBoard() {
        for (int i = 0; selectedCardIndexes.size() > i; i++) {
            if (cardDeck.size() > 0) {
                board.set(selectedCardIndexes.get(i), cardDeck.get(0));
                cardDeck.remove(0);
            } else {
                nullCardIndexes.add(selectedCardIndexes.get(i));
            }
        }
    }

    public UUID calculateWinner() {
        if (player1 != null && player2 != null) {
            if (points.get(player1) > points.get(player2)) {
                return player1;
            } else if (points.get(player1) < points.get(player2)){
                return player2;
            }
            else if(points.get(player1) == points.get(player2)){
                return UUID.randomUUID();
            }
        }
        return null;
    }
}
