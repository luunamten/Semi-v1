package org.nam.custom;

import android.content.SearchRecentSuggestionsProvider;
import android.icu.text.Normalizer2;

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "org.nam.custom.MySuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;
    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
