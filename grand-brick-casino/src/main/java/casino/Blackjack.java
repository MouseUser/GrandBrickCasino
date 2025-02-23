package casino;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Blackjack extends Application {
    private Scene blackjackGame;
    private Random random = new Random();
    private HBox dealerHandBox = new HBox(10);
    private HBox playerHandBox = new HBox(10);
    private Label dealerLabel = new Label();
    private Label bettingLabel = new Label();
    private Label payoutLabel = new Label();
    private Label currentMoneyLabel = new Label();
    private Button shopButton = new Button("Shop");
    private Button exitShopButton = new Button("Return to game");
    private HBox luckBox;
    private HBox payoutBox;
    private HBox intelligenceBox;
    private Button LuckButton = new Button("Increase Luck");
    private Button PayButton = new Button("Increase Payout");
    private Button IntButton = new Button("Increase Intelligence");
    private Label statusLabel = new Label();
    private Button hitButton = new Button("Hit");
    private Button standButton = new Button("Stand");
    private Button playAgain = new Button("Play again?");
    private List<Card> dealerHand = new ArrayList<>();
    private List<Card> playerHand = new ArrayList<>();
    private int dealerScore;
    private int playerScore;
    //upgradeable 'skills'
    private int luckLevel = 1;
    private int payoutLevel = 1;
    private int intelligence;
    //skill prices
    private int luckCost = (int)Math.pow(10, luckLevel);
    private int payoutCost = (int)Math.pow(5, payoutLevel);
    private int intCost = 250 + (int)Math.pow(intelligence, 2);

    private int currentBet;
    private int moneyAmount = 1000;
    private int betMultiplier = 1;

    private boolean stand;
    private boolean blackjack;
    private boolean bust;

    public void start(@SuppressWarnings("exports") Stage stage) {
        
        // Title Screen
        Label welcomeLabel = new Label("Welcome to the Grand Brick Casino");
        Button startButton = new Button("Start");
        startButton.setOnAction((ActionEvent event) -> {
            startGame(stage);
        });
        VBox titleScreenItems = new VBox(welcomeLabel, startButton);
        titleScreenItems.setAlignment(Pos.CENTER);
        Scene titleScreen = new Scene(titleScreenItems, 800, 600);
        stage.setScene(titleScreen);
        stage.setTitle("Grand Brick Casino");

        // Game Table Layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20px;");
        
        // Dealer Area
        VBox dealerBox = new VBox(10, dealerLabel, dealerHandBox);
        dealerBox.setStyle("-fx-alignment: center;");
        dealerHandBox.setStyle("-fx-padding: 10px;");
        dealerHandBox.setAlignment(Pos.CENTER);
        
        // Player Area
        VBox playerBox = new VBox(10, playerHandBox);
        playerBox.setStyle("-fx-alignment: center;");
        playerHandBox.setStyle("-fx-padding: 10px;");
        playerHandBox.setAlignment(Pos.CENTER);
        
        // Betting Area
        VBox betInfo = new VBox(10, bettingLabel, payoutLabel, currentMoneyLabel, shopButton);
        betInfo.setStyle("-fx-background-color: lightgreen; -fx-padding: 10px;");
        
        // Buttons
        HBox buttonBox = new HBox(10, hitButton, standButton, playAgain);
        buttonBox.setStyle("-fx-alignment: center;");
        statusLabel.setText("Game has started.");
        VBox bottomBox = new VBox(10, buttonBox, statusLabel);
        
        root.setTop(dealerBox);
        root.setCenter(playerBox);
        root.setLeft(betInfo);
        root.setBottom(bottomBox);
        
        blackjackGame = new Scene(root, 800, 600);

        // Add action listeners to the buttons
        LuckButton.setOnAction(e -> upgradeLuck());
        PayButton.setOnAction(e -> upgradePay());
        IntButton.setOnAction(e -> upgradeInt());

        // Create a layout and add buttons to it
        luckBox = new HBox(10, new Label("Luck - lvl. " + luckLevel), LuckButton, new Label("$" + luckCost));
        payoutBox = new HBox(new Label("Payout - lvl. " + payoutLevel), PayButton, new Label("$" + payoutCost));
        intelligenceBox = new HBox(new Label("Intelligence - lvl. " + intelligence), IntButton, new Label("$" + intCost));
        VBox shopLayout = new VBox(10);
        shopLayout.getChildren().addAll(luckBox, payoutBox, intelligenceBox);
        HBox shopMenu = new HBox(shopLayout, exitShopButton);

        // Create a scene and add the layout to it
        Scene shopScene = new Scene(shopMenu, 800, 600);
        
        // Button Actions
        hitButton.setOnAction(e -> hit());
        standButton.setOnAction(e -> stand());
        playAgain.setOnAction(e -> gameplay());
        shopButton.setOnAction(e -> changeScene(stage, shopScene));
        exitShopButton.setOnAction((ActionEvent e) -> {
            changeScene(stage, blackjackGame);
            refreshShopDisplay();
            currentMoneyLabel.setText("Current Balance:\n$" + moneyAmount);
            bettingLabel.setText("Currently Betting:\n" + "$" + currentBet);
            payoutLabel.setText("Payout:\n" + betMultiplier + "x bet");
        });
        
        stage.show();
    }
    private void startGame(Stage stage) {
        changeScene(stage, blackjackGame);
        gameplay();
    }
    private void changeScene(Stage stage, Scene scene) {
        stage.setScene(scene);
    }
    private void endPlayerTurn() {
        hitButton.setDisable(true);
        standButton.setDisable(true);
        dealComp();
        playAgain.setDisable(false);
        shopButton.setDisable(false);
    }
    private void stand() {
        stand = true;
        endPlayerTurn();
    }
    private boolean buyIn() {
        
        /**check if player has enough money for bet
         * take player money and put into current bet
         */
        int buyin = 50 * betMultiplier;

        if (moneyAmount >= buyin) {
            moneyAmount = moneyAmount - buyin;
            //take money out of money and into bet
            currentBet = buyin;
            bettingLabel.setText("Currently Betting:\n" + "$" + currentBet);
            payoutLabel.setText("Payout:\n" + betMultiplier + "x bet");
            currentMoneyLabel.setText("Current Balance:\n$" + moneyAmount);
        }
        else {
            statusLabel.setText("You do not have enough money for this bet.");
            //tell player to get more money
            return false;
        }
        return true;
    }
    private void refreshShopDisplay() {
        luckBox.getChildren().clear();
        luckBox.getChildren().addAll(new Label("Luck - lvl. " + luckLevel), LuckButton, new Label("$" + luckCost));
        payoutBox.getChildren().clear();
        payoutBox.getChildren().addAll(new Label("Payout - lvl. " + payoutLevel), PayButton, new Label("$" + payoutCost));
        intelligenceBox.getChildren().clear();
        intelligenceBox.getChildren().addAll(new Label("Intelligence - lvl. " + intelligence), IntButton, new Label("$" + intCost));
    }
    private void gameplay() {
        playAgain.setDisable(true);
        shopButton.setDisable(true);
            if (buyIn()) {
                dealerHand.clear();
                dealerHandBox.getChildren().clear();
                playerHand.clear();
                playerHandBox.getChildren().clear();
                dealerScore = 0;
                playerScore = 0;
                deal(playerHand, createCard("player", false), "player");
                deal(playerHand, createCard("player", false), "player");
                int dealerCards = 0;
                while (dealerScore < 17) {
                    boolean faceDown = true;
                    if (dealerCards == 0) {
                        faceDown = false;
                    }
                    deal(dealerHand, createCard("dealer", faceDown), "dealer");
                    dealerCards++;
                }
                dealerLabel.setText("The dealer has drawn his hand.");
                if (dealerScore > 21) {
                    dealerLabel.setText("The dealer has gone over 21! You win.");
                    endPlayerTurn();
                }
                else if (dealerScore == 21) {
                    dealerLabel.setText("The dealer has blackjack.");
                    endPlayerTurn();
                }
                // Enable buttons for player actions
                else { 
                    hitButton.setDisable(false);
                    standButton.setDisable(false);

                    statusLabel.setText("Click Hit to get another card or Stand to end your turn.");
                }
                betMultiplier *= 2;
            }
            else {
                statusLabel.setText("You are out of money and cannot afford this bet.");
            }
        }
    private Card createCard(String playerOrDealer, boolean faceDown) {
        Card card = new Card(faceDown);
        if (card.getValue() == 11) {
            if (playerOrDealer.toLowerCase() == "player" && playerScore + 11 > 21) {
                card.setValue(1);
            }
            else if (playerOrDealer.toLowerCase() == "dealer" && dealerScore + 11 > 21) {
                card.setValue(1);
            }
        }
        return card;
        
    }
    private void dealComp() {
        playerScore = 0;
        for (Card card : playerHand) {
            playerScore += card.getValue();
        }
        dealerScore = 0;
        for (Card card : dealerHand) {
            dealerScore += card.getValue();
            card.setFaceDown(false);
        }
        refreshHandDisplays();
        if ((playerScore > dealerScore && playerScore <= 21) || (dealerScore > 21 && playerScore <= 21)) {
            win();
        }
        else {
            loss();
        }
    }
    private void hit() {
            deal(playerHand, createCard("player", false), "player");
            handEval();
            if (bust || blackjack) {
                endPlayerTurn();
            }
    }
    private void handEval() {
        playerScore = 0;
        for (Card card : playerHand) {
            playerScore += card.getValue();
        }
        if (intelligence > 10 && ((playerScore >= 20) || playerScore <= 22)) {
            blackjack = true;
            //big brain blackjack
        }
        else if (playerScore == 21) {
            blackjack = true;
            //stupid blackjack
        }
        else if (playerScore > 21) {
            bust = true;
        }
    }
    private String getCardImagePath(Card card) {
        return "file:assets/cards/" + card.getRank() + "_of_" + card.getSuit().toString().toLowerCase() + ".png";
    }
    private void refreshHandDisplays() {
        dealerHandBox.getChildren().clear();
        playerHandBox.getChildren().clear();
        for (Card card : playerHand) {
            ImageView cardView;
            if (card.getFaceDown()) {
                cardView = new ImageView(new Image("file:assets/cards/back.png"));
            }
            else {
                cardView = new ImageView(new Image(getCardImagePath(card)));
            }
            cardView.setFitWidth(80);
            cardView.setFitHeight(120);
            playerHandBox.getChildren().add(cardView);
        }
        for (Card card : dealerHand) {
            ImageView cardView;
            if (card.getFaceDown()) {
                cardView = new ImageView(new Image("file:assets/cards/back.png"));
            }
            else {
                cardView = new ImageView(new Image(getCardImagePath(card)));
            }
            cardView.setFitWidth(80);
            cardView.setFitHeight(120);
            dealerHandBox.getChildren().add(cardView);
        }
    }
    private void deal(List<Card> hand, Card card, String playerOrDealer) {
        hand.add(card);
        refreshHandDisplays();
        if (playerOrDealer.toLowerCase() == "player") {
            playerScore += card.getValue();
        }
        else if (playerOrDealer.toLowerCase() == "dealer") {
            dealerScore += card.getValue();
        }    
    }
    private void win() {
        /**congradulate
         * multiply bet
         * add to money
         */
        String[] brickPuns = {
            "You really laid the foundation for success!",
            "You're a brick-tastic achiever!",
            "You built that victory brick by brick!",
            "You’re a brick above the rest!",
            "You’re a brick in the wall of fame!",
            "You’re a cornerstone of excellence!",
            "You bricked it out of the park!",
            "You’re stacking up the wins!",
            "You’re a brick star!",
            "You’re building a legacy, one brick at a time!"
        };
        
        statusLabel.setText(brickPuns[random.nextInt(brickPuns.length)] + " You Won $" + (currentBet*payoutLevel) + ". The dealer had " + dealerScore);
        moneyAmount += (currentBet * payoutLevel);
        bettingLabel.setText("Currently Betting:\n" + "$" + currentBet);
        payoutLabel.setText("Payout:\n" + payoutLevel + "x bet");
        currentMoneyLabel.setText("Current Balance:\n" + "$" + moneyAmount);
    }
    private void loss() {
        String[] sarcasticBrickjackPuns = {
            "That's bricked up.",
            "Looks like you hit a brick wall.",
            "like a brick sinking in water!",
            "You really cemented your place at the bottom!",
            "Another crack in your strategy, I see!",
            "Building a losing streak, one brick at a time!",
            "Your luck is as sturdy as a crumbling brick!",
            "Just another brick in the wall of almost-wins!",
            "You’re really stacking up those losses!",
            "That's how the brick crumbles."
        };
        statusLabel.setText(sarcasticBrickjackPuns[random.nextInt(sarcasticBrickjackPuns.length)] + " You Lost $" + currentBet + ". The dealer had " + dealerScore);
        
        currentMoneyLabel.setText("Current Balance:\n" + "$" + moneyAmount);
    }
    //Problem; upgrade money is the same as bet $$

     private void upgradeLuck() {
           if (moneyAmount >= luckCost) {
            luckLevel++;
            moneyAmount -= luckCost;
            luckCost = (int)Math.pow(10, luckLevel);
            refreshShopDisplay();
           }
      }
     private void upgradePay() {
         if (moneyAmount >= payoutCost) {
            payoutLevel++;
            moneyAmount -= payoutCost;
            payoutCost = (int)Math.pow(5, payoutLevel);
            refreshShopDisplay();
         }
        }
     private void upgradeInt() {
        if (moneyAmount >= intCost) {
            intelligence++;
            moneyAmount -= intCost;
            intCost = 250 + (int)Math.pow(intelligence, 2);
            refreshShopDisplay();
        }
     }
    public static void main(String[] args) {
        launch(args);
    }
}
