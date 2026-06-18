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
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
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
    private final Label roleLabel;
    private final TilePane catalogGrid;
    private final ScrollPane catalogScrollPane;
    private final Label statusLabel;
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
        this.roleLabel = new Label("Rol activo: " + session.getCurrentUser().getRole());
        this.catalogGrid = new TilePane();
        this.catalogScrollPane = new ScrollPane(catalogGrid);
        this.statusLabel = new Label();
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
    }

    public void show() {
        loadCareers();
        loadCatalog();
        stage.show();
    }

    private void configureStage() {
        stage.setTitle("Catalogo de Biblioteca");
        stage.setMinWidth(1280);
        stage.setMinHeight(780);
        stage.setScene(new Scene(buildRoot(), 1340, 820));
    }

    private VBox buildRoot() {
        VBox root = new VBox(14, buildTopBar(), buildContent());
        root.setPadding(new Insets(16));
        return root;
    }

    private Node buildTopBar() {
        Label titleLabel = new Label("Indice Digital de Biblioteca");
        titleLabel.setFont(Font.font(22));

        roleLabel.setStyle("-fx-text-fill: #d4d4d8;");

        titleSearchField.setPromptText("Buscar por titulo, autor, categoria o carrera");
        titleSearchField.setPrefColumnCount(30);

        searchButton.setOnAction(event -> loadCatalog());
        clearButton.setOnAction(event -> {
            titleSearchField.clear();
            resetFilters();
            loadCatalog();
        });

        titleSearchField.setOnAction(event -> loadCatalog());

        HBox searchRow = new HBox(10,
                new Label("Buscar"), titleSearchField,
                searchButton, clearButton);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        loginButton.setVisible(!isAdmin());
        loginButton.setManaged(!isAdmin());
        loginButton.setOnAction(event -> openLoginDialog());

        logoutButton.setVisible(isAdmin());
        logoutButton.setManaged(isAdmin());
        logoutButton.setOnAction(event -> logout());

        registerButton.setVisible(isAdmin());
        registerButton.setManaged(isAdmin());
        registerButton.setOnAction(event -> openRegisterDialog());

        editButton.setVisible(isAdmin());
        editButton.setManaged(isAdmin());
        editButton.setOnAction(event -> openEditDialog());

        HBox actions = new HBox(10, loginButton, logoutButton, registerButton, editButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox wrapper = new VBox(10, titleLabel, roleLabel, searchRow, actions, statusLabel);
        wrapper.setPadding(new Insets(8, 4, 0, 4));
        return wrapper;
    }

    private Node buildContent() {
        catalogGrid.setHgap(16);
        catalogGrid.setVgap(18);
        catalogGrid.setPadding(new Insets(12));
        catalogGrid.setPrefTileWidth(170);
        catalogGrid.setPrefTileHeight(280);
        catalogGrid.setTileAlignment(Pos.TOP_CENTER);
        catalogGrid.setPrefColumns(5);

        catalogScrollPane.setFitToWidth(true);
        catalogScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        catalogScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox filterPane = buildFilterPane();

        VBox leftPane = new VBox(10, new Label("Catalogo"), catalogScrollPane);
        VBox.setVgrow(catalogScrollPane, javafx.scene.layout.Priority.ALWAYS);
        leftPane.setPrefWidth(680);

        ScrollPane rightPane = buildDetailPane();
        rightPane.setPrefWidth(380);
        rightPane.setMinWidth(380);
        rightPane.setMaxWidth(380);

        SplitPane splitPane = new SplitPane(filterPane, leftPane, rightPane);
        splitPane.setDividerPositions(0.20, 0.60);
        return splitPane;
    }

    private VBox buildFilterPane() {
        Label title = new Label("Filtros");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox sortSection = buildSortSection();
        VBox alphabetSection = buildAlphabetSection();
        VBox careerSection = buildCareerSection();

        filterPanel.getChildren().setAll(title, sortSection, alphabetSection, careerSection);
        filterPanel.setSpacing(14);
        filterPanel.setPadding(new Insets(16));
        filterPanel.setPrefWidth(220);
        filterPanel.setMinWidth(220);
        filterPanel.setMaxWidth(220);
        filterPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #0b2f57, #071827); -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #1d4ed8;");
        return filterPanel;
    }

    private VBox buildSortSection() {
        Label heading = new Label("Ordenar por");
        heading.setStyle("-fx-text-fill: #bfdbfe; -fx-font-size: 13px; -fx-font-weight: bold;");

        sortAZ.setToggleGroup(sortGroup);
        sortZA.setToggleGroup(sortGroup);
        sortAZ.setSelected(true);
        sortAZ.setStyle("-fx-text-fill: white;");
        sortZA.setStyle("-fx-text-fill: white;");
        sortAZ.setOnAction(event -> loadCatalog());
        sortZA.setOnAction(event -> loadCatalog());

        VBox section = new VBox(6, heading, sortAZ, sortZA);
        section.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8; -fx-padding: 12;");
        return section;
    }

    private VBox buildAlphabetSection() {
        Label heading = new Label("Primera letra");
        heading.setStyle("-fx-text-fill: #bfdbfe; -fx-font-size: 13px; -fx-font-weight: bold;");

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
        alphabetPane.setPrefWrapLength(200);
        alphabetPane.getChildren().add(todasButton);

        for (char c = 'A'; c <= 'Z'; c++) {
            String letter = String.valueOf(c);
            Button btn = new Button(letter);
            btn.setPrefSize(34, 28);
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

        VBox section = new VBox(6, heading, alphabetPane);
        section.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8; -fx-padding: 12;");
        return section;
    }

    private final List<Button> alphabetButtons = new java.util.ArrayList<>();

    private String alphabetButtonStyle(boolean selected) {
        if (selected) {
            return "-fx-background-color: #1d4ed8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-border-color: #60a5fa; -fx-border-radius: 6; -fx-cursor: hand;";
        }
        return "-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-background-radius: 6; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 6; -fx-cursor: hand;";
    }

    private VBox buildCareerSection() {
        Label heading = new Label("Carrera");
        heading.setStyle("-fx-text-fill: #bfdbfe; -fx-font-size: 13px; -fx-font-weight: bold;");

        careerList.setSpacing(6);
        ScrollPane scroll = new ScrollPane(careerList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(200);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox section = new VBox(6, heading, scroll);
        section.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8; -fx-padding: 12;");
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
        detailCoverPane.setPrefSize(240, 320);
        detailCoverPane.setMinSize(240, 320);
        detailCoverPane.setMaxSize(240, 320);
        detailCoverPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f4c81, #06121f); -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: rgba(255,255,255,0.18);");

        Label coverFallback = new Label("Sin portada");
        coverFallback.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        detailCoverPane.getChildren().add(coverFallback);

        detailTitleLabel.setFont(Font.font("Georgia", 26));
        detailTitleLabel.setWrapText(true);
        detailTitleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        detailAuthorLabel.setStyle("-fx-text-fill: #dbeafe;");
        detailIsbnLabel.setStyle("-fx-text-fill: white;");
        detailPublisherLabel.setStyle("-fx-text-fill: white;");
        detailYearLabel.setStyle("-fx-text-fill: white;");
        detailCategoryLabel.setStyle("-fx-text-fill: white;");
        detailCareerLabel.setStyle("-fx-text-fill: white;");
        detailDescriptionLabel.setWrapText(true);
        detailDescriptionLabel.setStyle("-fx-text-fill: #eff6ff; -fx-font-size: 14px; -fx-line-spacing: 4px;");
        detailAvailabilityLabel.setStyle("-fx-text-fill: #bfdbfe; -fx-font-weight: bold;");
        detailLocationLabel.setWrapText(true);
        detailLocationLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        detailCopyStatusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        detailNotesLabel.setWrapText(true);
        detailNotesLabel.setStyle("-fx-text-fill: #dbeafe;");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(14);
        infoGrid.setVgap(9);
        infoGrid.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8; -fx-padding: 14;");
        infoGrid.add(detailFieldLabel("Autor"), 0, 0);
        infoGrid.add(detailAuthorLabel, 1, 0);
        infoGrid.add(detailFieldLabel("ISBN"), 0, 1);
        infoGrid.add(detailIsbnLabel, 1, 1);
        infoGrid.add(detailFieldLabel("Editorial"), 0, 2);
        infoGrid.add(detailPublisherLabel, 1, 2);
        infoGrid.add(detailFieldLabel("Anio"), 0, 3);
        infoGrid.add(detailYearLabel, 1, 3);
        infoGrid.add(detailFieldLabel("Categoria"), 0, 4);
        infoGrid.add(detailCategoryLabel, 1, 4);
        infoGrid.add(detailFieldLabel("Carrera"), 0, 5);
        infoGrid.add(detailCareerLabel, 1, 5);

        copySelector.setPromptText("Selecciona un ejemplar");
        copySelector.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(BookCopy copy) {
                if (copy == null) {
                    return "";
                }
                return copy.getInventoryCode() + " - " + translateStatus(copy.getStatus());
            }

            @Override
            public BookCopy fromString(String string) {
                return null;
            }
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
            if (newValue != null) {
                updateSelectedCopyDetail(newValue);
            }
        });

        statusSelector.getItems().setAll(CopyStatus.values());
        statusSelector.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(CopyStatus status) {
                return status == null ? "" : translateStatus(status);
            }

            @Override
            public CopyStatus fromString(String string) {
                return null;
            }
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

        Button updateStatusButton = new Button("Cambiar estado");
        updateStatusButton.setOnAction(event -> updateSelectedCopyStatus());

        Label adminTitle = new Label("Acciones de administrador");
        adminTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label statusEditLabel = new Label("Nuevo estado");
        statusEditLabel.setStyle("-fx-text-fill: #bfdbfe;");

        adminPanel.getChildren().setAll(
                new Separator(),
                adminTitle,
                statusEditLabel,
                statusSelector,
                updateStatusButton);
        adminPanel.setSpacing(10);
        adminPanel.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8; -fx-padding: 12;");
        adminPanel.setVisible(isAdmin());
        adminPanel.setManaged(isAdmin());

        VBox coverBlock = new VBox(10, detailCoverPane, detailAvailabilityLabel);
        coverBlock.setAlignment(Pos.TOP_CENTER);

        VBox descriptionBlock = new VBox(8, sectionHeading("Descripcion"), detailDescriptionLabel);
        descriptionBlock.setStyle("-fx-background-color: rgba(2,132,199,0.28); -fx-background-radius: 8; -fx-padding: 14;");

        GridPane copyGrid = new GridPane();
        copyGrid.setHgap(14);
        copyGrid.setVgap(9);
        copyGrid.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8; -fx-padding: 14;");
        copyGrid.add(detailFieldLabel("Ejemplar"), 0, 0);
        copyGrid.add(copySelector, 1, 0);
        copyGrid.add(detailFieldLabel("Estado"), 0, 1);
        copyGrid.add(detailCopyStatusLabel, 1, 1);
        copyGrid.add(detailFieldLabel("Notas"), 0, 2);
        copyGrid.add(detailNotesLabel, 1, 2);

        Label locationHeading = new Label("Codigo de ubicacion");
        locationHeading.setStyle("-fx-text-fill: #075985; -fx-font-size: 12px; -fx-font-weight: bold;");
        VBox locationBlock = new VBox(6, locationHeading, detailLocationLabel);
        locationBlock.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #dbeafe); -fx-background-radius: 8; -fx-padding: 14; -fx-border-color: #7dd3fc; -fx-border-radius: 8;");
        detailLocationLabel.setStyle("-fx-text-fill: #082f49; -fx-font-size: 22px; -fx-font-weight: bold;");

        VBox detailBox = new VBox(14,
                coverBlock,
                detailTitleLabel,
                infoGrid,
                descriptionBlock,
                copyGrid,
                locationBlock,
                adminPanel);
        detailBox.setPadding(new Insets(18));
        detailBox.setStyle("-fx-background-color: #071827; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #bfdbfe;");

        ScrollPane detailScroll = new ScrollPane(detailBox);
        detailScroll.setFitToWidth(true);
        detailScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        detailScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
        return detailScroll;
    }

    private void loadCatalog() {
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
            selectedBookTitleId = null;
            clearDetail();
            return;
        }

        catalogGrid.getChildren().setAll(items.stream().map(this::createCatalogCard).toList());
        selectCard(items.get(0).getBookTitleId());
    }

    private StackPane createCatalogCard(BookCatalogItemView item) {
        StackPane card = new StackPane();
        card.setPrefSize(170, 280);
        card.setMinSize(170, 280);
        card.setMaxSize(170, 280);
        card.setStyle("-fx-background-radius: 16; -fx-border-radius: 16; -fx-cursor: hand; -fx-background-color: #111827;");

        Region cover = new Region();
        cover.setPrefSize(170, 280);
        cover.setMinSize(170, 280);
        cover.setMaxSize(170, 280);
        applyCoverBackground(cover, item.getCoverPath());

        VBox overlay = new VBox(4);
        overlay.setPadding(new Insets(10));
        overlay.setAlignment(Pos.BOTTOM_LEFT);
        overlay.setPrefWidth(170);
        overlay.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.82), rgba(0,0,0,0.12)); -fx-background-radius: 0 0 16 16;");

        Label title = new Label(item.getTitle());
        title.setWrapText(true);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label author = new Label(item.getAuthor());
        author.setWrapText(true);
        author.setStyle("-fx-text-fill: #e5e7eb; -fx-font-size: 11px;");

        Label availability = new Label(item.getAvailableCopies() + " disponibles");
        availability.setStyle("-fx-text-fill: #fde68a; -fx-font-size: 11px; -fx-font-weight: bold;");

        overlay.getChildren().addAll(title, author, availability);
        card.getChildren().addAll(cover, overlay);
        card.setOnMouseClicked(event -> selectCard(item.getBookTitleId()));
        card.setUserData(item);
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
            card.setBorder(new Border(new BorderStroke(Color.web("#f59e0b"), BorderStrokeStyle.SOLID, new CornerRadii(16), new BorderWidths(3))));
        } else {
            card.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(16), new BorderWidths(1))));
        }
    }

    private void loadDetail(Long bookTitleId) {
        currentDetail = catalogController.loadBookDetail(bookTitleId, isAdmin());
        if (currentDetail == null) {
            clearDetail();
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
            fallback.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            detailCoverPane.getChildren().add(fallback);
            detailCoverPane.setBackground(defaultCoverBackground());
            return;
        }

        File file = new File(coverPath);
        if (!file.exists()) {
            Label fallback = new Label("Portada no disponible");
            fallback.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
            detailCoverPane.getChildren().add(fallback);
            detailCoverPane.setBackground(defaultCoverBackground());
            return;
        }

        javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString(), 220, 300, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true));
        detailCoverPane.setBackground(new Background(backgroundImage));
    }

    private void applyCoverBackground(Region region, String coverPath) {
        if (coverPath == null || coverPath.isBlank()) {
            region.setStyle("-fx-background-color: linear-gradient(to bottom right, #334155, #0f172a); -fx-background-radius: 16;");
            return;
        }
        File file = new File(coverPath);
        if (!file.exists()) {
            region.setStyle("-fx-background-color: linear-gradient(to bottom right, #334155, #0f172a); -fx-background-radius: 16;");
            return;
        }
        javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString(), 170, 280, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true));
        region.setBackground(new Background(backgroundImage));
    }

    private Background defaultCoverBackground() {
        return new Background(new BackgroundFill(Color.web("#111827"), new CornerRadii(14), Insets.EMPTY));
    }

    private void updateSelectedCopyStatus() {
        BookCopy selectedCopy = copySelector.getSelectionModel().getSelectedItem();
        CopyStatus selectedStatus = statusSelector.getSelectionModel().getSelectedItem();
        if (selectedCopy == null || selectedStatus == null) {
            showInfo("Selecciona un ejemplar y un estado.");
            return;
        }

        bookAdminController.changeCopyStatus(selectedCopy.getId(), selectedStatus);
        loadDetail(selectedCopy.getBookTitleId());
        loadCatalog();
        showInfo("Estado actualizado correctamente.");
    }

    private void openRegisterDialog() {
        BookAdminView adminView = new BookAdminView(bookAdminController, catalogController.getCareers(), this::loadCatalog);
        adminView.showCreate(stage);
    }

    private void openEditDialog() {
        if (currentDetail == null) {
            showInfo("Selecciona primero un libro del catalogo.");
            return;
        }

        BookCopy selectedCopy = copySelector.getSelectionModel().getSelectedItem();
        if (selectedCopy == null) {
            showInfo("Selecciona un ejemplar fisico para editar.");
            return;
        }

        Location location = findLocationById(selectedCopy.getLocationId());
        if (location == null) {
            showInfo("No se encontro la ubicacion del ejemplar seleccionado.");
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
        roleLabel.setText("Rol activo: " + session.getCurrentUser().getRole());
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

    private Label detailFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #bfdbfe; -fx-font-size: 12px; -fx-font-weight: bold;");
        label.setMinWidth(88);
        return label;
    }

    private Label sectionHeading(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        return label;
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
