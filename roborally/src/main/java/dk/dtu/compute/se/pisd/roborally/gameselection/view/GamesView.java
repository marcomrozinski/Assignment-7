package dk.dtu.compute.se.pisd.roborally.gameselection.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.gameselection.model.Game;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import uk.co.blackpepper.bowman.Client;
import uk.co.blackpepper.bowman.ClientFactory;
import uk.co.blackpepper.bowman.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
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
        /*
        this.prefWidth(width);
        this.maxWidth(width);
        this.minWidth(width);
        this.prefHeight(height);
        this.maxHeight(height);
        this.minHeight(height); */

        update();
    }

    private void update() {
        try {
            URI baseURI = new URI("http://localhost:8080/");

            ClientFactory factory = Configuration.builder()
                    .setBaseUri(baseURI)
                    .build()
                    .buildClientFactory();

            Client<Game> clientGame = factory.create(Game.class);
            // Client<Player> clientPlayer = factory.create(Player.class);
            // Client<User> clientUser = factory.create(User.class);

            Iterable<Game> games = clientGame.getAll();

            int i = 0;
            for (Game game: games ) {

                Button nameButton = new Button(game.getName() +
                        " (min: " + game.getMinPlayers() +
                        ", max: " + game.getMaxPlayers() + ")" );

                Button startButton = new Button("Start");

                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction((e) -> {
                    try {
                        clientGame.delete(game.getId());
                        appController.selectGame();
                    } catch (Exception exception) {
                        // Some info to the user, would be nice here
                        // Anyway this "complex functionality" should be somewhere
                        // in a controller
                    }
                });
                this.add(nameButton, 0, i);
                this.add(startButton, 1, i);
                this.add(deleteButton, 2, i);
                i++;
            }
        } catch (Exception e) {
            Label text = new Label("There was a problem with loading the games.");
            this.add(text, 0,0);

        }


    }

    private class GameButtons {

        public final Game game;
        public final Button nameButton;

        public final Button startButton;

        public final Button deleteButton;

        public GameButtons(Game game, Button nameButton, Button startButton, Button deleteButton) {
            this.game = game;
            this.nameButton = nameButton;
            this.startButton = startButton;
            this.deleteButton = deleteButton;
        }

    }

}
