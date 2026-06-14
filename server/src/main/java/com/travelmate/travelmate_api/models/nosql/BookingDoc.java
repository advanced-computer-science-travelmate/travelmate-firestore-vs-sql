package com.travelmate.travelmate_api.models.nosql;

public class BookingDoc {
    private String bookingId;
    private String type;
    private Double price;
    private String confirmationNumber;
    private BudgetDoc budgetExpense;

    public BookingDoc() {}

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public BudgetDoc getBudgetExpense() {
        return budgetExpense;
    }

    public void setBudgetExpense(BudgetDoc budgetExpense) {
        this.budgetExpense = budgetExpense;
    }
}