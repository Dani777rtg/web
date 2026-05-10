package com.ucaldas.electoral.repo;

import com.ucaldas.electoral.domain.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
}
