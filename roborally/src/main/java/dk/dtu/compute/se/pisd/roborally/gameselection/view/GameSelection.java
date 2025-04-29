package dk.dtu.compute.se.pisd.roborally.gameselection.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class GameSelection extends BorderPane {

    AppController appController;


    public GameSelection (AppController appcontroller) {
        this.appController = appcontroller;


        this.setPrefSize(GamesView.width,GamesView.height);


        Button close = new Button("Close");
        close.setOnAction((e) -> appController.gameSelected(null));
        close.setMinWidth(50);
        close.setMinHeight(30);

        Button create = new Button("Create Game");
        create.setMinWidth(50);
        create.setMinHeight(30);
        create.setOnAction(e -> {
            // Dette kan være et input til at få navnet på spillet og antal spillere fra brugeren
            String gameName = "New Game"; // Du kan erstatte dette med et inputfelt for brugeren
            int minPlayers = 1; // Minimum antal spillere (du kan erstatte dette med brugerinput)
            int maxPlayers = 10; // Maksimum antal spillere (kan også være brugerinput)

            // Kalder AppController's metode for at oprette spil
            appController.createNewGame(gameName, minPlayers, maxPlayers);
        });
// Læg knappen i en VBox eller HBox alene
        VBox closeBox = new VBox(close);
        VBox createBox  = new VBox(create);

// Nu laver vi label
        Label title = new Label("Online Games:");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

// Spil-listen
        Pane bottom = new GamesView(appController, this);

// Læg det hele i én VBox
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().addAll(closeBox, createBox, title, bottom);

// Sæt VBox i midten
        this.setCenter(vbox);



    }




}
