package com.biblioteca.ui.view;

import com.biblioteca.security.UserSession;
import com.biblioteca.ui.controller.BookAdminController;
import com.biblioteca.ui.controller.CatalogController;
import com.biblioteca.ui.controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    private final LoginController loginController;
    private final CatalogController catalogController;
    private final BookAdminController bookAdminController;
    private final Stage stage;
    private final TextField usernameField;
    private final PasswordField passwordField;

    public LoginView(LoginController loginController,
                     CatalogController catalogController,
                     BookAdminController bookAdminController,
                     Stage stage) {
        this.loginController = loginController;
        this.catalogController = catalogController;
        this.bookAdminController = bookAdminController;
        this.stage = stage;
        this.usernameField = new TextField();
        this.passwordField = new PasswordField();
        configureStage();
    }

    public void show() {
        stage.show();
    }

    private void configureStage() {
        stage.setTitle("Acceso a Biblioteca");
        stage.setScene(new Scene(buildRoot(), 460, 260));
    }

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));

        Label title = new Label("Indice Digital de Biblioteca");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Usuario"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Contrasena"), 0, 1);
        form.add(passwordField, 1, 1);

        Button guestButton = new Button("Entrar como invitado");
        guestButton.setOnAction(event -> openCatalog(loginController.loginAsGuest()));

        Button adminButton = new Button("Entrar como administrador");
        adminButton.setOnAction(event -> loginAsAdmin());

        HBox actions = new HBox(10, guestButton, adminButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox center = new VBox(18, title, form, actions);
        root.setCenter(center);
        return root;
    }

    private void loginAsAdmin() {
        try {
            UserSession session = loginController.login(usernameField.getText(), passwordField.getText());
            openCatalog(session);
        } catch (IllegalArgumentException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setHeaderText("Acceso denegado");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }
    }

    private void openCatalog(UserSession session) {
        CatalogView catalogView = new CatalogView(catalogController, bookAdminController, session);
        catalogView.show();
        stage.close();
    }
}
