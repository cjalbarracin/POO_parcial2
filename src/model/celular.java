package model;

public class celular {
    // Estas variables guardan la información básica de cada producto
    private String marca;
    private String modelo;
    private int camara;
    private int bateria;

    // Crea un nuevo objeto celular con sus características iniciales
    public celular(String marca, String modelo, int camara, int bateria) {
        this.marca = marca;
        this.modelo = modelo;
        this.camara = camara;
        this.bateria = bateria;
    }

    // --- MÉTODOS DE ACCESO (GETTERS) ---
    // Permiten que otras partes del sistema "lean" la información del celular

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getCamara() {
        return camara;
    }

    public int getBateria() {
        return bateria;
    }
}