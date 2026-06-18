package com.biblioteca.search.query;

import java.util.Set;

public class BookSearchCriteria {
    private String text;
    private Set<String> careers;
    private Set<String> firstLetters;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<String> getCareers() {
        return careers;
    }

    public void setCareers(Set<String> careers) {
        this.careers = careers;
    }

    public Set<String> getFirstLetters() {
        return firstLetters;
    }

    public void setFirstLetters(Set<String> firstLetters) {
        this.firstLetters = firstLetters;
    }
}
