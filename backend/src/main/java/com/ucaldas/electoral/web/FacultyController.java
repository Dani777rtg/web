package com.ucaldas.electoral.web;

import com.ucaldas.electoral.repo.FacultyRepository;
import com.ucaldas.electoral.web.dto.FacultyDtos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {

    private final FacultyRepository facultyRepository;

    public FacultyController(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @GetMapping
    public List<FacultyDtos.FacultyResponse> list() {
        return facultyRepository.findAll().stream()
                .map(f -> new FacultyDtos.FacultyResponse(f.getId(), f.getNombre()))
                .toList();
    }
}
