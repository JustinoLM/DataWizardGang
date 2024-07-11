package com.proyecto.agroinsight;
public class Planta {
    private int idPlanta;
    private String nombreComun;
    private byte[] imagen;
    private String fechaGuardado;
    private String tipoPlaga;

    // Constructor
    public Planta(int idPlanta, String nombreComun, byte[] imagen, String fechaGuardado, String tipoPlaga) {
        this.idPlanta = idPlanta;
        this.nombreComun = nombreComun;
        this.imagen = imagen != null ? imagen : new byte[0];
        this.fechaGuardado = fechaGuardado;
        this.tipoPlaga = tipoPlaga;
    }

    // Getters
    public int getIdPlanta() {
        return idPlanta;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public String getFechaGuardado() {
        return fechaGuardado;
    }

    public String getTipoPlaga() {
        return tipoPlaga;
    }

    @Override
    public String toString() {
        return "Planta{" +
                "idPlanta=" + idPlanta +
                ", nombreComun='" + nombreComun + '\'' +
                ", fechaGuardado='" + fechaGuardado + '\'' +
                ", tipoPlaga='" + tipoPlaga + '\'' +
                '}';
    }
}
