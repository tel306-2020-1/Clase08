package com.example.clase08;

import java.io.Serializable;

public class TrabajoDto {

    private String estado;
    private int cuota;
    private Trabajo[] trabajos;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCuota() {
        return cuota;
    }

    public void setCuota(int cuota) {
        this.cuota = cuota;
    }

    public Trabajo[] getTrabajos() {
        return trabajos;
    }

    public void setTrabajos(Trabajo[] trabajos) {
        this.trabajos = trabajos;
    }
}
