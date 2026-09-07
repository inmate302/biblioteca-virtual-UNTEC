package cl.untec.biblioteca_virtual.dao;

import cl.untec.biblioteca_virtual.config.dbConnection;
import cl.untec.biblioteca_virtual.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    
    /**
     * Mapea una fila del ResultSet para crear un objeto usuario.
     * @param rs ResultSet
     * @return objeto usuario
     * @throws SQLException
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String nombre = rs.getString("nombre");
        String email = rs.getString("email");
        String password = rs.getString("password");
        
        return new Usuario(id, nombre, email, password);
    }

    /**
     * Obtiene todos los usuarios
     * @return lista de usuario
     * @throws SQLException
     */
    public List<Usuario> obtenerUsuarios() throws SQLException {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = dbConnection.connectDb();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                long id = rs.getLong("id");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                String password = rs.getString("password");
                
                usuarios.add(new Usuario(id, nombre, email, password));
            }
        }
        return usuarios;
    }

    /**
     * Obtiene un usuario por email
     * @param email cadena de texto email de usuario
     * @return objeto usuario
     * @throws SQLException
     */
    public Usuario obtenerPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conn = dbConnection.connectDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        }
        return null;
    }
    
    /**
     * Autentica a un usuario usando email y password.
     * Regresa el usuario si las credenciales son correctas y null en caso contrario.
     * @param email correo electrónico parte de la credencial
     * @param password contraseña parte de la credencial
     * @return objeto usuario
     * @throws SQLException
     */
    public Usuario autenticar(String email, String password) throws SQLException {
        Usuario usr = obtenerPorEmail(email);
        
        if (usr != null && usr.getPassword().equals(password)) {
            return usr;
        }
        return null;
    }
}
