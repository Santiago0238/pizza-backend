package com.taller.proye01.DTO;


import java.time.OffsetDateTime;

public class AlertaDTO {

    private Long id;
    private String nivel;
    private String estado;
    private String mensaje;
    private OffsetDateTime fechaEmision;
    private OffsetDateTime fechaCierre;

    private Long focoCalorId;

    public AlertaDTO() {
    }

    public AlertaDTO(
            Long id,
            String nivel,
            String estado,
            String mensaje,
            OffsetDateTime fechaEmision,
            OffsetDateTime fechaCierre,
            Long focoCalorId) {

        this.id = id;
        this.nivel = nivel;
        this.estado = estado;
        this.mensaje = mensaje;
        this.fechaEmision = fechaEmision;
        this.fechaCierre = fechaCierre;
        this.focoCalorId = focoCalorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public OffsetDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(OffsetDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public OffsetDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(OffsetDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Long getFocoCalorId() {
        return focoCalorId;
    }

    public void setFocoCalorId(Long focoCalorId) {
        this.focoCalorId = focoCalorId;
    }
}