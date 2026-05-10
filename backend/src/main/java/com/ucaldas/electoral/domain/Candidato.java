package com.ucaldas.electoral.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "candidatos")
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plancha_id")
    private Plancha plancha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private AppUser usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolCandidato rol;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plancha getPlancha() {
        return plancha;
    }

    public void setPlancha(Plancha plancha) {
        this.plancha = plancha;
    }

    public AppUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AppUser usuario) {
        this.usuario = usuario;
    }

    public RolCandidato getRol() {
        return rol;
    }

    public void setRol(RolCandidato rol) {
        this.rol = rol;
    }
}
