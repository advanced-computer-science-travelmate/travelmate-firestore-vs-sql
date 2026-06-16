package com.travelmate.travelmate_api.repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelmate.travelmate_api.models.sql.DestinationSQL;

@Repository
public interface DestinationSQLRepository extends JpaRepository<DestinationSQL, Long> {

}
