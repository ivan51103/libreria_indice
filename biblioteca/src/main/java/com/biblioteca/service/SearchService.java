package com.biblioteca.service;

import com.biblioteca.search.query.SearchSuggestion;
import java.text.Normalizer;
import java.util.List;

public class SearchService {
    public List<SearchSuggestion> suggest(String text) {
        return List.of();
    }

    public String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().trim();
    }
}
