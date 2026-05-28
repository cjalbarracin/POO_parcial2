package model;

import java.sql.Timestamp;

public class inventario {

    private int id;
    private int celular_id;
    private int almacenamiento;
    private double precio;
    private int ram;
    private Timestamp creado;

    public inventario(int id, int celular_id, int almacenamiento, double precio, int ram, Timestamp creado) {
        this.id = id;
        this.celular_id = celular_id;
        this.almacenamiento = almacenamiento;
        this.precio = precio;
        this.ram = ram;
        this.creado = creado;
    }

    public inventario(int celular_id, int almacenamiento, double precio, int ram) {
        this.celular_id = celular_id;
        this.almacenamiento = almacenamiento;
        this.precio = precio;
        this.ram = ram;
    }

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