package com.biblioteca.ui.view;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.service.BusinessException;
import com.biblioteca.ui.controller.BookAdminController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BookAdminView {
    // Dialogo reutilizable para registrar o editar una obra y uno de sus ejemplares.
    private final BookAdminController bookAdminController;
    private final Runnable onSaved;

    public BookAdminView(BookAdminController bookAdminController, Runnable onSaved) {
        this.bookAdminController = bookAdminController;
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
        dialog.setMinWidth(640);
        dialog.setMinHeight(820);
        dialog.setResizable(true);

        TextField titleField = new TextField(value(bookTitle.getTitle()));
        TextField authorField = new TextField(value(bookTitle.getAuthor()));
        TextField isbnField = new TextField(value(bookTitle.getIsbn()));
        TextField publisherField = new TextField(value(bookTitle.getPublisher()));
        TextField yearField = new TextField(bookTitle.getYear() > 0 ? String.valueOf(bookTitle.getYear()) : "");
        TextField categoryField = new TextField(value(bookTitle.getCategory()));
        TextField careerField = new TextField(value(bookTitle.getCareer()));
        TextArea descriptionArea = new TextArea(value(bookTitle.getDescription()));
        descriptionArea.setPrefRowCount(3);
        TextField coverPathField = new TextField(value(bookTitle.getCoverPath()));

        TextField inventoryCodeField = new TextField(value(bookCopy.getInventoryCode()));
        ComboBox<CopyStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(CopyStatus.values());
        statusBox.getSelectionModel().select(bookCopy.getStatus() != null ? bookCopy.getStatus() : CopyStatus.AVAILABLE);
        TextField notesField = new TextField(value(bookCopy.getNotes()));

        TextField roomField = new TextField(value(location.getRoom()));
        TextField sectionField = new TextField(value(location.getSection()));
        TextField shelfField = new TextField(value(location.getShelf()));
        TextField levelField = new TextField(value(location.getLevel()));
        TextField positionField = new TextField(value(location.getPosition()));
        TextField codeField = new TextField(value(location.getCode()));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(16));
        grid.setHgap(10);
        grid.setVgap(8);

        int row = 0;
        grid.add(new Label("Titulo"), 0, row);
        grid.add(titleField, 1, row++);
        grid.add(new Label("Autor"), 0, row);
        grid.add(authorField, 1, row++);
        grid.add(new Label("ISBN"), 0, row);
        grid.add(isbnField, 1, row++);
        grid.add(new Label("Editorial"), 0, row);
        grid.add(publisherField, 1, row++);
        grid.add(new Label("Anio"), 0, row);
        grid.add(yearField, 1, row++);
        grid.add(new Label("Categoria"), 0, row);
        grid.add(categoryField, 1, row++);
        grid.add(new Label("Carrera"), 0, row);
        grid.add(careerField, 1, row++);
        grid.add(new Label("Descripcion"), 0, row);
        grid.add(descriptionArea, 1, row++);
        grid.add(new Label("Ruta portada"), 0, row);
        grid.add(coverPathField, 1, row++);
        grid.add(new Label("Codigo inventario"), 0, row);
        grid.add(inventoryCodeField, 1, row++);
        grid.add(new Label("Estado"), 0, row);
        grid.add(statusBox, 1, row++);
        grid.add(new Label("Notas"), 0, row);
        grid.add(notesField, 1, row++);
        grid.add(new Label("Sala"), 0, row);
        grid.add(roomField, 1, row++);
        grid.add(new Label("Seccion"), 0, row);
        grid.add(sectionField, 1, row++);
        grid.add(new Label("Estante"), 0, row);
        grid.add(shelfField, 1, row++);
        grid.add(new Label("Nivel"), 0, row);
        grid.add(levelField, 1, row++);
        grid.add(new Label("Posicion"), 0, row);
        grid.add(positionField, 1, row++);
        grid.add(new Label("Codigo ubicacion"), 0, row);
        grid.add(codeField, 1, row++);

        Button saveButton = new Button(createMode ? "Guardar" : "Actualizar");
        saveButton.setOnAction(event -> {
            try {
                fillBookTitle(bookTitle, titleField, authorField, isbnField, publisherField, yearField, categoryField, careerField, descriptionArea, coverPathField);
                fillBookCopy(bookCopy, inventoryCodeField, statusBox, notesField);
                fillLocation(location, roomField, sectionField, shelfField, levelField, positionField, codeField);

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
        grid.add(saveButton, 1, row);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);

        dialog.setScene(new Scene(scrollPane, 680, 860));
        dialog.centerOnScreen();
        dialog.showAndWait();
    }

    private void fillBookTitle(BookTitle bookTitle,
                               TextField titleField,
                               TextField authorField,
                               TextField isbnField,
                               TextField publisherField,
                               TextField yearField,
                               TextField categoryField,
                               TextField careerField,
                               TextArea descriptionArea,
                               TextField coverPathField) {
        bookTitle.setTitle(titleField.getText());
        bookTitle.setAuthor(authorField.getText());
        bookTitle.setIsbn(normalizeOptional(isbnField.getText()));
        bookTitle.setPublisher(normalizeOptional(publisherField.getText()));
        bookTitle.setYear(parseYear(yearField.getText()));
        bookTitle.setCategory(normalizeOptional(categoryField.getText()));
        bookTitle.setCareer(normalizeOptional(careerField.getText()));
        bookTitle.setDescription(normalizeOptional(descriptionArea.getText()));
        bookTitle.setCoverPath(normalizeOptional(coverPathField.getText()));
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
                              TextField positionField,
                              TextField codeField) {
        location.setRoom(normalizeOptional(roomField.getText()));
        location.setSection(normalizeOptional(sectionField.getText()));
        location.setShelf(normalizeOptional(shelfField.getText()));
        location.setLevel(normalizeOptional(levelField.getText()));
        location.setPosition(normalizeOptional(positionField.getText()));
        location.setCode(normalizeOptional(codeField.getText()));
    }

    private int parseYear(String yearText) {
        if (yearText == null || yearText.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(yearText.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El anio debe ser numerico.");
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
