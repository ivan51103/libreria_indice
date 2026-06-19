package com.biblioteca.ui.view;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.service.BusinessException;
import com.biblioteca.ui.controller.BookAdminController;
import com.biblioteca.ui.style.DesignTokens;
import com.biblioteca.util.CoverResolver;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class BookAdminView {
    private final BookAdminController bookAdminController;
    private final List<String> careers;
    private final Runnable onSaved;
    private final Map<String, String> abbreviatedCareers;

    private static final Map<String, String> CAREER_ABBREVIATIONS = Map.ofEntries(
        Map.entry("LICENCIATURA EN ADMINISTRACIÓN Y GESTIÓN DE NEGOCIOS EMPRENDEDORES", "ADMINISTRACIÓN"),
        Map.entry("LICENCIATURA EN INGENIERÍA EN ADMINISTRACIÓN INDUSTRIAL", "INGENIERÍA"),
        Map.entry("LICENCIATURA EN MERCADOTECNIA ESTRATÉGICA", "MERCADOTECNIA"),
        Map.entry("LICENCIATURA EN DISEÑO GRÁFICO", "DISEÑO"),
        Map.entry("LICENCIATURA EN LENGUAS EXTRANJERAS", "LENGUAS"),
        Map.entry("LICENCIATURA EN SISTEMAS COMPUTACIONALES", "SISTEMAS"),
        Map.entry("LICENCIATURA EN COMUNICACIÓN", "COMUNICACIÓN")
    );

    public BookAdminView(BookAdminController bookAdminController, List<String> careers, Runnable onSaved) {
        this.bookAdminController = bookAdminController;
        this.careers = careers;
        this.onSaved = onSaved;
        this.abbreviatedCareers = new HashMap<>();
        for (String career : careers) {
            abbreviatedCareers.put(CAREER_ABBREVIATIONS.getOrDefault(career, career), career);
        }
    }

    public void showCreate(Stage owner) {
        showForm(owner, new BookTitle(), new BookCopy(), new Location(), true);
    }

    public void showEdit(Stage owner, BookTitle bookTitle, BookCopy bookCopy, Location location) {
        showForm(owner, copyBookTitle(bookTitle), copyBookCopy(bookCopy), copyLocation(location), false);
    }

    private void showForm(Stage owner, BookTitle bookTitle, BookCopy bookCopy, Location location, boolean createMode) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(createMode ? "Registrar libro y ejemplar" : "Editar libro y ejemplar");
        dialog.setMinWidth(680);
        dialog.setMinHeight(680);
        dialog.setResizable(true);

        TextField titleField = styledField(value(bookTitle.getTitle()));
        TextField authorField = styledField(value(bookTitle.getAuthor()));
        TextField isbnField = styledField(value(bookTitle.getIsbn()));
        TextField publisherField = styledField(value(bookTitle.getPublisher()));
        TextField yearField = styledField(bookTitle.getYear() > 0 ? String.valueOf(bookTitle.getYear()) : "");
        TextField categoryField = styledField(value(bookTitle.getCategory()));
        ComboBox<String> careerField = new ComboBox<>();
        careerField.getItems().setAll(abbreviatedCareers.keySet());
        careerField.setEditable(false);
        careerField.setStyle(styledComboStyle());
        if (bookTitle.getCareer() != null) {
            String abbrev = CAREER_ABBREVIATIONS.getOrDefault(bookTitle.getCareer(), bookTitle.getCareer());
            careerField.getSelectionModel().select(abbrev);
        }
        TextArea descriptionArea = new TextArea(value(bookTitle.getDescription()));
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setWrapText(true);
        descriptionArea.setStyle(styledFieldStyle());

        TextField inventoryCodeField = styledField(value(bookCopy.getInventoryCode()));
        ComboBox<CopyStatus> statusBox = new ComboBox<>();
        statusBox.getItems().setAll(CopyStatus.values());
        statusBox.getSelectionModel().select(bookCopy.getStatus() != null ? bookCopy.getStatus() : CopyStatus.AVAILABLE);
        statusBox.setStyle(styledComboStyle());
        statusBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CopyStatus status) {
                return status == null ? "" : translateStatus(status);
            }
            @Override
            public CopyStatus fromString(String string) { return null; }
        });
        statusBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(CopyStatus status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty || status == null ? null : translateStatus(status));
            }
        });
        statusBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CopyStatus status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty || status == null ? null : translateStatus(status));
            }
        });
        TextField notesField = styledField(value(bookCopy.getNotes()));

        TextField roomField = styledField(value(location.getRoom()));
        TextField sectionField = styledField(value(location.getSection()));
        TextField shelfField = styledField(value(location.getShelf()));
        TextField levelField = styledField(value(location.getLevel()));
        TextField codeField = styledField(value(location.getCode()));

        ImageView coverPreview = new ImageView();
        coverPreview.setFitWidth(120);
        coverPreview.setFitHeight(170);
        coverPreview.setPreserveRatio(true);
        coverPreview.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0, 0, 2);");

        Label coverSelectionLabel = new Label(describeCoverPath(bookTitle.getCoverPath()));
        coverSelectionLabel.setWrapText(true);
        coverSelectionLabel.setStyle(DesignTokens.textFill(DesignTokens.TEXT_SECONDARY));
        Tooltip.install(coverSelectionLabel, new Tooltip("Ruta final de la portada guardada en disco"));
        Path[] selectedCoverFile = new Path[1];

        Button selectCoverButton = new Button("Seleccionar imagen");
        selectCoverButton.setStyle("-fx-background-color: " + DesignTokens.toRgba(DesignTokens.BLUE_PRIMARY)
                + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;"
                + " -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 14;");
        selectCoverButton.setOnAction(event -> {
            Path selectedFile = chooseCoverFile(owner);
            if (selectedFile != null) {
                selectedCoverFile[0] = selectedFile;
                coverSelectionLabel.setText(selectedFile.getFileName().toString());
                try {
                    Image img = new Image(selectedFile.toUri().toString(), 240, 340, true, true);
                    coverPreview.setImage(img);
                } catch (Exception ex) {
                    coverPreview.setImage(null);
                }
            }
        });

        if (bookTitle.getCoverPath() != null && !bookTitle.getCoverPath().isBlank()) {
            Path coverFile = CoverResolver.resolve(bookTitle.getCoverPath());
            if (coverFile != null && Files.exists(coverFile)) {
                try {
                    Image img = new Image(coverFile.toUri().toString(), 240, 340, true, true);
                    coverPreview.setImage(img);
                } catch (Exception ex) {
                    // ignore
                }
            }
        }

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(14);
        grid.setVgap(10);
        grid.setStyle(DesignTokens.bg(DesignTokens.BG_PRIMARY));

        int row = 0;
        grid.add(sectionLabel("Titulo"), 0, row);
        grid.add(titleField, 1, row++);
        grid.add(sectionLabel("Autor"), 0, row);
        grid.add(authorField, 1, row++);
        grid.add(sectionLabel("ISBN"), 0, row);
        grid.add(isbnField, 1, row++);
        grid.add(sectionLabel("Editorial"), 0, row);
        grid.add(publisherField, 1, row++);
        grid.add(sectionLabel("Año"), 0, row);
        grid.add(yearField, 1, row++);
        grid.add(sectionLabel("Categoria"), 0, row);
        grid.add(categoryField, 1, row++);
        grid.add(sectionLabel("Carrera"), 0, row);
        grid.add(careerField, 1, row++);
        grid.add(sectionLabel("Descripcion"), 0, row);
        grid.add(descriptionArea, 1, row++);
        grid.add(sectionLabel("Portada"), 0, row);
        grid.add(new VBox(8, selectCoverButton, coverPreview, coverSelectionLabel), 1, row++);
        grid.add(sectionLabel("Codigo de inventario"), 0, row);
        grid.add(inventoryCodeField, 1, row++);
        grid.add(sectionLabel("Estado"), 0, row);
        grid.add(statusBox, 1, row++);
        grid.add(sectionLabel("Notas"), 0, row);
        grid.add(notesField, 1, row++);
        grid.add(sectionLabel("Ubicación"), 0, row);
        grid.add(codeField, 1, row++);

        Button saveButton = new Button(createMode ? "Guardar" : "Actualizar");
        saveButton.setStyle("-fx-background-color: " + DesignTokens.toRgba(DesignTokens.BLUE_PRIMARY)
                + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"
                + " -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 28;");
        saveButton.setOnMouseEntered(e -> saveButton.setStyle(
                "-fx-background-color: " + DesignTokens.toRgba(DesignTokens.BLUE_HOVER)
                + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"
                + " -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 28;"));
        saveButton.setOnMouseExited(e -> saveButton.setStyle(
                "-fx-background-color: " + DesignTokens.toRgba(DesignTokens.BLUE_PRIMARY)
                + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"
                + " -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 28;"));
        saveButton.setOnAction(event -> {
            try {
                fillBookTitle(bookTitle, titleField, authorField, isbnField, publisherField, yearField, categoryField, careerField, descriptionArea);
                if (selectedCoverFile[0] != null) {
                    bookTitle.setCoverPath(bookAdminController.storeCoverImage(selectedCoverFile[0], bookTitle.getIsbn()));
                }
                fillBookCopy(bookCopy, inventoryCodeField, statusBox, notesField);
                fillLocation(location, roomField, sectionField, shelfField, levelField, codeField);
                if (createMode) {
                    bookAdminController.registerBook(bookTitle, bookCopy, location);
                } else {
                    bookAdminController.updateBookEntry(bookTitle, bookCopy, location);
                }
                onSaved.run();
                dialog.close();
            } catch (BusinessException | IllegalArgumentException exception) {
                showError(dialog, exception.getMessage());
            }
        });

        Button cancelButton = new Button("Cancelar");
        cancelButton.setStyle("-fx-background-color: rgba(255,255,255,0.10); -fx-text-fill: white; -fx-font-size: 14px;"
                + " -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 28;");
        cancelButton.setOnAction(event -> dialog.close());

        HBox actions = new HBox(10, saveButton, cancelButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox formCard = new VBox(14, grid, actions);
        formCard.setPadding(new Insets(16));
        formCard.setStyle(DesignTokens.bg(DesignTokens.BG_SECONDARY)
                + " -fx-background-radius: 12;");

        VBox rootContainer = new VBox(formCard);
        rootContainer.setPadding(new Insets(16));
        rootContainer.setStyle(DesignTokens.bg(DesignTokens.BG_PRIMARY));

        ScrollPane scrollPane = new ScrollPane(rootContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Scene dialogScene = new Scene(scrollPane, 700, 720);
        dialogScene.getStylesheets().add(getClass().getResource("/scrollbar-style.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.centerOnScreen();
        dialog.showAndWait();
    }

    private Path chooseCoverFile(Stage owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen de portada");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagenes", "*.jpg", "*.jpeg", "*.png"));
        java.io.File file = chooser.showOpenDialog(owner);
        return file == null ? null : file.toPath();
    }

    private void fillBookTitle(BookTitle bookTitle,
                               TextField titleField,
                               TextField authorField,
                               TextField isbnField,
                               TextField publisherField,
                               TextField yearField,
                               TextField categoryField,
                               ComboBox<String> careerField,
                               TextArea descriptionArea) {
        bookTitle.setTitle(titleField.getText());
        bookTitle.setAuthor(authorField.getText());
        bookTitle.setIsbn(normalizeOptional(isbnField.getText()));
        bookTitle.setPublisher(normalizeOptional(publisherField.getText()));
        bookTitle.setYear(parseYear(yearField.getText()));
        bookTitle.setCategory(normalizeOptional(categoryField.getText()));
        String selectedAbbrev = careerField.getValue();
        bookTitle.setCareer(normalizeOptional(abbreviatedCareers.getOrDefault(selectedAbbrev, selectedAbbrev)));
        bookTitle.setDescription(normalizeOptional(descriptionArea.getText()));
    }

    private void fillBookCopy(BookCopy bookCopy,
                              TextField inventoryCodeField,
                              ComboBox<CopyStatus> statusBox,
                              TextField notesField) {
        bookCopy.setInventoryCode(normalizeOptional(inventoryCodeField.getText()));
        bookCopy.setStatus(statusBox.getValue());
        bookCopy.setNotes(normalizeOptional(notesField.getText()));
    }

    private void fillLocation(Location location,
                              TextField roomField,
                              TextField sectionField,
                              TextField shelfField,
                              TextField levelField,
                              TextField codeField) {
        location.setRoom(normalizeOptional(roomField.getText()));
        location.setSection(normalizeOptional(sectionField.getText()));
        location.setShelf(normalizeOptional(shelfField.getText()));
        location.setLevel(normalizeOptional(levelField.getText()));
        location.setCode(normalizeOptional(codeField.getText()));
    }

    private int parseYear(String yearText) {
        if (yearText == null || yearText.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(yearText.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El año debe ser numérico.");
        }
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private String describeCoverPath(String coverPath) {
        if (coverPath == null || coverPath.isBlank()) {
            return "Sin portada seleccionada";
        }
        try {
            return Path.of(coverPath).getFileName().toString();
        } catch (Exception exception) {
            return coverPath;
        }
    }

    private TextField styledField(String text) {
        TextField field = new TextField(text);
        field.setStyle(styledFieldStyle());
        return field;
    }

    private String styledFieldStyle() {
        return "-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8;"
                + " -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: rgba(255,255,255,0.10);"
                + " -fx-padding: 8 12; -fx-font-size: 13px;";
    }

    private String styledComboStyle() {
        return "-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white;"
                + " -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: rgba(255,255,255,0.10);"
                + " -fx-padding: 4 8;";
    }

    private String catalogButtonStyle() {
        return "-fx-background-color: " + DesignTokens.toRgba(DesignTokens.BG_ELEVATED)
                + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;"
                + " -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 14;";
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle(DesignTokens.textFill(DesignTokens.BLUE_LIGHT)
                + " -fx-font-size: 12px; -fx-font-weight: bold;");
        label.setMinWidth(170);
        return label;
    }

    private String translateStatus(CopyStatus status) {
        return switch (status) {
            case AVAILABLE -> "Disponible";
            case MISSING -> "Extraviado";
            case REMOVED -> "Retirado";
        };
    }

    private BookTitle copyBookTitle(BookTitle source) {
        BookTitle copy = new BookTitle();
        copy.setId(source.getId());
        copy.setTitle(source.getTitle());
        copy.setAuthor(source.getAuthor());
        copy.setIsbn(source.getIsbn());
        copy.setPublisher(source.getPublisher());
        copy.setYear(source.getYear());
        copy.setCategory(source.getCategory());
        copy.setCareer(source.getCareer());
        copy.setDescription(source.getDescription());
        copy.setCoverPath(source.getCoverPath());
        return copy;
    }

    private BookCopy copyBookCopy(BookCopy source) {
        BookCopy copy = new BookCopy();
        copy.setId(source.getId());
        copy.setBookTitleId(source.getBookTitleId());
        copy.setInventoryCode(source.getInventoryCode());
        copy.setLocationId(source.getLocationId());
        copy.setStatus(source.getStatus());
        copy.setNotes(source.getNotes());
        return copy;
    }

    private Location copyLocation(Location source) {
        Location copy = new Location();
        copy.setId(source.getId());
        copy.setRoom(source.getRoom());
        copy.setSection(source.getSection());
        copy.setShelf(source.getShelf());
        copy.setLevel(source.getLevel());
        copy.setPosition(source.getPosition());
        copy.setCode(source.getCode());
        return copy;
    }

    private void showError(Stage owner, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(owner);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
