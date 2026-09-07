package cl.untec.biblioteca_virtual.dao;

import cl.untec.biblioteca_virtual.config.dbConnection;
import cl.untec.biblioteca_virtual.model.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    

    /**
     * Inserta un nuevo libro en la base de datos
     * @param lib objeto libro
     * @throws SQLException
     */
    public void insertar(Libro lib) throws SQLException {
        String sql = "INSERT INTO libros (isbn, titulo, autor, stock) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lib.getIsbn());
            pstmt.setString(2, lib.getTitulo());
            pstmt.setString(3, lib.getAutor());
            pstmt.setInt(4, lib.getStock());
            
            pstmt.executeUpdate();
        }
    }
    

    /**
     * Obtiene libro por id
     * @param id id de libro 
     * @return objeto libro
     * @throws SQLException
     */
    public Libro obtenerPorId(long id) throws SQLException {
        String sql = "SELECT * FROM libros WHERE id = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearLibro(rs);
            }
        }
        return null;
    }
    
    
    /**
     * Obtiene un libro por isbn.
     * @param isbn cadena de texto con identificador de libro
     * @return regresa objeto libro
     * @throws SQLException
     */
    public Libro obtenerPorIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM libros WHERE isbn = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearLibro(rs);
            }
        }
        return null;
    }
    

    /**
     * Busca libros por título.
     * @param titulo cadena de texto con título de libro
     * @return lista de libros filtrada por título de libro
     * @throws SQLException
     */
    public List<Libro> buscarPorTitulo(String titulo) throws SQLException {
        String sql = "SELECT * FROM libros WHERE LOWER(titulo) LIKE LOWER(?)";
        List<Libro> libros = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + titulo + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }
        }
        return libros;
    }
    
    /**
     * Busca libros por autor
     * @param autor cadena de texto con nombre de autor de libro
     * @return lista de libros filtrada por autor
     * @throws SQLException
     */
    public List<Libro> buscarPorAutor(String autor) throws SQLException {
        String sql = "SELECT * FROM libros WHERE LOWER(autor) LIKE LOWER(?)";
        List<Libro> libros = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + autor + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }
        }
        return libros;
    }
    
    /**
     * Obtiene todos los libros
     * @return lista de todos los libros
     * @throws SQLException
     */
    public List<Libro> obtenerTodos() throws SQLException {
        String sql = "SELECT * FROM libros ORDER BY titulo ASC";
        List<Libro> libros = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }
        }
        return libros;
    }
    
    /**
     * Obtiene sólo aquellos libros disponibles (stock > 0)
     * @return lista de libros cuyo stock es mayor a cero
     * @throws SQLException
     */
    public List<Libro> obtenerDisponibles() throws SQLException {
        String sql = "SELECT * FROM libros WHERE stock > 0 ORDER BY titulo ASC";
        List<Libro> libros = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }
        }
        return libros;
    }
    
/**
 * Actualiza un libro existente.
 * @param lib objeto libro a actualizar detalles
 * @throws SQLException
 */
    public void actualizar(Libro lib) throws SQLException {
        String sql = "UPDATE libros SET isbn = ?, titulo = ?, autor = ?, stock = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lib.getIsbn());
            pstmt.setString(2, lib.getTitulo());
            pstmt.setString(3, lib.getAutor());
            pstmt.setInt(4, lib.getStock());
            pstmt.setLong(5, lib.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Decrementa el stock cuando un libro es prestado.
     * @param id id de libro a decrementar stock
     * @throws SQLException
     */
    public void disminuirStock(long id) throws SQLException {
        String sql = "UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
   /**
    * Aumenta el stock cuando un libro es regresado.
    * @param id id de libro a aumentar stock
    * @throws SQLException
    */
    public void aumentarStock(long id) throws SQLException {
        String sql = "UPDATE libros SET stock = stock + 1 WHERE id = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
   /**
    * Borra un libro según id
    * @param id id de libro
    * @throws SQLException
    */
    public void eliminar(long id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Mapea una fila de ResultSet a un objeto libro.
     * @param rs ResultSet
     * @return objecto libro
     * @throws SQLException
     */
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String isbn = rs.getString("isbn");
        String titulo = rs.getString("titulo");
        String autor = rs.getString("autor");
        int stock = rs.getInt("stock");
        
        return new Libro(id, isbn, titulo, autor, stock);
    }

    /**
     * Revisa si existe préstamo según id de libro
     * @param libroId id de libro.
     * @return
     */
    public boolean tienePrestamo(Long libroId) {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE libro_id = ?";
        
        try (Connection conn = dbConnection.connectDb();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, libroId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                long count = rs.getLong(1);
                return count > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al revisar préstamos de libro: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

}
