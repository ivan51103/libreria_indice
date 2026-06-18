package com.biblioteca.ui.view;

import com.biblioteca.security.UserSession;
import com.biblioteca.ui.controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private UserSession result;

    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        this.usernameField = new TextField();
        this.passwordField = new PasswordField();
    }

    public UserSession showAndWait(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Iniciar sesión");
        dialog.setResizable(false);
        dialog.setScene(new Scene(buildRoot(), 420, 240));
        dialog.showAndWait();
        return result;
    }

    private VBox buildRoot() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(18));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Inicio de sesión de administrador");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Usuario"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Contraseña"), 0, 1);
        form.add(passwordField, 1, 1);

        Button loginButton = new Button("Entrar");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(event -> login());

        root.getChildren().addAll(title, form, loginButton);
        return root;
    }

    private void login() {
        try {
            result = loginController.login(usernameField.getText(), passwordField.getText());
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IllegalArgumentException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Credenciales inválidas");
            alert.setResizable(false);
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.setResizable(false);
            alertStage.setWidth(300);
            alertStage.setHeight(140);
            alert.showAndWait();
        }
    }
}
