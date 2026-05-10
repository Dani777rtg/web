package com.ucaldas.electoral.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "electoral_processes")
public class ElectoralProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlcanceElectoral alcance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facultad_id")
    private Faculty facultad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collegial_body_id")
    private CollegialBody collegialBody;

    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private Instant fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProceso estado = EstadoProceso.BORRADOR;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public AlcanceElectoral getAlcance() {
        return alcance;
    }

    public void setAlcance(AlcanceElectoral alcance) {
        this.alcance = alcance;
    }

    public Faculty getFacultad() {
        return facultad;
    }

    public void setFacultad(Faculty facultad) {
        this.facultad = facultad;
    }

    public CollegialBody getCollegialBody() {
        return collegialBody;
    }

    public void setCollegialBody(CollegialBody collegialBody) {
        this.collegialBody = collegialBody;
    }

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Instant fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Instant getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Instant fechaFin) {
        this.fechaFin = fechaFin;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    public void setEstado(EstadoProceso estado) {
        this.estado = estado;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
