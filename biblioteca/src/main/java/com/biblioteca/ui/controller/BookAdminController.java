package com.biblioteca.ui.controller;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.service.InventoryService;

public class BookAdminController {
    private final InventoryService inventoryService;

    public BookAdminController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void registerBook(BookTitle bookTitle, BookCopy bookCopy, Location location) {
        inventoryService.registerBook(bookTitle, bookCopy, location);
    }

    public void updateBook(BookTitle bookTitle) {
        inventoryService.updateBook(bookTitle);
    }

    public void updateBookEntry(BookTitle bookTitle, BookCopy bookCopy, Location location) {
        inventoryService.updateBookEntry(bookTitle, bookCopy, location);
    }

    public void changeCopyStatus(Long copyId, CopyStatus status) {
        inventoryService.changeCopyStatus(copyId, status);
    }
}
