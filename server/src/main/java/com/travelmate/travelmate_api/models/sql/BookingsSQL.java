package com.travelmate.travelmate_api.models.sql;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class BookingsSQL {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // FLIGHT, HOTEL, TRAIN, etc.

    @Column(nullable = false)
    private Double price;

    @Column(name = "confirmation_number", unique = true)
    private String confirmationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserSQL user;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private BudgetSQL budgetExpense;

    public BookingsSQL() {}
    public BookingsSQL(String type, Double price, String confirmationNumber, UserSQL user) {
        this.type = type;
        this.price = price;
        this.confirmationNumber = confirmationNumber;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getConfirmationNumber() { return confirmationNumber; }
    public void setConfirmationNumber(String confirmationNumber) { this.confirmationNumber = confirmationNumber; }
    public void setUser(UserSQL user) { this.user = user; }
    public BudgetSQL getBudgetExpense() { return budgetExpense; }
    public void setBudgetExpense(BudgetSQL budgetExpense) { this.budgetExpense = budgetExpense; }
    
    
}
