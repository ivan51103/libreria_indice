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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class CatalogView {
    // Vista principal del flujo publico: listar, buscar y consultar detalle.
    private final CatalogController catalogController;
    private final BookAdminController bookAdminController;
    private final UserSession session;
    private final Stage stage;
    private final TextField titleSearchField;
    private final TextField authorSearchField;
    private final TableView<BookCatalogItemView> catalogTable;
    private final TextArea detailArea;
    private final ComboBox<BookCopy> copySelector;
    private final ComboBox<CopyStatus> statusSelector;
    private BookDetailViewModel currentDetail;

    public CatalogView(CatalogController catalogController,
                       BookAdminController bookAdminController,
                       UserSession session) {
        this.catalogController = catalogController;
        this.bookAdminController = bookAdminController;
        this.session = session;
        this.stage = new Stage();
        this.titleSearchField = new TextField();
        this.authorSearchField = new TextField();
        this.catalogTable = new TableView<>();
        this.detailArea = new TextArea();
        this.copySelector = new ComboBox<>();
        this.statusSelector = new ComboBox<>();
        configureStage();
    }

    public void show() {
        loadCatalog();
        stage.show();
    }

    private void configureStage() {
        stage.setTitle("Catalogo de Biblioteca");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.setScene(new Scene(buildRoot(), 1220, 760));
    }

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setTop(buildTopBar());
        root.setCenter(buildCenterPane());
        return root;
    }

    private VBox buildTopBar() {
        Label roleLabel = new Label("Rol activo: " + session.getCurrentUser().getRole());

        titleSearchField.setPromptText("Buscar por titulo");
        authorSearchField.setPromptText("Buscar por autor");

        Button searchButton = new Button("Buscar");
        searchButton.setOnAction(event -> loadCatalog());

        Button clearButton = new Button("Limpiar");
        clearButton.setOnAction(event -> {
            titleSearchField.clear();
            authorSearchField.clear();
            loadCatalog();
        });

        HBox filters = new HBox(10, new Label("Titulo"), titleSearchField, new Label("Autor"), authorSearchField, searchButton, clearButton);
        filters.setAlignment(Pos.CENTER_LEFT);

        HBox topRow = new HBox(16, roleLabel, filters);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox wrapper = new VBox(12, topRow);
        wrapper.setPadding(new Insets(16));
        return wrapper;
    }

    private SplitPane buildCenterPane() {
        configureCatalogTable();
        detailArea.setEditable(false);
        detailArea.setWrapText(true);

        VBox leftPane = new VBox(10, buildCatalogActions(), catalogTable);
        VBox.setVgrow(catalogTable, Priority.ALWAYS);
        leftPane.setPadding(new Insets(0, 16, 16, 16));

        VBox rightPane = new VBox(12, new Label("Detalle del libro"), detailArea, buildAdminPanel());
        rightPane.setPadding(new Insets(0, 16, 16, 0));
        VBox.setVgrow(detailArea, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane(leftPane, rightPane);
        splitPane.setDividerPositions(0.55);
        return splitPane;
    }

    private HBox buildCatalogActions() {
        Button refreshButton = new Button("Actualizar");
        refreshButton.setOnAction(event -> loadCatalog());

        Button registerButton = new Button("Registrar libro");
        registerButton.setVisible(isAdmin());
        registerButton.setManaged(isAdmin());
        registerButton.setOnAction(event -> openRegisterDialog());

        Button editButton = new Button("Editar seleccionado");
        editButton.setVisible(isAdmin());
        editButton.setManaged(isAdmin());
        editButton.setOnAction(event -> openEditDialog());

        HBox actions = new HBox(10, refreshButton, registerButton, editButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        return actions;
    }

    private VBox buildAdminPanel() {
        statusSelector.setItems(FXCollections.observableArrayList(CopyStatus.values()));
        copySelector.setPromptText("Selecciona un ejemplar");
        copySelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                statusSelector.getSelectionModel().select(newValue.getStatus());
            }
        });
        copySelector.setConverter(new StringConverter<>() {
            @Override
            public String toString(BookCopy bookCopy) {
                if (bookCopy == null) {
                    return "";
                }
                return bookCopy.getInventoryCode() + " - " + bookCopy.getStatus();
            }

            @Override
            public BookCopy fromString(String string) {
                return null;
            }
        });

        Button updateStatusButton = new Button("Cambiar estado");
        updateStatusButton.setOnAction(event -> updateSelectedCopyStatus());

        VBox adminBox = new VBox(10,
                new Label("Acciones de administrador"),
                new Label("Ejemplar fisico a modificar"),
                copySelector,
                new Label("Nuevo estado"),
                statusSelector,
                updateStatusButton);
        adminBox.setVisible(isAdmin());
        adminBox.setManaged(isAdmin());
        return adminBox;
    }

    private void configureCatalogTable() {
        // La tabla representa solo el resumen del catalogo; el detalle completo vive al lado derecho.
        TableColumn<BookCatalogItemView, String> titleColumn = new TableColumn<>("Titulo");
        titleColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getTitle()));
        titleColumn.setPrefWidth(260);

        TableColumn<BookCatalogItemView, String> authorColumn = new TableColumn<>("Autor");
        authorColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getAuthor()));
        authorColumn.setPrefWidth(200);

        TableColumn<BookCatalogItemView, String> categoryColumn = new TableColumn<>("Categoria");
        categoryColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getCategory()));
        categoryColumn.setPrefWidth(180);

        TableColumn<BookCatalogItemView, Integer> copiesColumn = new TableColumn<>("Disponibles");
        copiesColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getAvailableCopies()));
        copiesColumn.setPrefWidth(120);

        catalogTable.getColumns().clear();
        catalogTable.getColumns().add(titleColumn);
        catalogTable.getColumns().add(authorColumn);
        catalogTable.getColumns().add(categoryColumn);
        catalogTable.getColumns().add(copiesColumn);
        catalogTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadDetail(newValue.getBookTitleId());
            }
        });
    }

    private void loadCatalog() {
        // La busqueda es opcional: si los campos van vacios, se debe mostrar el catalogo general.
        BookSearchCriteria criteria = new BookSearchCriteria();
        criteria.setText(titleSearchField.getText());
        criteria.setAuthor(authorSearchField.getText());

        PageRequest request = new PageRequest();
        request.setPage(0);
        request.setSize(50);
        request.setSortField(BookSortField.TITLE);
        request.setDirection(SortDirection.ASC);

        PageResult<BookCatalogItemView> result = catalogController.loadCatalog(criteria, request, isAdmin());
        catalogTable.setItems(FXCollections.observableArrayList(result.getItems()));

        if (!result.getItems().isEmpty()) {
            catalogTable.getSelectionModel().selectFirst();
        } else {
            currentDetail = null;
            detailArea.setText("No hay resultados para los criterios seleccionados.");
            copySelector.getItems().clear();
            statusSelector.getSelectionModel().clearSelection();
        }
    }

    private void loadDetail(Long bookTitleId) {
        currentDetail = catalogController.loadBookDetail(bookTitleId, isAdmin());
        if (currentDetail == null) {
            detailArea.setText("No se pudo cargar el detalle.");
            copySelector.getItems().clear();
            return;
        }

        detailArea.setText(buildDetailText(currentDetail));
        copySelector.setItems(FXCollections.observableArrayList(currentDetail.getCopies()));
        if (!currentDetail.getCopies().isEmpty()) {
            copySelector.getSelectionModel().selectFirst();
            statusSelector.getSelectionModel().select(currentDetail.getCopies().get(0).getStatus());
        }
    }

    private String buildDetailText(BookDetailViewModel detail) {
        // Se indexan ubicaciones por id para relacionarlas rapido con cada ejemplar mostrado.
        Map<Long, Location> locationsById = new HashMap<>();
        for (Location location : detail.getLocations()) {
            locationsById.put(location.getId(), location);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Titulo: ").append(detail.getBookTitle().getTitle()).append("\n");
        builder.append("Autor: ").append(detail.getBookTitle().getAuthor()).append("\n");
        builder.append("ISBN: ").append(detail.getBookTitle().getIsbn()).append("\n");
        builder.append("Editorial: ").append(detail.getBookTitle().getPublisher()).append("\n");
        builder.append("Anio: ").append(detail.getBookTitle().getYear()).append("\n");
        builder.append("Categoria: ").append(detail.getBookTitle().getCategory()).append("\n");
        builder.append("Carrera: ").append(detail.getBookTitle().getCareer()).append("\n\n");
        builder.append("Descripcion:\n").append(valueOrEmpty(detail.getBookTitle().getDescription())).append("\n\n");
        builder.append("Ejemplares:\n");

        for (BookCopy copy : detail.getCopies()) {
            builder.append("- Codigo: ").append(copy.getInventoryCode()).append("\n");
            builder.append("  Estado: ").append(copy.getStatus()).append("\n");
            builder.append("  Ubicacion: ").append(formatLocation(locationsById.get(copy.getLocationId()))).append("\n");
            if (copy.getNotes() != null && !copy.getNotes().isBlank()) {
                builder.append("  Notas: ").append(copy.getNotes()).append("\n");
            }
        }
        return builder.toString();
    }

    private void updateSelectedCopyStatus() {
        // Aqui "Ejemplar" significa una copia fisica concreta del libro seleccionado.
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
        BookAdminView adminView = new BookAdminView(bookAdminController, this::loadCatalog);
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
        BookAdminView adminView = new BookAdminView(bookAdminController, () -> {
            loadCatalog();
            loadDetail(selectedBook.getId());
        });
        adminView.showEdit(stage, selectedBook, selectedCopy, location);
    }

    private boolean isAdmin() {
        return session.getCurrentUser().getRole() == UserRole.ADMIN;
    }

    private String formatLocation(Location location) {
        if (location == null) {
            return "No disponible";
        }
        return "%s / %s / estante %s / nivel %s / posicion %s (%s)"
                .formatted(location.getRoom(),
                        location.getSection(),
                        location.getShelf(),
                        valueOrDash(location.getLevel()),
                        valueOrDash(location.getPosition()),
                        location.getCode());
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String valueOrEmpty(String value) {
        return Optional.ofNullable(value).orElse("");
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
