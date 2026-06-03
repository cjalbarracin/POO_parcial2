package db.operaciones;

import db.DBConnection;
import model.celular;
import model.inventario;
import java.sql.*;

public class TiendaDAO {

    /* * Patrón de diseño para centralizar la lectura de datos.
     * Utiliza StringBuilder para concatenar cadenas eficientemente en memoria,
     * evitando la creación innecesaria de objetos String en cada iteración del bucle.
     */
    private String ejecutarConsulta(String sql) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            boolean encontrado = false;
            while (rs.next()) {
                encontrado = true;
                sb.append("Marca: ").append(rs.getString("marca"))
                        .append(" | Modelo: ").append(rs.getString("modelo"))
                        .append(" | Cámara: ").append(rs.getInt("camara")).append("MP")
                        .append(" | Batería: ").append(rs.getInt("bateria")).append("mAh")
                        .append(" | Almacenamiento: ").append(rs.getInt("almacenamiento")).append("GB")
                        .append(" | RAM: ").append(rs.getInt("ram")).append("GB")
                        .append(" | Precio: $").append(rs.getDouble("precio")).append("\n");
            }
            return encontrado ? sb.toString() : "No se encontraron resultados.";
        } catch (SQLException e) {
            return "Error en BD: " + e.getMessage();
        }
    }

    /* * Operación atómica: usa transacciones (setAutoCommit(false)) para garantizar la integridad.
     * Si el registro en 'celular' o en 'inventario' falla, el 'rollback' evita datos huérfanos.
     * RETURN_GENERATED_KEYS permite recuperar el ID autoincremental del celular para vincularlo al inventario.
     */
    public boolean registrarCelularCompleto(celular c, inventario i) {
        String sqlCelular = "INSERT INTO celular (marca, modelo, camara, bateria) VALUES (?, ?, ?, ?)";
        String sqlInventario = "INSERT INTO inventario (celular_id, almacenamiento, precio, ram) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlCelular, Statement.RETURN_GENERATED_KEYS)) {
                ps1.setString(1, c.getMarca());
                ps1.setString(2, c.getModelo());
                ps1.setInt(3, c.getCamara());
                ps1.setInt(4, c.getBateria());
                ps1.executeUpdate();

                ResultSet rs = ps1.getGeneratedKeys();
                if (rs.next()) {
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlInventario)) {
                        ps2.setInt(1, rs.getInt(1));
                        ps2.setInt(2, i.getAlmacenamiento());
                        ps2.setDouble(3, i.getPrecio());
                        ps2.setInt(4, i.getRam());
                        ps2.executeUpdate();
                    }
                }
                conn.commit(); // Persiste ambos cambios solo si todo fue exitoso
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Reverte cambios si ocurre error en la inserción
                return false;
            }
        } catch (SQLException e) { return false; }
    }

    /* * Consultas centralizadas: se utiliza JOIN para normalizar la obtención de datos
     * de dos tablas relacionadas, optimizando el tráfico de red con una única consulta.
     */
    public String obtenerInventarioCompletoTexto() {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id");
    }

    /* * ILIKE se utiliza para búsquedas case-insensitive (insensible a mayúsculas/minúsculas).
     * Nota: En producción real, considera validar el input para evitar SQL Injection.
     */
    public String filtrarPorMarcaTexto(String marca) {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE c.marca ILIKE '%" + marca + "%'");
    }

    public String filtrarPorPrecioMax(double precio) {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.precio <= " + precio);
    }

    public String filtrarPorAlmacenamiento(int gb) {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.almacenamiento = " + gb);
    }
}