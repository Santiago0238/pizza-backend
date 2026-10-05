package com.taller.proye01.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "incendio_comunidad")
public class IncendioComunidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_afectacion")
    private Timestamp fechaAfectacion;

    @Column(name = "nivel_afectacion", length = 20)
    private String nivelAfectacion;

    @Column(name = "personas_evacuadas")
    private Integer personasEvacuadas;

    @Column(name = "viviendas_afectadas")
    private Integer viviendasAfectadas;

    @Column(name = "observaciones", length = 100)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_incendio", nullable = false)
    private Incendio incendio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comunidad", nullable = false)
    private Comunidad comunidad;

}