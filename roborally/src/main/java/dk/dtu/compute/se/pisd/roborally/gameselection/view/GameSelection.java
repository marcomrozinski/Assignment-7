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

        GamesView gamesView = new GamesView(appController, this);
        create.setOnAction(e -> {
            gamesView.openCreateGameDialog(); // Åbn dialogen du lavede før
        });

        Button signOutButton = new Button("Sign Out");
        signOutButton.setOnAction(e -> {
            OnlineState.getInstance().setCurrentUser(null);

            gamesView.update();

            create.setDisable(true); // Deaktiver knappen
        });

// Læg knappen i en VBox eller HBox alene
        VBox closeBox = new VBox(close);
        VBox createBox  = new VBox(create);

// Nu laver vi label
        Label title = new Label("Online Games:");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

    Pane bottom = gamesView;

// Læg det hele i én VBox
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().addAll(closeBox, createBox,signOutButton, title, bottom);

// Sæt VBox i midten
        this.setCenter(vbox);
    }
}
