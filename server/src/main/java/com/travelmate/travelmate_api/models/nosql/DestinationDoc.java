package com.travelmate.travelmate_api.models.nosql;

import java.util.ArrayList;
import java.util.List;

public class DestinationDoc {
    private String id; // Document ID string
    private String name;
    private String overview;
    private String image;
    private List<String> activities = new ArrayList<>();
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public List<String> getActivities() {
		return activities;
	}
	public void setActivities(List<String> activities) {
		this.activities = activities;
	}
	
	// This dynamically intercepts the data and generates the "famousCities" array on the fly!
	public List<String> getFamousCities() {
        List<String> cities = new ArrayList<>();
        if (this.activities != null) {
            for (String activity : this.activities) {
                if (activity.startsWith("CITY:")) {
                    cities.add(activity.substring(5)); // Trims out the "CITY:" prefix
                }
            }
        }
        return cities;
    }

    // This dynamically intercepts the data and generates the "famousPlaces" array on the fly!
    public List<String> getFamousPlaces() {
        List<String> places = new ArrayList<>();
        if (this.activities != null) {
            for (String activity : this.activities) {
                if (activity.startsWith("PLACE:")) {
                    places.add(activity.substring(6)); // Trims out the "PLACE:" prefix
                }
            }
        }
        return places;
    }

    
}
