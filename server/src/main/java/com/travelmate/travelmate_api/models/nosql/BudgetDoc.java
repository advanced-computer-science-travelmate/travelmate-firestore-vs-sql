package com.travelmate.travelmate_api.models.nosql;

public class BudgetDoc {
    private String category;
    private Double amount;

    public BudgetDoc() {}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}