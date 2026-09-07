package cl.untec.biblioteca_virtual.dao;

import cl.untec.biblioteca_virtual.config.dbConnection;
import cl.untec.biblioteca_virtual.model.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    
    /**
     * Inserta un nuevo préstamo.
     * @param prestamo objeto prestamo
     * @throws SQLException
     */
    public void insertar(Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO prestamos (usuario_id, libro_id, titulo_libro, isbn_libro, " +
                     "fecha_prestamo, fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, prestamo.getUsuarioId());
            pstmt.setLong(2, prestamo.getLibroId());
            pstmt.setString(3, prestamo.getTituloLibro());
            pstmt.setString(4, prestamo.getIsbnLibro());
            pstmt.setDate(5, Date.valueOf(prestamo.getFechaPrestamo()));
            pstmt.setDate(6, prestamo.getFechaDevolucion() != null ? Date.valueOf(prestamo.getFechaDevolucion()) : null);
            pstmt.setBoolean(7, prestamo.isDevuelto());
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Obtiene un único préstamo por id.
     * @param id id de prestamo
     * @return objeto prestamo
     * @throws SQLException
     */
    public Prestamo obtenerPorId(long id) throws SQLException {
        String sql = "SELECT * FROM prestamos WHERE id = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearPrestamo(rs);
            }
        }
        return null;
    }
    
    /**
     * Obtiene todos los préstamos registrados a un usuario en específico ordenados por fecha.
     * @param usuarioId id de usuario para búsqueda de todos sus prestamos
     * @return lista de prestamos
     * @throws SQLException
     */
    public List<Prestamo> obtenerPorUsuario(long usuarioId) throws SQLException {
        String sql = "SELECT * FROM prestamos WHERE usuario_id = ? ORDER BY fecha_prestamo DESC";
        List<Prestamo> prestamos = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        }
        return prestamos;
    }
    
    /**
     * Obtiene todos los préstamos pendientes de un usuario específico.
     * @param usuarioId id de usuario como filtro para búsqueda de prestamo
     * @return lista de prestamos
     * @throws SQLException
     */
    public List<Prestamo> obtenerPrestamosActivosPorUsuario(long usuarioId) throws SQLException {
        String sql = "SELECT * FROM prestamos WHERE usuario_id = ? AND devuelto = false ORDER BY fecha_prestamo DESC";
        List<Prestamo> prestamos = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        }
        return prestamos;
    }
    
    /**
     * Obtiene todos los préstamos de un libro en específico.
     * @param libroId id de libro como filtro para buscar prestamo
     * @return lista de prestamos
     * @throws SQLException
     */
    public List<Prestamo> obtenerPorLibro(long libroId) throws SQLException {
        String sql = "SELECT * FROM prestamos WHERE libro_id = ? ORDER BY fecha_prestamo DESC";
        List<Prestamo> prestamos = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, libroId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        }
        return prestamos;
    } 

    /**
     * Obtiene todos los préstamos registrados ordenados por fecha.
     * @return lista de prestamos
     * @throws SQLException
     */
    public List<Prestamo> obtenerTodos() throws SQLException {
        String sql = "SELECT * FROM prestamos ORDER BY fecha_prestamo DESC";
        List<Prestamo> prestamos = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        }
        return prestamos;
    }
    
    /**
     * Obtiene todos los préstamos pendientes.
     * @return lista de prestamos
     * @throws SQLException
     */
    public List<Prestamo> obtenerPrestamosActivos() throws SQLException {
        String sql = "SELECT * FROM prestamos WHERE devuelto = false ORDER BY fecha_prestamo DESC";
        List<Prestamo> prestamos = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        }
        return prestamos;
    }
    
    /**
     * Actualiza detalles de un préstamo.
     * @param prestamo objeto prestamo a actualizar
     * @throws SQLException
     */
    public void actualizar(Prestamo prestamo) throws SQLException {
        String sql = "UPDATE prestamos SET usuario_id = ?, libro_id = ?, titulo_libro = ?, " +
                     "isbn_libro = ?, fecha_prestamo = ?, fecha_devolucion = ?, devuelto = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, prestamo.getUsuarioId());
            pstmt.setLong(2, prestamo.getLibroId());
            pstmt.setString(3, prestamo.getTituloLibro());
            pstmt.setString(4, prestamo.getIsbnLibro());
            pstmt.setDate(5, Date.valueOf(prestamo.getFechaPrestamo()));
            pstmt.setDate(6, prestamo.getFechaDevolucion() != null ? Date.valueOf(prestamo.getFechaDevolucion()) : null);
            pstmt.setBoolean(7, prestamo.isDevuelto());
            pstmt.setLong(8, prestamo.getId());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Marca un préstamo como devuelto y registra fecha de devolución.
     * @param id id de prestamo
     * @param fechaDevolucion fecha de devolución de préstamo
     * @throws SQLException
     */
    public void marcarDevuelto(long id, LocalDate fechaDevolucion) throws SQLException {
        String sql = "UPDATE prestamos SET devuelto = true, fecha_devolucion = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(fechaDevolucion));
            pstmt.setLong(2, id);
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Borra un préstamo por id.
     * @param id id de prestamo
     * @throws SQLException
     */
    public void eliminar(long id) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Mapea una fila de  ResultSet a un objeto Prestamo.
     * @param rs ResultSet
     * @return objeto prestamo
     * @throws SQLException
     */
    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long usuarioId = rs.getLong("usuario_id");
        long libroId = rs.getLong("libro_id");
        String tituloLibro = rs.getString("titulo_libro");
        String isbnLibro = rs.getString("isbn_libro");
        LocalDate fechaPrestamo = rs.getDate("fecha_prestamo") != null ? 
            rs.getDate("fecha_prestamo").toLocalDate() : null;
        LocalDate fechaDevolucion = rs.getDate("fecha_devolucion") != null ? 
            rs.getDate("fecha_devolucion").toLocalDate() : null;
        boolean devuelto = rs.getBoolean("devuelto");
        
        return new Prestamo(id, usuarioId, libroId, tituloLibro, isbnLibro, 
                                fechaPrestamo, fechaDevolucion, devuelto);
    }
}

    