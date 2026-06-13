package com.travelmate.travelmate_api.repository.sql;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelmate.travelmate_api.models.sql.UserSQL;

@Repository
public interface UserSQLRepository extends JpaRepository<UserSQL, Long> {
	Optional<UserSQL> findByEmail(String email);
}
