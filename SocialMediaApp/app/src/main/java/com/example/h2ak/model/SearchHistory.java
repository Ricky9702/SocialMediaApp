package com.example.h2ak.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class SearchHistory {
    private int id;
    private User currentUser;
    private User searchingUser;


    public SearchHistory() {}

    public SearchHistory (User currentUser, User searchingUser) {
        this.currentUser = currentUser;
        this.searchingUser = searchingUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
