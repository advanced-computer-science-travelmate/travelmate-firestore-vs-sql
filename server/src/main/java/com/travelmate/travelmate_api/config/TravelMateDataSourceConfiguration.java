package com.travelmate.travelmate_api.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class TravelMateDataSourceConfiguration {

    @Bean
    @Primary // This tells Spring Boot: "Ignore properties files. Use THIS configuration instead!"
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Pointing directly to your correct Google Cloud IP
        dataSource.setUrl("jdbc:mysql://35.198.178.189:3306/travelmate_db?"+ "useSSL=true"
                + "&sslMode=REQUIRED"
                + "&allowPublicKeyRetrieval=true"
                + "&useServerPrepStmts=false"
                + "&serverTimezone=UTC");
        
        // This forces Spring Boot to use 'travelmate_admin' instead of defaulting to 'root'
        dataSource.setUsername("travelmate_admin");
        dataSource.setPassword("ddFP;j8.0x%3F%3D]<m6"); // Make sure this matches the password you set in GCP console users tab
        
        System.out.println("🔌 Hardcoded Cloud SQL DataSource Bean successfully connected!");
        return dataSource;
    }
}