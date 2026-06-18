package com.biblioteca.ui.view;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.service.BusinessException;
import com.biblioteca.ui.controller.BookAdminController;
import java.nio.file.Path;
import java.util.List;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class BookAdminView {
    // Dialogo reutilizable para registrar o editar una obra y uno de sus ejemplares.
    private final BookAdminController bookAdminController;
    private final List<String> careers;
    private final Runnable onSaved;

    public BookAdminView(BookAdminController bookAdminController, List<String> careers, Runnable onSaved) {
        this.bookAdminController = bookAdminController;
        this.careers = careers;
        this.onSaved = onSaved;
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

        TextField titleField = new TextField(value(bookTitle.getTitle()));
        TextField authorField = new TextField(value(bookTitle.getAuthor()));
        TextField isbnField = new TextField(value(bookTitle.getIsbn()));
        TextField publisherField = new TextField(value(bookTitle.getPublisher()));
        TextField yearField = new TextField(bookTitle.getYear() > 0 ? String.valueOf(bookTitle.getYear()) : "");
        TextField categoryField = new TextField(value(bookTitle.getCategory()));
        ComboBox<String> careerField = new ComboBox<>();
        careerField.getItems().setAll(careers);
        careerField.setEditable(false);
        if (bookTitle.getCareer() != null) {
            careerField.getSelectionModel().select(bookTitle.getCareer());
        }
        TextArea descriptionArea = new TextArea(value(bookTitle.getDescription()));
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setWrapText(true);

        TextField inventoryCodeField = new TextField(value(bookCopy.getInventoryCode()));
        ComboBox<CopyStatus> statusBox = new ComboBox<>();
        statusBox.getItems().setAll(CopyStatus.values());
        statusBox.getSelectionModel().select(bookCopy.getStatus() != null ? bookCopy.getStatus() : CopyStatus.AVAILABLE);
        statusBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CopyStatus status) {
                return status == null ? "" : translateStatus(status);
            }

            @Override
            public CopyStatus fromString(String string) {
                return null;
            }
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
        TextField notesField = new TextField(value(bookCopy.getNotes()));

        TextField roomField = new TextField(value(location.getRoom()));
        TextField sectionField = new TextField(value(location.getSection()));
        TextField shelfField = new TextField(value(location.getShelf()));
        TextField levelField = new TextField(value(location.getLevel()));
        TextField codeField = new TextField(value(location.getCode()));

        Label coverSelectionLabel = new Label(describeCoverPath(bookTitle.getCoverPath()));
        coverSelectionLabel.setWrapText(true);
        Tooltip.install(coverSelectionLabel, new Tooltip("Ruta final de la portada guardada en disco"));
        Path[] selectedCoverFile = new Path[1];

        Button selectCoverButton = new Button("Seleccionar imagen");
        selectCoverButton.setOnAction(event -> {
            Path selectedFile = chooseCoverFile(owner);
            if (selectedFile != null) {
                selectedCoverFile[0] = selectedFile;
                coverSelectionLabel.setText(selectedFile.getFileName().toString());
            }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(18));
        grid.setHgap(12);
        grid.setVgap(10);

        int row = 0;
        grid.add(sectionLabel("Titulo"), 0, row);
        grid.add(titleField, 1, row++);
        grid.add(sectionLabel("Autor"), 0, row);
        grid.add(authorField, 1, row++);
        grid.add(sectionLabel("ISBN"), 0, row);
        grid.add(isbnField, 1, row++);
        grid.add(sectionLabel("Editorial"), 0, row);
        grid.add(publisherField, 1, row++);
        grid.add(sectionLabel("Anio"), 0, row);
        grid.add(yearField, 1, row++);
        grid.add(sectionLabel("Categoria"), 0, row);
        grid.add(categoryField, 1, row++);
        grid.add(sectionLabel("Carrera"), 0, row);
        grid.add(careerField, 1, row++);
        grid.add(sectionLabel("Descripcion"), 0, row);
        grid.add(descriptionArea, 1, row++);
        grid.add(sectionLabel("Portada"), 0, row);
        grid.add(new VBox(6, selectCoverButton, coverSelectionLabel), 1, row++);
        grid.add(sectionLabel("Codigo de inventario"), 0, row);
        grid.add(inventoryCodeField, 1, row++);
        grid.add(sectionLabel("Estado"), 0, row);
        grid.add(statusBox, 1, row++);
        grid.add(sectionLabel("Notas"), 0, row);
        grid.add(notesField, 1, row++);
        grid.add(sectionLabel("Ubicación"), 0, row);
        grid.add(codeField, 1, row++);

        Button saveButton = new Button(createMode ? "Guardar" : "Actualizar");
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

        HBox actions = new HBox(10, saveButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        grid.add(actions, 1, row);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        dialog.setScene(new Scene(scrollPane, 700, 720));
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
        bookTitle.setCareer(normalizeOptional(careerField.getValue()));
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

    private Label sectionLabel(String text) {
        Label label = new Label(text);
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
        alert.setHeaderText("No se pudo guardar");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
