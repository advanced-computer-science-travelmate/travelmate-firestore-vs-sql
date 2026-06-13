package com.travelmate.travelmate_api.repository.sql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelmate.travelmate_api.models.sql.BookingsSQL;

@Repository
public interface BookingSQLRepository extends JpaRepository<BookingsSQL, Long> {
	List<BookingsSQL> findByUserId(Long userId);
}
