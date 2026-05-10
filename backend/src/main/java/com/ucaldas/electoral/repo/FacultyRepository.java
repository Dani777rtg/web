package com.ucaldas.electoral.repo;

import com.ucaldas.electoral.domain.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByNombre(String nombre);
}
