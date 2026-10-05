package com.taller.proye01.model;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
public class Usuario {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "nombre", nullable = false, length = 100)
	    private String nombre;

	    @Column(name = "correo", nullable = false, unique = true, length = 100)
	    private String correo;

	    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	    @Column(name = "contra", nullable = false, length = 255)
	    private String contra;

	    @Column(name = "activo")
	    private Boolean activo;

	    @Column(name = "fecha_creacion")
	    private Timestamp fechaCreacion;

	    @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name = "id_rol", nullable = false)
	    private Rol rol;

	    @JsonIgnore
	    @OneToMany(mappedBy = "usuario")
	    private List<Capa> capas;

	    @JsonIgnore
	    @OneToMany(mappedBy = "usuario")
	    private List<Ruta> rutas;

	    @JsonIgnore
	    @OneToMany(mappedBy = "usuario")
	    private List<Alerta> alertas;
   

    
}