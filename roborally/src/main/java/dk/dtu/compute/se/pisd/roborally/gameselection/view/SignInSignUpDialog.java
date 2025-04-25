package dk.dtu.compute.se.pisd.roborally.gameselection.view;



import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SignInSignUpDialog {

    // Metode til at vise login/registrer-pop-up
    public static void display() {
        // Opret et nyt "Stage" (et pop-up vindue)
        Stage dialog = new Stage();
        dialog.setTitle("Sign In or Sign Up");

        // Layout til pop-up (VBox layout med plads til felter/knapper)
        VBox dialogVBox = new VBox(10);
        dialogVBox.setSpacing(10);
        dialogVBox.setStyle("-fx-padding: 20;");

        // Felter til e-mail og adgangskode
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Opret Sign In-knap
        Button signInButton = new Button("Sign In");
        signInButton.setOnAction(event -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            System.out.println("Sign In triggeret: " + email + ", " + password);
            // Backend/REST-API-kald for sign-in tilføjes senere
        });

        // Opret Sign Up-knap
        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(event -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            System.out.println("Sign Up triggeret: " + email + ", " + password);
            // Backend/REST-API-kald for sign-up tilføjes senere
        });

        // Tilføj felter og knapper til layout
        dialogVBox.getChildren().addAll(
                new Label("Enter your email and password:"),
                emailField,
                passwordField,
                new HBox(10, signInButton, signUpButton) // Knapper arrangeret i samme linje
        );

        // Opret en Scene og tilknyt den til vinduet
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialog.setScene(dialogScene);

        // Vis pop-up'en
        dialog.showAndWait();
    }
}
