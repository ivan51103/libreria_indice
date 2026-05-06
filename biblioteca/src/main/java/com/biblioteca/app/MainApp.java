package com.biblioteca.app;

import com.biblioteca.config.AppConfig;
import com.biblioteca.ui.controller.BookAdminController;
import com.biblioteca.ui.controller.CatalogController;
import com.biblioteca.ui.controller.LoginController;
import com.biblioteca.ui.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        AppConfig config = new AppConfig();
        LoginController loginController = new LoginController(config.getAuthenticationService());
        CatalogController catalogController = new CatalogController(config.getCatalogService());
        BookAdminController bookAdminController = new BookAdminController(config.getInventoryService());

        LoginView loginView = new LoginView(loginController, catalogController, bookAdminController, primaryStage);
        loginView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
