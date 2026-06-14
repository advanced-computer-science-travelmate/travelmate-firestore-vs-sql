package com.travelmate.travelmate_api.models.nosql;

import java.util.List;

public class UserDoc {
    private String userId;
    private String name;
    private String email;
    private List<BookingDoc> embeddedBookings;

    public UserDoc() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BookingDoc> getEmbeddedBookings() {
        return embeddedBookings;
    }

    public void setEmbeddedBookings(List<BookingDoc> embeddedBookings) {
        this.embeddedBookings = embeddedBookings;
    }
}