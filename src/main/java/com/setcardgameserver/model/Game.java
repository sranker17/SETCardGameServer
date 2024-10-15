package com.setcardgameserver.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Data
@Slf4j
@NoArgsConstructor
public class Game {

    private static final int BOARD_SIZE = 9;
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
    private boolean playerLeft = false;

    public Game(int gameId, UUID winner, boolean playerLeft) {
        this.gameId = gameId;
        this.winner = winner;
        this.playerLeft = playerLeft;
    }

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

    public boolean hasSet(List<Card> cards) {
        if (nullCardIndexes.size() == 9) {
            log.debug("doesn't have any more cards");
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
                                        log.debug("i: {} j: {} k: {}", i, j, k);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        log.debug("doesn't have SET");
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

    public List<Card> getCardsFromIndex(List<Integer> indexes) {
        ArrayList<Card> selectedCards = new ArrayList<>();

        for (Integer index : indexes) {
            selectedCards.add(board.get(index));
        }

        return selectedCards;
    }

    public void changeCardsOnBoard() {
        for (Integer selectedCardIndex : selectedCardIndexes) {
            if (!cardDeck.isEmpty()) {
                board.set(selectedCardIndex, cardDeck.get(0));
                cardDeck.remove(0);
            } else {
                nullCardIndexes.add(selectedCardIndex);
            }
        }
    }

    public UUID calculateWinner() {
        if (player1 != null && player2 != null) {
            if (points.get(player1) > points.get(player2)) {
                return player1;
            } else if (points.get(player1) < points.get(player2)) {
                return player2;
            } else if (Objects.equals(points.get(player1), points.get(player2))) {
                return UUID.randomUUID();
            }
        }
        return null;
    }
}
