package db;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    // Objeto para guardar la configuración de conexión
    private static Properties prop = new Properties();

    static {
        // Carga automática de los datos de conexión desde el archivo config.properties
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Error: No se encontró el archivo config.properties dentro de la carpeta src");
            } else {
                prop.load(input);
            }
        } catch (IOException ex) {
            System.err.println("Error al cargar config.properties: " + ex.getMessage());
        }
    }

    // Crea y devuelve una nueva conexión a la base de datos usando las credenciales cargadas
    public static Connection getConnection() throws SQLException {
        String url = prop.getProperty("db.url");
        String user = prop.getProperty("db.user");
        String pass = prop.getProperty("db.password");

        // Verifica que la información necesaria esté presente antes de intentar conectar
        if (url == null || user == null || pass == null) {
            throw new SQLException("Error: Faltan propiedades en config.properties");
        }

        return DriverManager.getConnection(url, user, pass);
    }
}