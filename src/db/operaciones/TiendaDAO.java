package db.operaciones;

import db.DBConnection;
import model.celular;
import model.inventario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TiendaDAO {

    // 1. Registro transaccional: Inserta en ambas tablas y vincula el ID
    public void registrarCelularCompleto(celular c, inventario i) {
        String sqlCelular = "INSERT INTO celular (marca, modelo, camara, bateria) VALUES (?, ?, ?, ?)";
        String sqlInventario = "INSERT INTO inventario (celular_id, almacenamiento, precio, ram) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar celular
            try (PreparedStatement ps1 = conn.prepareStatement(sqlCelular, Statement.RETURN_GENERATED_KEYS)) {
                ps1.setString(1, c.getMarca());
                ps1.setString(2, c.getModelo());
                ps1.setInt(3, c.getCamara());
                ps1.setInt(4, c.getBateria());
                ps1.executeUpdate();

                ResultSet rs = ps1.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);

                    // Insertar inventario vinculado
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlInventario)) {
                        ps2.setInt(1, idGenerado);
                        ps2.setInt(2, i.getAlmacenamiento());
                        ps2.setDouble(3, i.getPrecio());
                        ps2.setInt(4, i.getRam());
                        ps2.executeUpdate();
                    }
                }
                conn.commit(); // Confirmar ambos cambios
                System.out.println("Registro exitoso en ambas tablas.");
            } catch (SQLException e) {
                conn.rollback(); // Si algo falla, deshacer todo
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Consulta unificada (JOIN): Trae toda la info de ambas tablas
    public void listarInventarioCompleto() {
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c " +
                "JOIN inventario i ON c.id = i.celular_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- LISTADO COMPLETO ---");
            while (rs.next()) {
                System.out.println("Marca: " + rs.getString("marca") +
                        " | Modelo: " + rs.getString("modelo") +
                        " | Precio: $" + rs.getDouble("precio") +
                        " | Almacenamiento: " + rs.getInt("almacenamiento") + "GB" +
                        " | RAM: " + rs.getInt("ram") + "GB");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 3. Filtrado por marca
    // 3. Filtrado por marca
    public List<celular> filtrarPorMarca(String marca) {
        List<celular> lista = new ArrayList<>();
        String sql = "SELECT * FROM celular WHERE marca ILIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + marca + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new celular(
                            rs.getString("marca"),
                            rs.getString("modelo"),
                            rs.getInt("camara"),
                            rs.getInt("bateria")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 4. Filtrar por rango de precios (Muestra info completa)
    public void filtrarPorPrecio(double precioMin, double precioMax) {
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c " +
                "JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.precio BETWEEN ? AND ? " +
                "ORDER BY i.precio ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, precioMin);
            ps.setDouble(2, precioMax);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- CELULARES ENTRE $" + precioMin + " Y $" + precioMax + " ---");
                boolean conResultados = false;

                while (rs.next()) {
                    conResultados = true;
                    System.out.println("Marca: " + rs.getString("marca") +
                            " | Modelo: " + rs.getString("modelo") +
                            " | Precio: $" + rs.getDouble("precio") +
                            " | Almacenamiento: " + rs.getInt("almacenamiento") + "GB" +
                            " | RAM: " + rs.getInt("ram") + "GB");
                }
                if (!conResultados) {
                    System.out.println("No se encontraron celulares en este rango de precio.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. Filtrar por almacenamiento específico (Ej: 128, 256)
    public void filtrarPorAlmacenamiento(int gb) {
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c " +
                "JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.almacenamiento = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gb);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- CELULARES CON " + gb + "GB DE ALMACENAMIENTO ---");
                boolean conResultados = false;

                while (rs.next()) {
                    conResultados = true;
                    System.out.println("Marca: " + rs.getString("marca") +
                            " | Modelo: " + rs.getString("modelo") +
                            " | Precio: $" + rs.getDouble("precio") +
                            " | Almacenamiento: " + rs.getInt("almacenamiento") + "GB" +
                            " | RAM: " + rs.getInt("ram") + "GB");
                }
                if (!conResultados) {
                    System.out.println("No se encontraron celulares con esa capacidad.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}