package dk.dtu.compute.se.pisd.roborally.gameselection.view;



import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SignInSignUpDialog {

    public static void display(AppController appController) {
        Stage dialog = new Stage();
        dialog.setTitle("Sign In or Sign Up");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setSpacing(10);
        dialogVBox.setStyle("-fx-padding: 20;");

        Label label = new Label("Choose action:");
        Button signInButton = new Button("Sign In");
        Button signUpButton = new Button("Sign Up");

        // Startvisning – kun to knapper
        dialogVBox.getChildren().addAll(label, new HBox(10, signInButton, signUpButton));

        // Handling for Sign In
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

        // Handling for Sign Up
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
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
