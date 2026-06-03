package model;

import java.sql.Timestamp;

public class inventario {

    // Detalles técnicos que guardamos de cada producto en inventario
    private int id;
    private int celular_id;
    private int almacenamiento;
    private double precio;
    private int ram;
    private Timestamp creado;

    // Constructor completo: usado cuando ya tenemos toda la información, incluyendo el ID de la base de datos
    public inventario(int id, int celular_id, int almacenamiento, double precio, int ram, Timestamp creado) {
        this.id = id;
        this.celular_id = celular_id;
        this.almacenamiento = almacenamiento;
        this.precio = precio;
        this.ram = ram;
        this.creado = creado;
    }

    // Constructor simplificado: usado al crear un producto nuevo, antes de que tenga ID o fecha
    public inventario(int celular_id, int almacenamiento, double precio, int ram) {
        this.celular_id = celular_id;
        this.almacenamiento = almacenamiento;
        this.precio = precio;
        this.ram = ram;
    }

    // --- MÉTODOS DE ACCESO (GETTERS) ---
    // Permiten consultar cada una de las características guardadas en el objeto

    public int getId() {
        return id;
    }

    public int getCelular_id() {
        return celular_id;
    }

    public int getAlmacenamiento() {
        return almacenamiento;
    }

    public double getPrecio() {
        return precio;
    }

    public int getRam() {
        return ram;
    }

    public Timestamp getCreado() {
        return creado;
    }
}