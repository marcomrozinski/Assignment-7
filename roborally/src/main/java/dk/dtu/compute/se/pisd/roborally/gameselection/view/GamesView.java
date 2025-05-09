package dk.dtu.compute.se.pisd.roborally.gameselection.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.OnlineState;
import dk.dtu.compute.se.pisd.roborally.gameselection.model.Game;
import dk.dtu.compute.se.pisd.roborally.gameselection.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uk.co.blackpepper.bowman.Client;
import uk.co.blackpepper.bowman.ClientFactory;
import uk.co.blackpepper.bowman.Configuration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GamesView extends GridPane {

    public static final int width = 640;
    public static final int height = 400;

    private AppController appController;
    private List<GameButtons> games;

    public GamesView(AppController appController, GameSelection gameSelection) {
        this.appController = appController;
        games = new ArrayList<>();

        this.getColumnConstraints().add(new ColumnConstraints(200));
        this.getColumnConstraints().add(new ColumnConstraints(70));
        this.getColumnConstraints().add(new ColumnConstraints(70));
        this.getColumnConstraints().add(new ColumnConstraints(70));
        this.getColumnConstraints().add(new ColumnConstraints(70));

        Button createButton = new Button("Create Game");
        createButton.setOnAction(e -> openCreateGameDialog());
        this.add(createButton, 0, 0);

        update();
    }

    void update() {
        System.out.println("Update called");
        this.getChildren().clear();
        User user = OnlineState.getInstance().getCurrentUser();
        if (user == null) {
            Label info = new Label("You are not logged in");
            this.add(info, 0, 1);
            return;
        }

        try {
            URI baseURI = new URI("http://localhost:8080/");

            ClientFactory factory = Configuration.builder()
                    .setBaseUri(baseURI)
                    .build()
                    .buildClientFactory();

            Client<Game> clientGame = factory.create(Game.class);
            Iterable<Game> games = clientGame.getAll();

            int i = 1;
            for (Game game : games) {
                VBox gameBox = new VBox();
                gameBox.setSpacing(5);

                Label gameLabel = new Label(game.getName() +
                        " (min: " + game.getMinPlayers() +
                        ", max: " + game.getMaxPlayers() + ")");
                gameBox.getChildren().add(gameLabel);

                if (game.getPlayers() != null && !game.getPlayers().isEmpty()) {
                    for (Player player : game.getPlayers()) {
                        String playerText = "- " + player.getName();
                        gameBox.getChildren().add(new Label(playerText));
                    }
                } else {
                    gameBox.getChildren().add(new Label("No players yet."));
                }

                this.add(gameBox, 0, i);

                boolean alreadyJoined = game.getPlayers().stream()
                        .anyMatch(p -> p.getUser() != null && p.getUser().getUid() == user.getUid());

                Button joinButton = new Button("Join");
                joinButton.setDisable(alreadyJoined); // Disable knappen hvis brugeren er med

                if (game.getPlayers().size() == game.getMaxPlayers()) {
                    joinButton.setDisable(true); // Disable start button if there are not enough players
                } else {
                    joinButton.setDisable(false); // Enable start button if the game is full enough
                }

                joinButton.setOnAction(e -> {
                    try {
                        User currentUser = OnlineState.getInstance().getCurrentUser();
                        if (currentUser == null) {
                            System.out.println("User not logged in.");
                            return;
                        }

                        Player newPlayer = new Player();
                        newPlayer.setName(currentUser.getName());

                        Game gameRef = new Game();
                        gameRef.setUid(game.getUid());
                        gameRef.setId(URI.create("http://localhost:8080/games/" + game.getUid()));
                        newPlayer.setGame(gameRef);

                        Client<Player> playerClient = factory.create(Player.class);
                        playerClient.post(newPlayer);

                        System.out.println("Joined game!");
                        update();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Could not join game: " + ex.getMessage());
                        alert.showAndWait();
                    }
                });

                Button leaveButton = new Button("Leave");


                leaveButton.setOnAction(e -> {
                    try {
                        User currentUser = OnlineState.getInstance().getCurrentUser();
                        if (currentUser == null) {
                            System.out.println("User not logged in.");
                            return;
                        }

                        // Find Player-objekt for currentUser i dette spil
                        Player playerToRemove = game.getPlayers().stream()
                                .filter(p -> p.getName() != null && p.getName().equals(currentUser.getName()))
                                .findFirst()
                                .orElse(null);


                        if (playerToRemove == null) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have not joined this game.");
                            alert.showAndWait();
                            return;
                        }

                        Client<Player> playerClient = factory.create(Player.class);

                        playerClient.delete(playerToRemove.getId());
                        System.out.println("Left game: " + game.getName());

                        update(); // Refresh GUI

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Could not leave game: " + ex.getMessage());
                        alert.showAndWait();
                    }
                });


                leaveButton.setDisable(true);  // Disable leave by default

                // Enable the leave button if the user is already in the game
                for (Player player : game.getPlayers()) {
                    if (player.getName().equals(user.getName())) {
                        leaveButton.setDisable(false);  // Enable leave button if the user is in the game
                        break;
                    }
                }
                Button startButton = new Button("Start");

                if (game.getPlayers().size() < game.getMinPlayers()) {
                    startButton.setDisable(true); // Disable start button if there are not enough players
                } else {
                    startButton.setDisable(false); // Enable start button if the game is full enough
                }

                Button deleteButton = new Button("Delete");

                deleteButton.setOnAction(e -> {
                    try {
                        User currentUser = OnlineState.getInstance().getCurrentUser();
                        if (currentUser == null) {
                            System.out.println("User not logged in.");
                            return;
                        }

                        if (game.getOwner() != null && game.getOwner().getUid() != currentUser.getUid()) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Only the owner can delete this game.");
                            alert.showAndWait();
                            return;
                        }

                        URI gameId = game.getId();
                        if (gameId != null) {
                            System.out.println("Deleting game: " + gameId);
                            clientGame.delete(gameId);
                            update();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Game ID is missing.");
                            alert.showAndWait();
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting game: " + ex.getMessage());
                        alert.showAndWait();
                    }
                });




                this.add(joinButton, 1, i);
                this.add(leaveButton, 2, i);
                this.add(startButton, 3, i);
                this.add(deleteButton, 4, i);
                i++;
            }
        } catch (Exception e) {
            Label text = new Label("There was a problem with loading the games.");
            this.add(text, 0, 0);
        }
    }

    public void openCreateGameDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Create New Game");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField nameField = new TextField();
        TextField minField = new TextField();
        TextField maxField = new TextField();
        Button createButton = new Button("Create");

        createButton.setOnAction(e -> {
            try {
                User currentUser = OnlineState.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "You must be logged in to create a game.");
                    alert.showAndWait();
                    return;
                }

                // Opret spil
                Game game = new Game();
                game.setName(nameField.getText());
                game.setMinPlayers(Integer.parseInt(minField.getText()));
                game.setMaxPlayers(Integer.parseInt(maxField.getText()));

                // Sæt owner
                dk.dtu.compute.se.pisd.roborally.gameselection.model.User userRef =
                        new dk.dtu.compute.se.pisd.roborally.gameselection.model.User();
                userRef.setUid(currentUser.getUid());
                userRef.setId(URI.create("http://localhost:8080/users/" + currentUser.getUid()));
                game.setOwner(userRef);

                // Send til backend
                URI baseURI = new URI("http://localhost:8080/");
                ClientFactory factory = Configuration.builder().setBaseUri(baseURI).build().buildClientFactory();
                Client<Game> clientGame = factory.create(Game.class);
                clientGame.post(game);

                update();
                dialog.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to create game: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        layout.getChildren().addAll(
                new Label("Game Name:"), nameField,
                new Label("Min Players:"), minField,
                new Label("Max Players:"), maxField,
                createButton
        );

        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    private class GameButtons {

        public final Game game;
        public final Button nameButton;
        public final Button startButton;
        public final Button deleteButton;
        public final Button joinButton;
        public final Button leaveButton;

        public GameButtons(Game game, Button nameButton, Button joinButton, Button leaveButton, Button startButton, Button deleteButton) {
            this.game = game;
            this.nameButton = nameButton;
            this.startButton = startButton;
            this.deleteButton = deleteButton;
            this.joinButton = joinButton;
            this.leaveButton = leaveButton;
        }
    }
}
