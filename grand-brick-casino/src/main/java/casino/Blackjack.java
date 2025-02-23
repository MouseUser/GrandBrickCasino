package casino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private Label bettingLabel = new Label("Currently Betting:\n100");
    private Label payoutLabel = new Label("Payout:\n2x Bet");
    private Label statusLabel = new Label();
    private Button hitButton = new Button("Hit");
    private Button standButton = new Button("Stand");
    private Button splitButton = new Button("Split");
    private List<Card> dealerHand = new ArrayList<>();
    private List<Card> playerHand = new ArrayList<>();
    private int dealerScore = 0;
    private int playerScore = 0;
    private boolean splittable;
    //upgradeable 'skills'
    private int luckLevel;
    private int payoutLevel = 1;
    private int critPayoutLevel;
    private int intelligence;

    private int currentBet;
    private int moneyAmount = 50;
    private int betMultiplier = 1;
    private BackgroundImage background;
    private Image deckImage;
    private Image chipsImage;
    
    private boolean stand;
    private boolean blackjack;
    private boolean bust;
    private boolean hit;
    
    public void start(@SuppressWarnings("exports") Stage stage) {
        
        // Title Screen
        Label welcomeLabel = new Label("Welcome to the Grand Brick Casino");
        Button startButton = new Button("Start");
        startButton.setOnAction((ActionEvent event) -> {
            //changeScene(stage, blackjackGame);
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

        VBox dealerBox = new VBox(10, new Label("Dealer"), dealerHandBox);
        dealerBox.setStyle("-fx-alignment: center;");
        dealerHandBox.setStyle("-fx-padding: 10px;");
        dealerHandBox.setAlignment(Pos.CENTER);
        
        // Player Area
        VBox playerBox = new VBox(10, playerHandBox);
        playerBox.setStyle("-fx-alignment: center;");
        playerHandBox.setStyle("-fx-padding: 10px;");
        playerHandBox.setAlignment(Pos.CENTER);
        
        // Betting Area
        VBox betInfo = new VBox(10, bettingLabel, payoutLabel);
        betInfo.setStyle("-fx-background-color: lightgreen; -fx-padding: 10px;");
        
        // Buttons
        HBox buttonBox = new HBox(10, hitButton, standButton, splitButton);
        buttonBox.setStyle("-fx-alignment: center;");
        statusLabel.setText("Game has started.");
        VBox bottomBox = new VBox(10, buttonBox, statusLabel);
        
        root.setTop(dealerBox);
        root.setCenter(playerBox);
        root.setLeft(betInfo);
        root.setBottom(bottomBox);
        
        blackjackGame = new Scene(root, 800, 600);
        
        // Button Actions
        hitButton.setOnAction(e -> hit());
        standButton.setOnAction(e -> stand());
        //splitButton.setOnAction(e -> split());
        
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
        splitButton.setDisable(true);
        dealComp();
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
        }
        else {
            statusLabel.setText("You do not have enough money for this bet.");
            //tell player to get more money
            return false;
        }
        return true;
    }
    private void gameplay() {
        System.out.println("Gameplay started");
            if (buyIn()) {
                dealerHand.clear();
                dealerHandBox.getChildren().clear();
                playerHand.clear();
                playerHandBox.getChildren().clear();                
                deal(playerHandBox, playerHand, createCard("player", false), "player");
                deal(playerHandBox, playerHand, createCard("player", false), "player");
                // if (playerHand.get(0).equals(playerHand.get(1))) {
                //     splittable = true;
                //     statusLabel.setText("Click Split to split your hand.");
                // }
                System.out.println("Initial cards dealt");
                int dealerCards = 0;
                while (dealerScore < 17) {
                    boolean faceDown = true;
                    if (dealerCards == 0) {
                        faceDown = false;
                    }
                    deal(dealerHandBox, dealerHand, createCard("dealer", false), "dealer");
                    System.out.println(dealerScore);
                    dealerCards++;
                }
                if (dealerScore > 21) {
                    System.out.println("code worked and dealer bust");
                    statusLabel.setText("The dealer has gone over 21! You win.");
                    dealComp();
                }
                // Enable buttons for player actions
                hitButton.setDisable(false);
                standButton.setDisable(false);
                splitButton.setDisable(false);

                statusLabel.setText("Click Hit to get another card or Stand to end your turn.");
            }
            else {
                loss();
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
        }
        if ((playerScore > dealerScore && playerScore <= 21) || (dealerScore > 21 && playerScore <= 21)) {
            win();
        }
        else {
            loss();
        }
    }
    private void hit() {
            deal(playerHandBox, playerHand, createCard("player", false), "player");
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
            //big brain bj
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
    private void deal(HBox handView, List<Card> hand, Card card, String playerOrDealer) {
        ImageView cardView;
        if (card.getFaceDown()) {
            cardView = new ImageView(new Image("file:assets/cards/back.png"));
        }
        else {
            cardView = new ImageView(new Image(getCardImagePath(card)));
        }
        cardView.setFitWidth(80);
        cardView.setFitHeight(120);
        hand.add(card);
        handView.getChildren().add(cardView);
        if (playerOrDealer.toLowerCase() == "player") {
            playerScore += card.getValue();
        }
        else if (playerOrDealer.toLowerCase() == "dealer") {
            dealerScore += card.getValue();
        }    
    }
    // private void split() {
    //     if (splittable) {
    //         boolean stand = false;
    //         boolean bust = false;
    //         boolean blackjack = false;
            
    //         while (stand == false && bust == false && blackjack == false) {
    //             if (hit) {
    //                 //player hits
    //                 handEval();
    //                 //check hand if bust/blackjack
    //                 hit = false;
    //                 //continue until player stands, busts or hits blackjack
    //             }
    //         if (h=2) {
    //             // this would mean the second hand is finished,
    //             // check both hands for win condition
    //         }
    //         else {
    //             //swap to second hand
                
    //             // reset stand, bust and blackjack
    //             stand = false;
    //             bust = false;
    //             blackjack = false;
                
    //             //allow player to play the second hand
    //         }
    //         }
    //     }
    //     // reset splittable after finished
    //     splittable = false;
    // }
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
        
        statusLabel.setText(brickPuns[random.nextInt(brickPuns.length)] + " You Won $" + (currentBet*payoutLevel) + " the dealer had " + dealerScore);
        moneyAmount = moneyAmount + (currentBet * payoutLevel);
    }
    private void loss() {
        String[] sarcasticBrickjackPuns = {
            "That's bricked up",
            "Looks like you hit a brick wall",
            "like a brick sinking in water!",
            "You really cemented your place at the bottom!",
            "Another crack in your strategy, I see!",
            "Building a losing streak, one brick at a time!",
            "Your luck is as sturdy as a crumbling brick!",
            "Just another brick in the wall of almost-wins!",
            "You’re really stacking up those losses!",
            "That's how the brick crumbles"
        };
        statusLabel.setText(sarcasticBrickjackPuns[random.nextInt(sarcasticBrickjackPuns.length)] + " You Lost $ " + currentBet + "The dealer had " + dealerScore);
        moneyAmount = moneyAmount - (currentBet);
        //subtracts betted money
    }
    // private void skillMenu (Stage primaryStage) {
    //     primaryStage.setTitle("Skills Menu");

    //     // Create buttons for skills
    //     Button LuckButton = new Button("Luck");
    //     Button PayButton = new Button("Payout");
    //     Button IntButton = new Button("Intelligence");

    //     // Add action listeners to the buttons
    //      LuckButton.setOnAction(e -> upgradeLuck());
    //      PayButton.setOnAction(e -> upgradePay());
    //      IntButton.setOnAction(e -> upgradeInt());

    //     // Create a layout and add buttons to it
    //     VBox layout = new VBox(10);
    //         layout.getChildren().addAll(LuckButton, PayButton, IntButton);

    //     // Create a scene and add the layout to it
    //     Scene scene = new Scene(layout, 300, 200);
    //     primaryStage.setScene(scene);
    //     primaryStage.show();
    // }

    // private void upgradeLuck(int number) {
    //     Luck++;
    //     moneyAmount -= luckCost
    //  }
    // private void upgradePay(int number) {
    //     Payout++;
    //     moneyAmount -= payoutCost
    //  }
    // private void upgradeInt(int number) {
    //     Intelligence++;
    //     moneyAmount -= intCost
    //  }
    public static void main(String[] args) {
        launch(args);
    }
}
