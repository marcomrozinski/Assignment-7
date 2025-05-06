package dk.dtu.compute.se.pisd.roborally.gameselection.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.gameselection.model.Game;
import dk.dtu.compute.se.pisd.roborally.gameselection.model.User;
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

    private void update() {
        this.getChildren().clear(); // ryd visning før opdatering
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
                Button nameButton = new Button(game.getName() +
                        " (min: " + game.getMinPlayers() +
                        ", max: " + game.getMaxPlayers() + ")");

                Button joinButton = new Button("Join");
                Button leaveButton = new Button("Leave");
                Button startButton = new Button("Start");
                Button deleteButton = new Button("Delete");

                deleteButton.setOnAction((e) -> {
                    try {
                        clientGame.delete(game.getId());
                        update();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });

                this.add(nameButton, 0, i);
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
                Game game = new Game();
                game.setName(nameField.getText());
                game.setMinPlayers(Integer.parseInt(minField.getText()));
                game.setMaxPlayers(Integer.parseInt(maxField.getText()));
                URI baseURI = new URI("http://localhost:8080/");
                ClientFactory factory = Configuration.builder().setBaseUri(baseURI).build().buildClientFactory();
                Client<Game> clientGame = factory.create(Game.class);
                clientGame.post(game);
                update();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // Her skal den refreshe og kalde den update ovenover
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
