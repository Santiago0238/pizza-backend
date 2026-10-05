package com.taller.proye01.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "capa")
public class Capa {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "nombre", nullable = false, length = 100)
	    private String nombre;

	    @Column(name = "tipo", length = 50)
	    private String tipo;

	    @Column(name = "formato", length = 20)
	    private String formato;

	    @Column(name = "publico")
	    private Boolean publico;

	    @Column(name = "fecha_creacion")
	    private Timestamp fechaCreacion;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "id_usuario", nullable = false)
	    private Usuario usuario;

	    @JsonIgnore
	    @OneToMany(mappedBy = "capa")
	    private List<CapaCaracteristica> caracteristicas;

	   
}