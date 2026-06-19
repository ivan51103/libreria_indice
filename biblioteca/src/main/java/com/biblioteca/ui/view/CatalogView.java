package com.biblioteca.ui.view;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.search.query.BookCatalogItemView;
import com.biblioteca.search.query.BookDetailViewModel;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.BookSortField;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import com.biblioteca.search.query.SortDirection;
import com.biblioteca.security.UserRole;
import com.biblioteca.security.UserSession;
import com.biblioteca.ui.controller.BookAdminController;
import com.biblioteca.ui.controller.CatalogController;
import com.biblioteca.ui.controller.LoginController;
import com.biblioteca.ui.style.DesignTokens;
import com.biblioteca.util.CoverResolver;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CatalogView {
    private final CatalogController catalogController;
    private final BookAdminController bookAdminController;
    private final LoginController loginController;
    private UserSession session;
    private final Stage stage;
    private final TextField titleSearchField;
    private final Button searchButton;
    private final Button clearButton;
    private final Button registerButton;
    private final Button editButton;
    private final Button loginButton;
    private final Button logoutButton;
    private final Label roleBadge;
    private final TilePane catalogGrid;
    private final ScrollPane catalogScrollPane;
    private final Label statusLabel;
    private final Label feedbackLabel;
    private final StackPane detailCoverPane;
    private final Label detailTitleLabel;
    private final Label detailAuthorLabel;
    private final Label detailIsbnLabel;
    private final Label detailPublisherLabel;
    private final Label detailYearLabel;
    private final Label detailCategoryLabel;
    private final Label detailCareerLabel;
    private final Label detailAvailabilityLabel;
    private final Label detailDescriptionLabel;
    private final Label detailLocationLabel;
    private final Label detailCopyStatusLabel;
    private final Label detailNotesLabel;
    private final ComboBox<BookCopy> copySelector;
    private final ComboBox<CopyStatus> statusSelector;
    private final VBox adminPanel;

    private final VBox filterPanel;
    private final FlowPane alphabetPane;
    private final ToggleGroup sortGroup;
    private final RadioButton sortAZ;
    private final RadioButton sortZA;
    private final VBox careerList;
    private final Map<String, CheckBox> careerCheckboxes = new HashMap<>();
    private final Set<String> selectedLetters = new HashSet<>();
    private Button todasButton;

    private Timeline searchDebounce;

    private static final Map<String, String> CAREER_ABBREVIATIONS = Map.ofEntries(
        Map.entry("LICENCIATURA EN ADMINISTRACIÓN Y GESTIÓN DE NEGOCIOS EMPRENDEDORES", "ADMINISTRACIÓN"),
        Map.entry("LICENCIATURA EN INGENIERÍA EN ADMINISTRACIÓN INDUSTRIAL", "INGENIERÍA"),
        Map.entry("LICENCIATURA EN MERCADOTECNIA ESTRATÉGICA", "MERCADOTECNIA"),
        Map.entry("LICENCIATURA EN DISEÑO GRÁFICO", "DISEÑO"),
        Map.entry("LICENCIATURA EN LENGUAS EXTRANJERAS", "LENGUAS"),
        Map.entry("LICENCIATURA EN SISTEMAS COMPUTACIONALES", "SISTEMAS"),
        Map.entry("LICENCIATURA EN COMUNICACIÓN", "COMUNICACIÓN")
    );

    private BookDetailViewModel currentDetail;
    private Long selectedBookTitleId;
    private final Map<Long, StackPane> cardsByBookId = new HashMap<>();

    public CatalogView(CatalogController catalogController,
                       BookAdminController bookAdminController,
                       LoginController loginController,
                       UserSession session) {
        this.catalogController = catalogController;
        this.bookAdminController = bookAdminController;
        this.loginController = loginController;
        this.session = session;
        this.stage = new Stage();
        this.titleSearchField = new TextField();
        this.searchButton = new Button("Buscar");
        this.clearButton = new Button("Limpiar");
        this.registerButton = new Button("Registrar libro");
        this.editButton = new Button("Editar seleccionado");
        this.loginButton = new Button("Iniciar sesión");
        this.logoutButton = new Button("Cerrar sesión");
        this.roleBadge = new Label(translateRole(session.getCurrentUser().getRole()));
        this.catalogGrid = new TilePane();
        this.catalogScrollPane = new ScrollPane(catalogGrid);
        this.statusLabel = new Label();
        this.feedbackLabel = new Label();
        this.detailCoverPane = new StackPane();
        this.detailTitleLabel = new Label("Selecciona un libro");
        this.detailAuthorLabel = new Label();
        this.detailIsbnLabel = new Label();
        this.detailPublisherLabel = new Label();
        this.detailYearLabel = new Label();
        this.detailCategoryLabel = new Label();
        this.detailCareerLabel = new Label();
        this.detailAvailabilityLabel = new Label();
        this.detailDescriptionLabel = new Label();
        this.detailLocationLabel = new Label();
        this.detailCopyStatusLabel = new Label();
        this.detailNotesLabel = new Label();
        this.copySelector = new ComboBox<>();
        this.statusSelector = new ComboBox<>();
        this.adminPanel = new VBox();
        this.filterPanel = new VBox();
        this.alphabetPane = new FlowPane();
        this.sortGroup = new ToggleGroup();
        this.sortAZ = new RadioButton("A a Z");
        this.sortZA = new RadioButton("Z a A");
        this.careerList = new VBox();
        configureStage();
        titleSearchField.setAccessibleText("Buscar por título, autor, categoría o carrera");
        searchButton.setAccessibleText("Ejecutar búsqueda");
        clearButton.setAccessibleText("Limpiar filtros y búsqueda");
        registerButton.setAccessibleText("Registrar nuevo libro");
        editButton.setAccessibleText("Editar libro seleccionado");
        loginButton.setAccessibleText("Iniciar sesión como administrador");
        logoutButton.setAccessibleText("Cerrar sesión");
        copySelector.setAccessibleText("Seleccionar ejemplar");
        statusSelector.setAccessibleText("Seleccionar nuevo estado del ejemplar");
    }

    public void show() {
        loadCareers();
        loadCatalog();
        stage.show();
    }

    private void configureStage() {
        stage.setTitle("Catalogo de Biblioteca");
        stage.setMinWidth(1400);
        stage.setMinHeight(780);
        Scene scene = new Scene(buildRoot(), 1420, 820);
        scene.getStylesheets().add(getClass().getResource("/scrollbar-style.css").toExternalForm());
        stage.setScene(scene);
    }

    private VBox buildRoot() {
        VBox root = new VBox(8, buildTopBar(), buildContent());
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setStyle(DesignTokens.bg(DesignTokens.BG_SURFACE));
        return root;
    }

    private Node buildTopBar() {
        Label titleLabel = new Label("Indice Digital de Biblioteca");
        titleLabel.setFont(Font.font("Segoe UI", 24));
        titleLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY) + " -fx-font-weight: bold;");

        roleBadge.setStyle(
            DesignTokens.bg(DesignTokens.BLUE_BG_SUBTLE) + DesignTokens.textFill(DesignTokens.BLUE_LIGHT)
            + " -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 999; -fx-padding: 2 10;"
        );

        titleSearchField.setPromptText("Buscar por titulo, autor, categoria o carrera");
        titleSearchField.setPrefColumnCount(30);
        titleSearchField.setStyle(
            "-fx-background-color: rgba(255,255,255,0.10); -fx-text-fill: white; "
            + "-fx-prompt-text-fill: #94a3b8; -fx-background-radius: 6; -fx-border-radius: 6; "
            + "-fx-border-color: rgba(255,255,255,0.15); -fx-padding: 6 12;"
        );
        titleSearchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                titleSearchField.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; "
                    + "-fx-prompt-text-fill: #94a3b8; -fx-background-radius: 6; -fx-border-radius: 6; "
                    + "-fx-border-color: " + DesignTokens.toRgba(DesignTokens.BLUE_GLOW) + "; -fx-padding: 6 12;"
                );
            } else {
                titleSearchField.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.10); -fx-text-fill: white; "
                    + "-fx-prompt-text-fill: #94a3b8; -fx-background-radius: 6; -fx-border-radius: 6; "
                    + "-fx-border-color: rgba(255,255,255,0.15); -fx-padding: 6 12;"
                );
            }
        });

        Button searchBtn = new Button("Buscar");
        searchBtn.setStyle(netflixButtonStyle(DesignTokens.BLUE_PRIMARY));
        searchBtn.setOnAction(event -> loadCatalog());

        Button clearBtn = new Button("Limpiar");
        clearBtn.setStyle(netflixButtonStyle(DesignTokens.BG_ELEVATED));
        clearBtn.setOnAction(event -> {
            titleSearchField.clear();
            resetFilters();
            loadCatalog();
        });

        titleSearchField.setOnAction(event -> loadCatalog());
        titleSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (searchDebounce != null) searchDebounce.stop();
            searchDebounce = new Timeline(new KeyFrame(Duration.millis(300), e -> loadCatalog()));
            searchDebounce.setCycleCount(1);
            searchDebounce.play();
        });

        HBox searchRow = new HBox(8, titleSearchField, searchBtn, clearBtn);
        searchRow.setAlignment(Pos.CENTER);

        loginButton.setVisible(!isAdmin());
        loginButton.setManaged(!isAdmin());
        loginButton.setOnAction(event -> openLoginDialog());
        loginButton.setStyle(netflixButtonStyle(DesignTokens.BG_ELEVATED));

        logoutButton.setVisible(isAdmin());
        logoutButton.setManaged(isAdmin());
        logoutButton.setOnAction(event -> logout());
        logoutButton.setStyle(netflixButtonStyle(DesignTokens.ACCENT_ERROR));

        registerButton.setVisible(isAdmin());
        registerButton.setManaged(isAdmin());
        registerButton.setOnAction(event -> openRegisterDialog());
        registerButton.setStyle(netflixButtonStyle(DesignTokens.BLUE_PRIMARY));

        editButton.setVisible(isAdmin());
        editButton.setManaged(isAdmin());
        editButton.setOnAction(event -> openEditDialog());
        editButton.setStyle(netflixButtonStyle(DesignTokens.BG_ELEVATED));

        loginButton.setStyle(netflixButtonStyle(DesignTokens.BG_ELEVATED));
        logoutButton.setStyle(netflixButtonStyle(DesignTokens.ACCENT_ERROR));

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, javafx.scene.layout.Priority.ALWAYS);
        Region midSpacer = new Region();
        HBox.setHgrow(midSpacer, javafx.scene.layout.Priority.ALWAYS);
        HBox topRow = new HBox(leftSpacer, titleLabel, roleBadge, midSpacer, loginButton, logoutButton);
        topRow.setAlignment(Pos.CENTER);

        HBox actions = new HBox(8, registerButton, editButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        feedbackLabel.setStyle(DesignTokens.textFill(DesignTokens.ACCENT_SUCCESS) + " -fx-font-size: 12px; -fx-font-weight: bold;");
        feedbackLabel.setVisible(false);

        VBox wrapper = new VBox(4, topRow, searchRow, actions, feedbackLabel);
        wrapper.setPadding(new Insets(8, 16, 4, 16));
        wrapper.setStyle(DesignTokens.bg(DesignTokens.BG_SURFACE)
            + " -fx-border-width: 0 0 1 0; -fx-border-color: rgba(255,255,255,0.08);");
        return wrapper;
    }

    private String netflixButtonStyle(Color bg) {
        return "-fx-background-color: " + DesignTokens.toRgba(bg) + "; -fx-text-fill: white; "
                + "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 6; "
                + "-fx-cursor: hand; -fx-padding: 6 14;";
    }

    private Node buildContent() {
        catalogGrid.setHgap(DesignTokens.CARD_HGAP);
        catalogGrid.setVgap(DesignTokens.CARD_VGAP);
        catalogGrid.setPadding(DesignTokens.PADDING_CONTENT);
        catalogGrid.setPrefTileWidth(DesignTokens.CARD_WIDTH);
        catalogGrid.setPrefTileHeight(DesignTokens.CARD_HEIGHT);
        catalogGrid.setTileAlignment(Pos.TOP_LEFT);
        catalogGrid.setPrefColumns(5);

        catalogScrollPane.setFitToWidth(true);
        catalogScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        catalogScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        catalogScrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double totalTileWidth = DesignTokens.CARD_WIDTH + DesignTokens.CARD_HGAP;
            int columns = Math.max(2, (int) ((newVal.doubleValue() - DesignTokens.CARD_HGAP) / totalTileWidth));
            catalogGrid.setPrefColumns(columns);
        });

        VBox filterPane = buildFilterPane();

        Label catalogHeading = new Label("Catalogo");
        catalogHeading.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-size: 16px; -fx-font-weight: bold;");
        catalogHeading.setAlignment(Pos.CENTER);
        catalogHeading.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_MUTED) + " -fx-font-size: 12px;");
        StackPane catalogHeader = new StackPane(catalogHeading, statusLabel);
        StackPane.setAlignment(statusLabel, Pos.CENTER_RIGHT);
        VBox leftPane = new VBox(10, catalogHeader, catalogScrollPane);
        VBox.setVgrow(catalogScrollPane, javafx.scene.layout.Priority.ALWAYS);
        leftPane.setStyle(DesignTokens.bg(DesignTokens.BG_SURFACE) + " -fx-padding: 0 4;"
                + " -fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 1 0 1;");

        ScrollPane rightPane = buildDetailPane();
        rightPane.setPrefWidth(DesignTokens.DETAIL_PANEL_WIDTH);
        rightPane.setMinWidth(DesignTokens.DETAIL_PANEL_WIDTH);
        rightPane.setMaxWidth(DesignTokens.DETAIL_PANEL_WIDTH);

        HBox content = new HBox(filterPane, leftPane, rightPane);
        HBox.setHgrow(leftPane, javafx.scene.layout.Priority.ALWAYS);
        return content;
    }

    private VBox buildFilterPane() {
        Label title = new Label("Filtros");
        title.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-size: 16px; -fx-font-weight: bold;");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        VBox sortSection = buildSortSection();
        VBox alphabetSection = buildAlphabetSection();
        VBox careerSection = buildCareerSection();

        filterPanel.getChildren().setAll(title, sortSection, alphabetSection, careerSection);
        filterPanel.setSpacing(DesignTokens.SPACING_MEDIUM);
        filterPanel.setPadding(DesignTokens.PADDING_PANEL);
        filterPanel.setPrefWidth(DesignTokens.FILTER_PANEL_WIDTH);
        filterPanel.setMinWidth(DesignTokens.FILTER_PANEL_WIDTH);
        filterPanel.setMaxWidth(DesignTokens.FILTER_PANEL_WIDTH);
        filterPanel.setStyle(DesignTokens.bg(DesignTokens.BG_SURFACE)
                + " -fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 1 0 0;");
        return filterPanel;
    }

    private VBox buildSortSection() {
        Label heading = new Label("Ordenar por");
        heading.setStyle(DesignTokens.textFill(DesignTokens.BLUE_LIGHT)
                + " -fx-font-size: 12px; -fx-font-weight: bold;");

        sortAZ.setToggleGroup(sortGroup);
        sortZA.setToggleGroup(sortGroup);
        sortAZ.setSelected(true);
        String sortBaseStyle = "-fx-text-fill: white; -fx-font-size: 12px; "
                + "-fx-background-radius: 999; -fx-padding: 4 10; -fx-cursor: hand;";
        sortAZ.setStyle(sortBaseStyle + " -fx-background-color: " + DesignTokens.toRgba(DesignTokens.BLUE_PRIMARY) + ";");
        sortZA.setStyle(sortBaseStyle + " -fx-background-color: " + DesignTokens.toRgba(DesignTokens.BG_ELEVATED) + ";");

        sortGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            sortAZ.setStyle(sortBaseStyle + " -fx-background-color: "
                    + (newVal == sortAZ ? DesignTokens.toRgba(DesignTokens.BLUE_PRIMARY) : DesignTokens.toRgba(DesignTokens.BG_ELEVATED)) + ";");
            sortZA.setStyle(sortBaseStyle + " -fx-background-color: "
                    + (newVal == sortZA ? DesignTokens.toRgba(DesignTokens.BLUE_PRIMARY) : DesignTokens.toRgba(DesignTokens.BG_ELEVATED)) + ";");
            loadCatalog();
        });

        HBox toggleRow = new HBox(6, sortAZ, sortZA);
        VBox section = new VBox(DesignTokens.SPACING_SMALL, heading, toggleRow);
        section.setStyle(DesignTokens.bg(DesignTokens.BG_CARD) + " -fx-padding: 12 12 12 6;");
        return section;
    }

    private VBox buildAlphabetSection() {
        Label heading = new Label("Primera letra");
        heading.setStyle(DesignTokens.textFill(DesignTokens.BLUE_LIGHT)
                + " -fx-font-size: 12px; -fx-font-weight: bold;");

        todasButton = new Button("TODAS");
        todasButton.setStyle(alphabetButtonStyle(true));
        todasButton.setOnAction(event -> {
            selectedLetters.clear();
            todasButton.setStyle(alphabetButtonStyle(true));
            for (Button b : alphabetButtons) {
                b.setStyle(alphabetButtonStyle(false));
            }
            loadCatalog();
        });

        alphabetPane.setHgap(4);
        alphabetPane.setVgap(4);
        alphabetPane.setPrefWrapLength(180);
        alphabetPane.getChildren().add(todasButton);

        for (char c = 'A'; c <= 'Z'; c++) {
            String letter = String.valueOf(c);
            Button btn = new Button(letter);
            btn.setPrefSize(32, 28);
            btn.setStyle(alphabetButtonStyle(false));
            btn.setOnAction(event -> {
                if (selectedLetters.contains(letter)) {
                    selectedLetters.remove(letter);
                    btn.setStyle(alphabetButtonStyle(false));
                } else {
                    selectedLetters.add(letter);
                    btn.setStyle(alphabetButtonStyle(true));
                }
                todasButton.setStyle(selectedLetters.isEmpty() ? alphabetButtonStyle(true) : alphabetButtonStyle(false));
                loadCatalog();
            });
            alphabetPane.getChildren().add(btn);
            alphabetButtons.add(btn);
        }

        VBox section = new VBox(DesignTokens.SPACING_SMALL, heading, alphabetPane);
        section.setStyle(DesignTokens.bg(DesignTokens.BG_CARD) + " -fx-padding: 12 12 12 6;");
        return section;
    }

    private final List<Button> alphabetButtons = new java.util.ArrayList<>();

    private String alphabetButtonStyle(boolean selected) {
        if (selected) {
            return "-fx-background-color: " + DesignTokens.toRgba(DesignTokens.BLUE_PRIMARY)
                    + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; "
                    + "-fx-border-color: " + DesignTokens.toRgba(DesignTokens.BLUE_GLOW)
                    + "; -fx-border-radius: 6; -fx-cursor: hand;";
        }
        return "-fx-background-color: rgba(255,255,255,0.10); -fx-text-fill: white; -fx-background-radius: 6; "
                + "-fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 6; -fx-cursor: hand;";
    }

    private VBox buildCareerSection() {
        Label heading = new Label("Carrera");
        heading.setStyle(DesignTokens.textFill(DesignTokens.BLUE_LIGHT)
                + " -fx-font-size: 12px; -fx-font-weight: bold;");

        careerList.setSpacing(DesignTokens.SPACING_SMALL);
        ScrollPane scroll = new ScrollPane(careerList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(180);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox section = new VBox(DesignTokens.SPACING_SMALL, heading, scroll);
        section.setStyle(DesignTokens.bg(DesignTokens.BG_CARD) + " -fx-padding: 12 12 12 8;");
        return section;
    }

    private void loadCareers() {
        List<String> careers = catalogController.getCareers();
        careerCheckboxes.clear();
        careerList.getChildren().clear();
        for (String career : careers) {
            String displayName = CAREER_ABBREVIATIONS.getOrDefault(career, career);
            CheckBox cb = new CheckBox(displayName);
            cb.setStyle("-fx-text-fill: white;");
            cb.setOnAction(event -> loadCatalog());
            careerCheckboxes.put(career, cb);
            careerList.getChildren().add(cb);
        }
    }

    private void resetFilters() {
        selectedLetters.clear();
        todasButton.setStyle(alphabetButtonStyle(true));
        for (Button b : alphabetButtons) {
            b.setStyle(alphabetButtonStyle(false));
        }
        sortAZ.setSelected(true);
        for (CheckBox cb : careerCheckboxes.values()) {
            cb.setSelected(false);
        }
    }

    private ScrollPane buildDetailPane() {
        detailCoverPane.setPrefSize(260, 360);
        detailCoverPane.setMinSize(260, 360);
        detailCoverPane.setMaxSize(260, 360);
        detailCoverPane.setStyle("-fx-border-color: rgba(255,255,255,0.12);"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0, 0, 4);");
        detailCoverPane.setBackground(new Background(new BackgroundFill(
                new javafx.scene.paint.LinearGradient(0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                        new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web("#0f4c81")),
                        new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web("#06121f"))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        Label coverFallback = new Label("Sin portada");
        coverFallback.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-size: 16px; -fx-font-weight: bold;");
        detailCoverPane.getChildren().add(coverFallback);

        detailTitleLabel.setFont(Font.font("Segoe UI", 24));
        detailTitleLabel.setWrapText(true);
        detailTitleLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-weight: bold; -fx-font-size: 24px;");
        detailAuthorLabel.setStyle(DesignTokens.textFill(DesignTokens.BLUE_LIGHT));
        detailIsbnLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_SECONDARY));
        detailPublisherLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_SECONDARY));
        detailYearLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_SECONDARY));
        detailCategoryLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_SECONDARY));
        detailCareerLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_SECONDARY));
        detailDescriptionLabel.setWrapText(true);
        detailDescriptionLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_SECONDARY)
                + " -fx-font-size: 13px; -fx-line-spacing: 3px;");
        detailAvailabilityLabel.setStyle(DesignTokens.textFill(DesignTokens.ACCENT_SUCCESS)
                + " -fx-font-weight: bold; -fx-font-size: 14px;");
        detailLocationLabel.setWrapText(true);
        detailLocationLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-size: 18px; -fx-font-weight: bold;");
        detailCopyStatusLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-weight: bold;");
        detailNotesLabel.setWrapText(true);
        detailNotesLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_SECONDARY));

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(12);
        infoGrid.setVgap(8);
        infoGrid.setStyle(DesignTokens.bg(DesignTokens.BLUE_BG_SUBTLE)
                + " -fx-background-radius: 8; -fx-padding: 12;");
        infoGrid.add(detailFieldLabel("Autor"), 0, 0);
        infoGrid.add(detailAuthorLabel, 1, 0);
        infoGrid.add(detailFieldLabel("ISBN"), 0, 1);
        infoGrid.add(detailIsbnLabel, 1, 1);
        infoGrid.add(detailFieldLabel("Editorial"), 0, 2);
        infoGrid.add(detailPublisherLabel, 1, 2);
        infoGrid.add(detailFieldLabel("Año"), 0, 3);
        infoGrid.add(detailYearLabel, 1, 3);
        infoGrid.add(detailFieldLabel("Categoria"), 0, 4);
        infoGrid.add(detailCategoryLabel, 1, 4);
        infoGrid.add(detailFieldLabel("Carrera"), 0, 5);
        infoGrid.add(detailCareerLabel, 1, 5);

        copySelector.setPromptText("Selecciona un ejemplar");
        copySelector.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(BookCopy copy) {
                if (copy == null) return "";
                return copy.getInventoryCode() + " - " + translateStatus(copy.getStatus());
            }
            @Override
            public BookCopy fromString(String string) { return null; }
        });
        copySelector.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(BookCopy copy, boolean empty) {
                super.updateItem(copy, empty);
                setText(empty || copy == null ? null : copy.getInventoryCode() + " - " + translateStatus(copy.getStatus()));
            }
        });
        copySelector.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(BookCopy copy, boolean empty) {
                super.updateItem(copy, empty);
                setText(empty || copy == null ? null : copy.getInventoryCode() + " - " + translateStatus(copy.getStatus()));
            }
        });
        copySelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) updateSelectedCopyDetail(newValue);
        });
        copySelector.setStyle("-fx-background-color: rgba(255,255,255,0.10); -fx-text-fill: white;"
                + " -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: rgba(255,255,255,0.15);");

        statusSelector.getItems().setAll(CopyStatus.values());
        statusSelector.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(CopyStatus status) { return status == null ? "" : translateStatus(status); }
            @Override
            public CopyStatus fromString(String string) { return null; }
        });
        statusSelector.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(CopyStatus status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty || status == null ? null : translateStatus(status));
            }
        });
        statusSelector.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(CopyStatus status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty || status == null ? null : translateStatus(status));
            }
        });
        statusSelector.setStyle("-fx-background-color: rgba(255,255,255,0.10); -fx-text-fill: white;"
                + " -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: rgba(255,255,255,0.15);");

        Button updateStatusButton = new Button("Cambiar estado");
        updateStatusButton.setStyle(netflixButtonStyle(DesignTokens.BLUE_PRIMARY));
        updateStatusButton.setOnAction(event -> updateSelectedCopyStatus());

        Label adminTitle = new Label("Acciones de administrador");
        adminTitle.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-weight: bold; -fx-font-size: 14px;");
        Label statusEditLabel = new Label("Nuevo estado");
        statusEditLabel.setStyle(DesignTokens.textFill(DesignTokens.BLUE_LIGHT) + " -fx-font-size: 12px;");

        adminPanel.getChildren().setAll(
                adminTitle, statusEditLabel, statusSelector, updateStatusButton);
        adminPanel.setSpacing(DesignTokens.SPACING_BASE);
        adminPanel.setStyle(DesignTokens.bg(DesignTokens.BLUE_BG_SUBTLE)
                + " -fx-background-radius: 8; -fx-padding: 12;");
        adminPanel.setVisible(isAdmin());
        adminPanel.setManaged(isAdmin());

        VBox coverBlock = new VBox(DesignTokens.SPACING_BASE, detailCoverPane, detailAvailabilityLabel);
        coverBlock.setAlignment(Pos.TOP_CENTER);

        VBox descriptionBlock = new VBox(8, sectionHeading("Descripcion"), detailDescriptionLabel);
        descriptionBlock.setStyle(DesignTokens.bg(DesignTokens.BLUE_BG_SUBTLE)
                + " -fx-background-radius: 8; -fx-padding: 14;");

        GridPane copyGrid = new GridPane();
        copyGrid.setHgap(12);
        copyGrid.setVgap(8);
        copyGrid.setStyle(DesignTokens.bg(DesignTokens.BLUE_BG_SUBTLE)
                + " -fx-background-radius: 8; -fx-padding: 12;");
        copyGrid.add(detailFieldLabel("Ejemplar"), 0, 0);
        copyGrid.add(copySelector, 1, 0);
        copyGrid.add(detailFieldLabel("Estado"), 0, 1);
        copyGrid.add(detailCopyStatusLabel, 1, 1);
        copyGrid.add(detailFieldLabel("Notas"), 0, 2);
        copyGrid.add(detailNotesLabel, 1, 2);

        Label locationHeading = new Label("Codigo de ubicacion");
        locationHeading.setStyle(DesignTokens.textFill(DesignTokens.TEXT_MUTED) + " -fx-font-size: 11px; -fx-font-weight: bold;");
        VBox locationBlock = new VBox(4, locationHeading, detailLocationLabel);
        locationBlock.setStyle(DesignTokens.bg(DesignTokens.BG_CARD)
                + " -fx-background-radius: 8; -fx-padding: 14; -fx-border-color: "
                + DesignTokens.toRgba(DesignTokens.BLUE_GLOW) + "; -fx-border-radius: 8; -fx-border-width: 1;");
        detailLocationLabel.setStyle(DesignTokens.textFill(DesignTokens.BLUE_LIGHT)
                + " -fx-font-size: 20px; -fx-font-weight: bold;");

        Label detailHeading = new Label("Detalles del libro");
        detailHeading.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-size: 16px; -fx-font-weight: bold;");
        detailHeading.setAlignment(Pos.CENTER);
        detailHeading.setMaxWidth(Double.MAX_VALUE);
        VBox detailBox = new VBox(DesignTokens.SPACING_MEDIUM,
                detailHeading, coverBlock, detailTitleLabel, infoGrid, descriptionBlock,
                copyGrid, locationBlock, adminPanel);
        detailBox.setPadding(DesignTokens.PADDING_PANEL);
        detailBox.setStyle(DesignTokens.bg(DesignTokens.BG_SURFACE)
                + " -fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 0 1;");

        ScrollPane detailScroll = new ScrollPane(detailBox);
        detailScroll.setFitToWidth(true);
        detailScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        detailScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
        return detailScroll;
    }

    private void loadCatalog() {
        statusLabel.setText("Cargando...");
        try {
            BookSearchCriteria criteria = new BookSearchCriteria();
            criteria.setText(titleSearchField.getText());

            if (!selectedLetters.isEmpty()) {
                criteria.setFirstLetters(new HashSet<>(selectedLetters));
            }

            Set<String> selectedCareers = new HashSet<>();
            for (Map.Entry<String, CheckBox> entry : careerCheckboxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    selectedCareers.add(entry.getKey());
                }
            }
            if (!selectedCareers.isEmpty()) {
                criteria.setCareers(selectedCareers);
            }

            PageRequest request = new PageRequest();
            request.setPage(0);
            request.setSize(100);
            request.setSortField(BookSortField.TITLE);
            request.setDirection(sortZA.isSelected() ? SortDirection.DESC : SortDirection.ASC);

            PageResult<BookCatalogItemView> result = catalogController.loadCatalog(criteria, request, isAdmin());
            statusLabel.setText(result.getTotalItems() + " libros encontrados");

            List<BookCatalogItemView> items = result.getItems();
            cardsByBookId.clear();
            catalogGrid.getChildren().clear();

            if (items.isEmpty()) {
                catalogGrid.getChildren().add(createEmptyStateLabel());
                selectedBookTitleId = null;
                clearDetail();
                return;
            }

            catalogGrid.setPrefTileWidth(DesignTokens.CARD_WIDTH);
            catalogGrid.getChildren().setAll(items.stream().map(this::createCatalogCard).toList());
            Long previousSelection = selectedBookTitleId;
            boolean found = previousSelection != null && cardsByBookId.containsKey(previousSelection);
            if (found) {
                for (Map.Entry<Long, StackPane> entry : cardsByBookId.entrySet()) {
                    applySelectionStyle(entry.getValue(), entry.getKey().equals(previousSelection));
                }
            } else if (!items.isEmpty()) {
                selectCard(items.get(0).getBookTitleId());
            }
        } catch (Exception e) {
            statusLabel.setText("Error al cargar el catálogo");
            cardsByBookId.clear();
            catalogGrid.getChildren().setAll(createErrorStateLabel());
            selectedBookTitleId = null;
            clearDetail();
        }
    }

    private StackPane createCatalogCard(BookCatalogItemView item) {
        StackPane card = new StackPane();
        card.setPrefSize(DesignTokens.CARD_WIDTH, DesignTokens.CARD_HEIGHT);
        card.setMinSize(DesignTokens.CARD_WIDTH, DesignTokens.CARD_HEIGHT);
        card.setMaxSize(DesignTokens.CARD_WIDTH, DesignTokens.CARD_HEIGHT);
        card.setStyle("-fx-cursor: hand;" + DesignTokens.bg(DesignTokens.BG_CARD));

        Region cover = new Region();
        cover.setPrefSize(DesignTokens.CARD_WIDTH, DesignTokens.CARD_HEIGHT);
        cover.setMinSize(DesignTokens.CARD_WIDTH, DesignTokens.CARD_HEIGHT);
        cover.setMaxSize(DesignTokens.CARD_WIDTH, DesignTokens.CARD_HEIGHT);
        applyCoverBackground(cover, item.getCoverPath());

        Region gradientOverlay = new Region();
        gradientOverlay.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.75) 0%, rgba(0,0,0,0.35) 40%, transparent 60%, transparent 100%);");
        gradientOverlay.setMouseTransparent(true);

        VBox overlay = new VBox(3);
        overlay.setPadding(new Insets(8));
        overlay.setAlignment(Pos.BOTTOM_LEFT);
        overlay.setPrefWidth(DesignTokens.CARD_WIDTH);

        Label title = new Label(item.getTitle());
        title.setWrapText(true);
        title.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-size: 12px; -fx-font-weight: bold;");
        title.setEffect(new javafx.scene.effect.DropShadow(2, 0, 1, javafx.scene.paint.Color.rgb(0, 0, 0, 0.8)));

        Label author = new Label(item.getAuthor());
        author.setWrapText(true);
        author.setStyle(DesignTokens.textFill(DesignTokens.TEXT_SECONDARY)
                + " -fx-font-size: 10px;");
        author.setEffect(new javafx.scene.effect.DropShadow(2, 0, 1, javafx.scene.paint.Color.rgb(0, 0, 0, 0.8)));

        Label availability = new Label(item.getAvailableCopies() + " disponibles");
        availability.setStyle(DesignTokens.textFill(DesignTokens.ACCENT_WARNING)
                + " -fx-font-size: 10px; -fx-font-weight: bold;");
        availability.setEffect(new javafx.scene.effect.DropShadow(2, 0, 1, javafx.scene.paint.Color.rgb(0, 0, 0, 0.8)));

        overlay.getChildren().addAll(title, author, availability);
        StackPane.setAlignment(overlay, Pos.BOTTOM_LEFT);
        card.getChildren().addAll(cover, gradientOverlay, overlay);
        card.setUserData(item);

        card.setOnMouseEntered(e -> {
            card.setScaleX(1.08);
            card.setScaleY(1.08);
            card.setEffect(new javafx.scene.effect.DropShadow(16, DesignTokens.SHADOW_BLUE));
        });
        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            card.setEffect(null);
            applySelectionStyle(card, item.getBookTitleId().equals(selectedBookTitleId));
        });
        card.setOnMouseClicked(event -> selectCard(item.getBookTitleId()));

        Tooltip.install(card, new Tooltip(item.getTitle() + " - " + item.getAuthor()));
        cardsByBookId.put(item.getBookTitleId(), card);
        applySelectionStyle(card, item.getBookTitleId().equals(selectedBookTitleId));
        return card;
    }

    private void selectCard(Long bookTitleId) {
        selectedBookTitleId = bookTitleId;
        cardsByBookId.forEach((id, card) -> applySelectionStyle(card, id.equals(bookTitleId)));
        loadDetail(bookTitleId);
    }

    private void applySelectionStyle(StackPane card, boolean selected) {
        if (selected) {
            card.setBorder(new Border(new BorderStroke(DesignTokens.BLUE_GLOW, BorderStrokeStyle.SOLID, DesignTokens.RADIUS_CARD, new BorderWidths(3))));
            card.setEffect(new javafx.scene.effect.DropShadow(12, DesignTokens.SHADOW_BLUE));
        } else {
            card.setBorder(null);
            card.setEffect(null);
        }
    }

    private void loadDetail(Long bookTitleId) {
        try {
            currentDetail = catalogController.loadBookDetail(bookTitleId, isAdmin());
            if (currentDetail == null) {
                clearDetail();
                return;
            }
        } catch (Exception e) {
            clearDetail();
            detailTitleLabel.setText("Error al cargar detalle");
            detailDescriptionLabel.setText("No se pudo cargar la información del libro.");
            return;
        }

        BookTitle bookTitle = currentDetail.getBookTitle();
        detailTitleLabel.setText(bookTitle.getTitle());
        detailAuthorLabel.setText(valueOrDash(bookTitle.getAuthor()));
        detailIsbnLabel.setText(valueOrDash(bookTitle.getIsbn()));
        detailPublisherLabel.setText(valueOrDash(bookTitle.getPublisher()));
        detailYearLabel.setText(bookTitle.getYear() > 0 ? String.valueOf(bookTitle.getYear()) : "-");
        detailCategoryLabel.setText(valueOrDash(bookTitle.getCategory()));
        detailCareerLabel.setText(valueOrDash(bookTitle.getCareer()));
        detailAvailabilityLabel.setText(countAvailableCopies(currentDetail.getCopies()) + " ejemplares disponibles");
        detailDescriptionLabel.setText(valueOrEmpty(bookTitle.getDescription()));

        copySelector.setItems(FXCollections.observableArrayList(currentDetail.getCopies()));
        if (!currentDetail.getCopies().isEmpty()) {
            copySelector.getSelectionModel().selectFirst();
            updateSelectedCopyDetail(currentDetail.getCopies().get(0));
        } else {
            detailLocationLabel.setText("No hay ejemplares visibles.");
            detailCopyStatusLabel.setText("-");
            detailNotesLabel.setText("-");
        }
        applyDetailCover(bookTitle.getCoverPath());
    }

    private void clearDetail() {
        currentDetail = null;
        detailTitleLabel.setText("Sin resultados");
        detailAuthorLabel.setText("");
        detailIsbnLabel.setText("");
        detailPublisherLabel.setText("");
        detailYearLabel.setText("");
        detailCategoryLabel.setText("");
        detailCareerLabel.setText("");
        detailAvailabilityLabel.setText("");
        detailDescriptionLabel.setText("No se encontraron libros para los criterios actuales.");
        detailLocationLabel.setText("");
        detailCopyStatusLabel.setText("");
        detailNotesLabel.setText("");
        copySelector.getItems().clear();
        statusSelector.getSelectionModel().clearSelection();
        detailCoverPane.getChildren().setAll(new Label("Sin portada"));
        detailCoverPane.setBackground(defaultCoverBackground());
    }

    private void applyDetailCover(String coverPath) {
        detailCoverPane.getChildren().clear();
        if (coverPath == null || coverPath.isBlank()) {
            Label fallback = new Label("Sin portada");
            fallback.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                    + " -fx-font-size: 16px; -fx-font-weight: bold;");
            detailCoverPane.getChildren().add(fallback);
            detailCoverPane.setBackground(defaultCoverBackground());
            return;
        }

        Path coverFile = CoverResolver.resolve(coverPath);
        if (coverFile == null || !Files.exists(coverFile)) {
            Label fallback = new Label("Portada no disponible");
            fallback.setStyle(DesignTokens.textFill(DesignTokens.TEXT_MUTED)
                    + " -fx-font-size: 14px; -fx-font-weight: bold;");
            detailCoverPane.getChildren().add(fallback);
            detailCoverPane.setBackground(defaultCoverBackground());
            return;
        }

        javafx.scene.image.Image image = new javafx.scene.image.Image(coverFile.toUri().toString(), 480, 680, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true));
        BackgroundFill gradientFill = new BackgroundFill(
                new javafx.scene.paint.LinearGradient(0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                        new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web("#0f4c81")),
                        new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web("#06121f"))),
                CornerRadii.EMPTY, Insets.EMPTY);
        detailCoverPane.setBackground(new Background(List.of(gradientFill), List.of(backgroundImage)));
    }

    private void applyCoverBackground(Region region, String coverPath) {
        if (coverPath == null || coverPath.isBlank()) {
            region.setStyle("-fx-background-color: linear-gradient(to bottom right, #1e3a5f, #0a1929);");
            return;
        }
        Path coverFile = CoverResolver.resolve(coverPath);
        if (coverFile == null || !Files.exists(coverFile)) {
            region.setStyle("-fx-background-color: linear-gradient(to bottom right, #1e3a5f, #0a1929);");
            return;
        }
        javafx.scene.image.Image image = new javafx.scene.image.Image(coverFile.toUri().toString(),
                DesignTokens.CARD_WIDTH * 2, DesignTokens.CARD_HEIGHT * 2, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true));
        region.setBackground(new Background(backgroundImage));
    }

    private Background defaultCoverBackground() {
        return new Background(new BackgroundFill(DesignTokens.BG_CARD, CornerRadii.EMPTY, Insets.EMPTY));
    }

    private void updateSelectedCopyStatus() {
        BookCopy selectedCopy = copySelector.getSelectionModel().getSelectedItem();
        CopyStatus selectedStatus = statusSelector.getSelectionModel().getSelectedItem();
        if (selectedCopy == null || selectedStatus == null) {
            showError("Selecciona un ejemplar y un estado.");
            return;
        }

        bookAdminController.changeCopyStatus(selectedCopy.getId(), selectedStatus);
        loadCatalog();
        if (selectedBookTitleId != null) {
            loadDetail(selectedBookTitleId);
        }
        showFeedback("Estado actualizado correctamente.");
    }

    private void openRegisterDialog() {
        BookAdminView adminView = new BookAdminView(bookAdminController, catalogController.getCareers(), this::loadCatalog);
        adminView.showCreate(stage);
    }

    private void openEditDialog() {
        if (currentDetail == null) {
            showError("Selecciona primero un libro del catalogo.");
            return;
        }

        BookCopy selectedCopy = copySelector.getSelectionModel().getSelectedItem();
        if (selectedCopy == null) {
            showError("Selecciona un ejemplar fisico para editar.");
            return;
        }

        Location location = findLocationById(selectedCopy.getLocationId());
        if (location == null) {
            showError("No se encontro la ubicacion del ejemplar seleccionado.");
            return;
        }

        BookTitle selectedBook = currentDetail.getBookTitle();
        BookAdminView adminView = new BookAdminView(bookAdminController, catalogController.getCareers(), () -> {
            loadCatalog();
            loadDetail(selectedBook.getId());
        });
        adminView.showEdit(stage, selectedBook, selectedCopy, location);
    }

    private boolean isAdmin() {
        return session.getCurrentUser().getRole() == UserRole.ADMIN;
    }

    private void openLoginDialog() {
        LoginView loginView = new LoginView(loginController);
        UserSession adminSession = loginView.showAndWait(stage);
        if (adminSession != null) {
            updateSession(adminSession);
        }
    }

    private void logout() {
        updateSession(loginController.loginAsGuest());
    }

    private void updateSession(UserSession newSession) {
        this.session = newSession;
        roleBadge.setText(translateRole(session.getCurrentUser().getRole()));
        boolean admin = isAdmin();
        loginButton.setVisible(!admin);
        loginButton.setManaged(!admin);
        logoutButton.setVisible(admin);
        logoutButton.setManaged(admin);
        registerButton.setVisible(admin);
        registerButton.setManaged(admin);
        editButton.setVisible(admin);
        editButton.setManaged(admin);
        adminPanel.setVisible(admin);
        adminPanel.setManaged(admin);
        loadCatalog();
    }

    private String formatLocation(Location location) {
        if (location == null) {
            return "No disponible";
        }
        return valueOrDash(location.getCode());
    }

    private void updateSelectedCopyDetail(BookCopy copy) {
        statusSelector.getSelectionModel().select(copy.getStatus());
        detailCopyStatusLabel.setText(translateStatus(copy.getStatus()));
        detailNotesLabel.setText(valueOrDash(copy.getNotes()));
        detailLocationLabel.setText(formatLocation(findLocationById(copy.getLocationId())));
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String valueOrEmpty(String value) {
        return Optional.ofNullable(value).orElse("");
    }

    private long countAvailableCopies(List<BookCopy> copies) {
        return copies.stream().filter(copy -> copy.getStatus() == CopyStatus.AVAILABLE).count();
    }

    private Location findLocationById(Long locationId) {
        if (currentDetail == null || currentDetail.getLocations() == null) {
            return null;
        }
        return currentDetail.getLocations().stream()
                .filter(location -> locationId.equals(location.getId()))
                .findFirst()
                .orElse(null);
    }

    private Label createEmptyStateLabel() {
        Label label = new Label("No se encontraron libros");
        label.setStyle(DesignTokens.textFill(DesignTokens.TEXT_MUTED)
                + " -fx-font-size: 16px; -fx-font-weight: bold;");
        label.setAlignment(Pos.CENTER);
        catalogGrid.setPrefTileWidth(400);
        return label;
    }

    private Label createErrorStateLabel() {
        Label label = new Label("Error al cargar el catálogo");
        label.setStyle(DesignTokens.textFill(DesignTokens.ACCENT_ERROR)
                + " -fx-font-size: 16px; -fx-font-weight: bold;");
        label.setAlignment(Pos.CENTER);
        catalogGrid.setPrefTileWidth(400);
        return label;
    }

    private Label detailFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle(DesignTokens.textFill(DesignTokens.BLUE_LIGHT)
                + " -fx-font-size: 11px; -fx-font-weight: bold;");
        label.setMinWidth(80);
        return label;
    }

    private Label sectionHeading(String text) {
        Label label = new Label(text);
        label.setStyle(DesignTokens.textFill(DesignTokens.TEXT_PRIMARY)
                + " -fx-font-size: 14px; -fx-font-weight: bold;");
        return label;
    }

    private String translateRole(UserRole role) {
        return role == UserRole.ADMIN ? "Admin" : "Invitado";
    }

    private String translateStatus(CopyStatus status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case AVAILABLE -> "Disponible";
            case MISSING -> "Extraviado";
            case REMOVED -> "Retirado";
        };
    }

    private void showFeedback(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.setStyle(DesignTokens.textFill(DesignTokens.ACCENT_SUCCESS) + " -fx-font-size: 12px; -fx-font-weight: bold;");
        feedbackLabel.setVisible(true);
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(3), e -> feedbackLabel.setVisible(false)));
        timer.setCycleCount(1);
        timer.play();
    }

    private void showError(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.setStyle(DesignTokens.textFill(DesignTokens.ACCENT_ERROR) + " -fx-font-size: 12px; -fx-font-weight: bold;");
        feedbackLabel.setVisible(true);
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(4), e -> feedbackLabel.setVisible(false)));
        timer.setCycleCount(1);
        timer.play();
    }
}
