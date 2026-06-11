package com.travelmate.travelmate_api.repository.sql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelmate.travelmate_api.models.sql.Bookings;

@Repository
public interface BookingRepository extends JpaRepository<Bookings, Long> {
	List<Bookings> findByUserId(Long userId);
}
