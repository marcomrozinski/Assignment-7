package dk.dtu.compute.se.pisd.roborally.gameselection.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.OnlineState;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * View for selecting and managing online games.
 * <p>
 * Displays a list of available games (via {@link GamesView}), and provides
 * controls for creating new games, closing the view, and signing out.
 * </p>
 */
public class GameSelection extends BorderPane {

    /** Reference to the main application controller. */
    private final AppController appController;

    /**
     * Creates the game selection view.
     *
     * @param appController the application controller used to handle actions
     */
    public GameSelection(AppController appController) {
        this.appController = appController;

        // Set preferred size for the pane
        this.setPrefSize(GamesView.width, GamesView.height);

        // Close button: returns to previous view
        Button close = new Button("Close");
        close.setOnAction(e -> appController.gameSelected(null));
        close.setMinWidth(50);
        close.setMinHeight(30);

        // Create Game button: opens the dialog to create a new game
        Button create = new Button("Create Game");
        create.setMinWidth(50);
        create.setMinHeight(30);

        // Games list view
        GamesView gamesView = new GamesView(appController, this);
        create.setOnAction(e -> gamesView.openCreateGameDialog());

        // Sign Out button: clears the current user and refreshes the view
        Button signOutButton = new Button("Sign Out");
        signOutButton.setOnAction(e -> {
            OnlineState.getInstance().setCurrentUser(null);
            gamesView.update();
            create.setDisable(true);
        });

        // Layout containers
        VBox closeBox = new VBox(close);
        VBox createBox = new VBox(create);

        // Title label
        Label title = new Label("Online Games:");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Pane bottom = gamesView;

        // Main vertical layout
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().addAll(closeBox, createBox, signOutButton, title, bottom);

        // Place the VBox in the center of this BorderPane
        this.setCenter(vbox);
    }
}
