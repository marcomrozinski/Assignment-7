package dk.dtu.compute.se.pisd.roborally.gameselection.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * A modal dialog that allows the user to either sign in with existing credentials
 * or sign up for a new account.
 * <p>
 * Presents two initial buttons ("Sign In" / "Sign Up"), then dynamically switches
 * to the appropriate input form based on the user's choice.
 * </p>
 */
public class SignInSignUpDialog {

    /**
     * Displays the sign-in / sign-up dialog and waits for it to be closed.
     *
     * @param appController the controller used to perform sign-in or sign-up actions
     */
    public static void display(AppController appController) {
        Stage dialog = new Stage();
        dialog.setTitle("Sign In or Sign Up");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setSpacing(10);
        dialogVBox.setStyle("-fx-padding: 20;");

        Label label = new Label("Choose action:");
        Button signInButton = new Button("Sign In");
        Button signUpButton = new Button("Sign Up");

        // Initial view: two buttons
        dialogVBox.getChildren().addAll(label, new HBox(10, signInButton, signUpButton));

        // Sign In form
        signInButton.setOnAction(e -> {
            dialogVBox.getChildren().clear();

            Label inputLabel = new Label("Enter your username and password:");
            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");

            Button confirmButton = new Button("Confirm Sign In");
            confirmButton.setOnAction(event -> {
                String username = usernameField.getText();
                String password = passwordField.getText();
                if (!username.isEmpty() && !password.isEmpty()) {
                    appController.handleSignIn(username, password);
                    dialog.close();
                }
            });

            dialogVBox.getChildren().addAll(inputLabel, usernameField, passwordField, confirmButton);
        });

        // Sign Up form
        signUpButton.setOnAction(e -> {
            dialogVBox.getChildren().clear();

            Label inputLabel = new Label("Enter a username and password to register:");

            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");

            PasswordField confirmPasswordField = new PasswordField();
            confirmPasswordField.setPromptText("Confirm Password");

            Button confirmButton = new Button("Confirm Sign Up");
            confirmButton.setOnAction(event -> {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    showError("All fields must be filled out.");
                } else if (!password.equals(confirmPassword)) {
                    showError("Passwords do not match.");
                } else {
                    appController.handleSignUp(username, password);
                    dialog.close();
                }
            });

            dialogVBox.getChildren().addAll(
                    inputLabel,
                    usernameField,
                    passwordField,
                    confirmPasswordField,
                    confirmButton
            );
        });

        Scene scene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Displays an error alert with the specified message.
     *
     * @param message the error message to show in the alert
     */
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
