package com.ucaldas.electoral.web;

import com.ucaldas.electoral.domain.Faculty;
import com.ucaldas.electoral.repo.FacultyRepository;
import com.ucaldas.electoral.web.dto.FacultyDtos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/faculties")
public class AdminFacultyController {

    private final FacultyRepository facultyRepository;

    public AdminFacultyController(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @PostMapping
    public FacultyDtos.FacultyResponse create(@Valid @RequestBody FacultyDtos.FacultyCreateRequest req) {
        Faculty f = new Faculty();
        f.setNombre(req.nombre().trim());
        f = facultyRepository.save(f);
        return new FacultyDtos.FacultyResponse(f.getId(), f.getNombre());
    }

    @PutMapping("/{id}")
    public FacultyDtos.FacultyResponse update(@PathVariable Long id, @Valid @RequestBody FacultyDtos.FacultyCreateRequest req) {
        Faculty f = facultyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Facultad no encontrada."));
        f.setNombre(req.nombre().trim());
        f = facultyRepository.save(f);
        return new FacultyDtos.FacultyResponse(f.getId(), f.getNombre());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        facultyRepository.deleteById(id);
    }

    @GetMapping
    public List<FacultyDtos.FacultyResponse> list() {
        return facultyRepository.findAll().stream()
                .map(f -> new FacultyDtos.FacultyResponse(f.getId(), f.getNombre()))
                .toList();
    }
}
