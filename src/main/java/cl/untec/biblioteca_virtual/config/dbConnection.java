package cl.untec.biblioteca_virtual.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class dbConnection {

    private static final String dbUrl = "jdbc:h2:mem:bvirtual:;DB_CLOSE_DELAY=-1";
    private static final String dbUser = "sa";
    private static final String dbPwd = "";

    static {
        try {
            Class.forName("org.h2.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error iniciando base de datos", e);
        }
    }

    private dbConnection() {
    }

    public static Connection connectDb() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPwd);
    }

    private static void initializeDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
             BufferedReader reader = new BufferedReader(
                new InputStreamReader(dbConnection.class.getResourceAsStream("/db/001_init.sql")));
             Statement stmt = conn.createStatement()) {
            
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
            
            String[] statements = sql.toString().split(";");
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error al ejecutar script de inicio", e);
        }
    }
}
