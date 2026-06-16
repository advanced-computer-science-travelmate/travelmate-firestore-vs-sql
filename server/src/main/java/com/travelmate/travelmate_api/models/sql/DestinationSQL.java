package com.travelmate.travelmate_api.models.sql;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "destinations")
public class DestinationSQL {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Added to support your teammate's UI template fields
    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "image_url")
    private String image;

    @ElementCollection
    @CollectionTable(name = "destination_activities", joinColumns = @JoinColumn(name = "destination_id"))
    @Column(name = "activity")
    private List<String> activities;

    public DestinationSQL() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public List<String> getActivities() { return activities; }
    public void setActivities(List<String> activities) { this.activities = activities; }
}
