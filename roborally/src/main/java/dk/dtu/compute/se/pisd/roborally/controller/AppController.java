package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.gameselection.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.User;
import dk.dtu.compute.se.pisd.roborally.model.UserAdapter;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The main application controller for the RoboRally JavaFX client.
 * <p>
 * Manages user sign-in/sign-up, game creation, game selection, and
 * delegates to {@link GameController} for in-game behavior.
 * </p>
 * Implements {@link Observer} so it can react to model updates if needed.
 */
public class AppController implements Observer {

    private static final List<Integer> PLAYER_NUMBER_OPTIONS =
            Arrays.asList(2, 3, 4, 5, 6);
    private static final List<String> PLAYER_COLORS =
            Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    private final RoboRally roboRally;
    private GameController gameController;

    /**
     * Constructs a new AppController.
     *
     * @param roboRally the main application instance (JavaFX stage manager)
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Sends a request to create a new game on the backend server.
     *
     * @param gameName   the desired name for the new game
     * @param minPlayers the minimum number of players allowed
     * @param maxPlayers the maximum number of players allowed
     */
    public void createNewGame(String gameName, int minPlayers, int maxPlayers) {
        try {
            Game newGame = new Game(gameName, minPlayers, maxPlayers);
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format(
                    "{\"name\":\"%s\",\"minPlayers\":%d,\"maxPlayers\":%d}",
                    gameName, minPlayers, maxPlayers);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/games"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                showInfo("Game " + gameName + " created successfully!");
            } else {
                showError("Failed to create game: " + response.body());
            }
        } catch (Exception e) {
            showError("Could not create game: " + e.getMessage());
        }
    }

    /**
     * Attempts to sign in an existing user against the backend.
     *
     * @param name     the username
     * @param password the password
     */
    public void handleSignIn(String name, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format(
                    "{\"name\":\"%s\",\"password\":\"%s\"}",
                    name, password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/users/signin"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(User.class, new UserAdapter())
                        .create();
                User user = gson.fromJson(response.body(), User.class);
                OnlineState.getInstance().setCurrentUser(user);
                showInfo("Signed in as " + user.getName());
            } else if (response.statusCode() == 401) {
                showError("Incorrect username or password.");
            } else {
                showError("Sign in failed: " + response.body());
            }
        } catch (Exception e) {
            showError("Could not sign in: " + e.getMessage());
        }
    }

    /**
     * Attempts to register a new user on the backend.
     *
     * @param name     the desired username
     * @param password the desired password
     */
    public void handleSignUp(String name, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format(
                    "{\"name\":\"%s\",\"password\":\"%s\"}",
                    name, password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/users/signup"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(User.class, new UserAdapter())
                        .create();
                User user = gson.fromJson(response.body(), User.class);
                OnlineState.getInstance().setCurrentUser(user);
                showInfo("Signed up as " + user.getName());
            } else if (response.statusCode() == 409) {
                showError("Username already exists. Please choose another.");
            } else {
                showError("Sign up failed: " + response.body());
            }
        } catch (Exception e) {
            showError("Could not sign up: " + e.getMessage());
        }
    }

    /**
     * Opens a dialog to choose the number of players, then starts a new local game.
     */
    public void newGame() {
        ChoiceDialog<Integer> dialog =
                new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (gameController != null && !stopGame()) {
                return;
            }
            Board board = new Board(8, 8);
            gameController = new GameController(board);
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(
                        board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }
            gameController.startProgrammingPhase();
            roboRally.createBoardView(gameController);
        }
    }

    /** Placeholder for save‐game functionality (to be implemented). */
    public void saveGame() { }

    /** Placeholder for load‐game functionality (to be implemented). */
    public void loadGame() {
        if (gameController == null) {
            newGame();
        }
    }

    /**
     * Stops the current game, optionally saving it first.
     *
     * @return true if there was a game to stop, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {
            saveGame();
            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /** Opens the game‐selection view if no game is running. */
    public void selectGame() {
        if (gameController == null) {
            roboRally.createGameSelectionView(this);
        }
    }

    /** Returns to the selection view after a game is chosen (not yet implemented). */
    public void gameSelected(Game game) {
        if (gameController == null) {
            roboRally.createGameSelectionView(null);
        }
    }

    /**
     * Exits the application, prompting to save if a game is active.
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    /**
     * Displays an error alert with the given message.
     *
     * @param message the error message to show
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an information alert with the given message.
     *
     * @param message the info message to show
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Checks whether a game is currently running.
     *
     * @return true if a game session is active; false otherwise
     */
    public boolean isGameRunning() {
        return gameController != null;
    }

    @Override
    public void update(Subject subject) {
        // Currently not used
    }

    /**
     * (Unused) Returns the signed-in user from this controller.
     * <p>Note: This method currently calls itself recursively and
     * should be implemented or removed.</p>
     *
     * @return the signed-in User
     */
    public User getSignedInUser() {
        AppController onlineState = null;
        return onlineState.getSignedInUser();
    }
}
