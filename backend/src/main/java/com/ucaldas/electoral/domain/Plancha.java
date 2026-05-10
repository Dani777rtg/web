package com.ucaldas.electoral.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "planchas")
public class Plancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPlancha estado = EstadoPlancha.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proceso_id")
    private ElectoralProcess proceso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collegial_body_id")
    private CollegialBody collegialBody;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facultad_id")
    private Faculty facultad;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoPlancha getEstado() {
        return estado;
    }

    public void setEstado(EstadoPlancha estado) {
        this.estado = estado;
    }

    public ElectoralProcess getProceso() {
        return proceso;
    }

    public void setProceso(ElectoralProcess proceso) {
        this.proceso = proceso;
    }

    public CollegialBody getCollegialBody() {
        return collegialBody;
    }

    public void setCollegialBody(CollegialBody collegialBody) {
        this.collegialBody = collegialBody;
    }

    public Faculty getFacultad() {
        return facultad;
    }

    public void setFacultad(Faculty facultad) {
        this.facultad = facultad;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
