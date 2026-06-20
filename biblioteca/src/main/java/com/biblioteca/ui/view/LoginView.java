package com.biblioteca.ui.view;

import com.biblioteca.security.UserSession;
import com.biblioteca.ui.controller.LoginController;
import com.biblioteca.ui.style.DesignTokens;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginView {
    private final LoginController loginController;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Label errorLabel;
    private UserSession result;

    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        this.usernameField = new TextField();
        this.passwordField = new PasswordField();
        this.errorLabel = new Label();
        usernameField.setAccessibleText("Nombre de usuario administrador");
        passwordField.setAccessibleText("Contraseña");
    }

    public UserSession showAndWait(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Iniciar sesión");
        dialog.setResizable(false);
        dialog.setScene(new Scene(buildRoot(), 400, 320));
        dialog.showAndWait();
        return result;
    }

    private VBox buildRoot() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle(DesignTokens.bg(DesignTokens.BG_PRIMARY));

        VBox card = new VBox(16);
        card.setMaxWidth(320);
        card.setPadding(new Insets(28));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 8);");
        card.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Administrador");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");

        Label subtitle = new Label("Inicia sesión para gestionar el catálogo");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        usernameField.setPromptText("Usuario");
        usernameField.setStyle(inputStyle());
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) ->
                usernameField.setStyle(newVal ? inputFocusStyle() : inputStyle()));

        passwordField.setPromptText("Contraseña");
        passwordField.setStyle(inputStyle());
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) ->
                passwordField.setStyle(newVal ? inputFocusStyle() : inputStyle()));

        Button loginButton = new Button("Entrar");
        loginButton.setDefaultButton(true);
        loginButton.setStyle("-fx-background-color: " + DesignTokens.toRgba(DesignTokens.BLUE_PRIMARY)
                + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"
                + " -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 0;");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(event -> login());
        loginButton.setOnMouseEntered(e ->
                loginButton.setStyle("-fx-background-color: " + DesignTokens.toRgba(DesignTokens.BLUE_HOVER)
                        + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"
                        + " -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 0;"));
        loginButton.setOnMouseExited(e ->
                loginButton.setStyle("-fx-background-color: " + DesignTokens.toRgba(DesignTokens.BLUE_PRIMARY)
                        + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"
                        + " -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 0;"));

        errorLabel.setStyle("-fx-text-fill: " + DesignTokens.toRgba(DesignTokens.ACCENT_ERROR)
                + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);

        card.getChildren().addAll(title, subtitle, usernameField, passwordField, loginButton, errorLabel);
        root.getChildren().add(card);
        return root;
    }

    private void login() {
        errorLabel.setVisible(false);
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            errorLabel.setText("Usuario y contraseña son requeridos");
            errorLabel.setVisible(true);
            return;
        }
        try {
            result = loginController.login(username, password);
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IllegalArgumentException exception) {
            errorLabel.setText("Credenciales inválidas");
            errorLabel.setVisible(true);
        }
    }

    private String inputStyle() {
        return "-fx-background-color: #f1f5f9; -fx-text-fill: #1a1a2e; -fx-prompt-text-fill: #94a3b8;"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;"
                + " -fx-padding: 10 14; -fx-font-size: 13px;";
    }

    private String inputFocusStyle() {
        return "-fx-background-color: #f8fafc; -fx-text-fill: #1a1a2e; -fx-prompt-text-fill: #94a3b8;"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: "
                + DesignTokens.toRgba(DesignTokens.BLUE_GLOW) + "; -fx-padding: 10 14; -fx-font-size: 13px;";
    }
}
