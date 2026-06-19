package com.travelmate.travelmate_api.dto;

public class BenchmarkDTO {
	private long cloudSqlDurationMs;
    private long firestoreDurationMs;
    private String generatedNoSqlId;

    public BenchmarkDTO(long cloudSqlDurationMs, long firestoreDurationMs, String generatedNoSqlId) {
        this.cloudSqlDurationMs = cloudSqlDurationMs;
        this.firestoreDurationMs = firestoreDurationMs;
        this.generatedNoSqlId = generatedNoSqlId;
    }

    // Getters and Setters
    public long getCloudSqlDurationMs() { return cloudSqlDurationMs; }
    public void setCloudSqlDurationMs(long cloudSqlDurationMs) { this.cloudSqlDurationMs = cloudSqlDurationMs; }

    public long getFirestoreDurationMs() { return firestoreDurationMs; }
    public void setFirestoreDurationMs(long firestoreDurationMs) { this.firestoreDurationMs = firestoreDurationMs; }

    public String getGeneratedNoSqlId() { return generatedNoSqlId; }
    public void setGeneratedNoSqlId(String generatedNoSqlId) { this.generatedNoSqlId = generatedNoSqlId; }
}
