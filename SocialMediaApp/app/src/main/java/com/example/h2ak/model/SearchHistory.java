package com.example.h2ak.model;

import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchHistory {
    private String id;
    private User currentUser;
    private User searchingUser;
    private static AtomicInteger count = new AtomicInteger(0);

    {
        id = String.format("SearchHistory%d", count.incrementAndGet());
    }
    public SearchHistory() {}

    public SearchHistory (User currentUser, User searchingUser) {
        this.currentUser = currentUser;
        this.searchingUser = searchingUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getSearchingUser() {
        return searchingUser;
    }

    public void setSearchingUser(User searchingUser) {
        this.searchingUser = searchingUser;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        SearchHistory searchHistory = (SearchHistory) obj;

        return this.currentUser.equals(searchHistory.getCurrentUser()) &&
                this.searchingUser.equals(searchHistory.getSearchingUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentUser, searchingUser);
    }
}
