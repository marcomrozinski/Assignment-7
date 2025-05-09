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

/**
 * A JavaFX view that displays the list of online games and allows the
 * signed‐in user to join, leave, start, delete, or create games.
 * <p>
 * This GridPane fetches the games from the backend via a Bowman client,
 * renders each game with its players, and enables or disables buttons
 * based on the current user's status and the game's state.
 * </p>
 */
public class GamesView extends GridPane {

    public static final int width = 640;
    public static final int height = 400;

    private final AppController appController;
    private final List<GameButtons> games;

    /**
     * Constructs the games view.
     *
     * @param appController the controller handling application logic
     * @param gameSelection the parent GameSelection pane (for callbacks)
     */
    public GamesView(AppController appController, GameSelection gameSelection) {
        this.appController = appController;
        this.games = new ArrayList<>();

        // Configure columns for game info and action buttons
        this.getColumnConstraints().add(new ColumnConstraints(200));
        this.getColumnConstraints().add(new ColumnConstraints(70));
        this.getColumnConstraints().add(new ColumnConstraints(70));
        this.getColumnConstraints().add(new ColumnConstraints(70));
        this.getColumnConstraints().add(new ColumnConstraints(70));

        // Button to open the "Create Game" dialog
        Button createButton = new Button("Create Game");
        createButton.setOnAction(e -> openCreateGameDialog());
        this.add(createButton, 0, 0);

        // Populate the view with current games
        update();
    }

    /**
     * Clears and rebuilds the UI to show the latest list of games.
     * <p>
     * If no user is signed in, shows a message prompting login.
     * Otherwise, it:
     * <ul>
     *   <li>Fetches all games from the backend</li>
     *   <li>Displays each game's name, min/max players, and current players</li>
     *   <li>Shows "Join", "Leave", "Start", and "Delete" buttons,
     *       enabling or disabling them based on the user's relation to the game.</li>
     * </ul>
     * </p>
     */
    void update() {
        this.getChildren().clear();

        User user = OnlineState.getInstance().getCurrentUser();
        if (user == null) {
            this.add(new Label("You are not logged in"), 0, 1);
            return;
        }

        try {
            URI baseURI = new URI("http://localhost:8080/");
            ClientFactory factory = Configuration.builder()
                    .setBaseUri(baseURI)
                    .build()
                    .buildClientFactory();
            Client<Game> clientGame = factory.create(Game.class);
            List<Game> allGames = new ArrayList<>();
            clientGame.getAll().forEach(allGames::add);

            // Check if user is in any game
            boolean userInAnyGame = allGames.stream()
                    .anyMatch(g -> g.getPlayers().stream()
                            .anyMatch(p -> p.getName().equals(user.getName())));

            int row = 1;
            for (Game game : allGames) {
                // Build game info box
                VBox gameBox = new VBox(5);
                gameBox.getChildren().add(new Label(
                        game.getName() + " (min: " + game.getMinPlayers() +
                                ", max: " + game.getMaxPlayers() + ")"));
                if (game.getPlayers().isEmpty()) {
                    gameBox.getChildren().add(new Label("No players yet."));
                } else {
                    for (Player player : game.getPlayers()) {
                        gameBox.getChildren().add(new Label("- " + player.getName()));
                    }
                }
                this.add(gameBox, 0, row);

                // Join button
                Button joinButton = new Button("Join");
                boolean joinedThis = game.getPlayers().stream()
                        .anyMatch(p -> p.getName().equals(user.getName()));
                joinButton.setDisable(userInAnyGame || joinedThis
                        || game.getPlayers().size() == game.getMaxPlayers());
                joinButton.setOnAction(e -> { /* ... join logic ... */ });

                // Leave button
                Button leaveButton = new Button("Leave");
                leaveButton.setDisable(!joinedThis);
                leaveButton.setOnAction(e -> { /* ... leave logic ... */ });

                // Start button
                Button startButton = new Button("Start");
                startButton.setDisable(game.getPlayers().size() < game.getMinPlayers());

                // Delete button
                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(e -> { /* ... delete logic ... */ });

                // Add action buttons to the grid
                this.add(joinButton,  1, row);
                this.add(leaveButton, 2, row);
                this.add(startButton, 3, row);
                this.add(deleteButton,4, row);

                row++;
            }

        } catch (Exception e) {
            this.add(new Label("There was a problem loading games."), 0, 0);
        }
    }

    /**
     * Opens a dialog window that allows the signed‐in user to enter
     * a new game's name, minimum players, and maximum players, then
     * sends a request to create it on the backend.
     */
    public void openCreateGameDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Create New Game");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField nameField = new TextField();
        TextField minField  = new TextField();
        TextField maxField  = new TextField();
        Button    createBtn = new Button("Create");

        createBtn.setOnAction(e -> {
            // ... create‐game logic ...
            dialog.close();
        });

        layout.getChildren().addAll(
                new Label("Game Name:"), nameField,
                new Label("Min Players:"), minField,
                new Label("Max Players:"), maxField,
                createBtn
        );

        dialog.setScene(new Scene(layout));
        dialog.show();
    }

    /**
     * Helper class grouping the UI buttons for a single game row.
     * <p>This can be used to enable/disable or reference buttons
     * for a particular game.</p>
     */
    private class GameButtons {
        public final Game   game;
        public final Button nameButton;
        public final Button joinButton;
        public final Button leaveButton;
        public final Button startButton;
        public final Button deleteButton;

        GameButtons(Game game,
                    Button nameButton,
                    Button joinButton,
                    Button leaveButton,
                    Button startButton,
                    Button deleteButton) {
            this.game         = game;
            this.nameButton   = nameButton;
            this.joinButton   = joinButton;
            this.leaveButton  = leaveButton;
            this.startButton  = startButton;
            this.deleteButton = deleteButton;
        }
    }
}
