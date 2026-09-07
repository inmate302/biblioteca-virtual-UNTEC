package cl.untec.biblioteca_virtual.service;

import cl.untec.biblioteca_virtual.dao.UsuarioDAO;
import cl.untec.biblioteca_virtual.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    
    private UsuarioDAO usuarioDAO;
    
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Autentica a usuario por email y password
     * @return objeto usuario si la autenticación es exitosa
     * de lo contrario regresa null
     */
    public Usuario autenticar(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        return usuarioDAO.autenticar(email, password);
    }

    /**
     * Obtiene a todos los usuarios
     * @return lista de objetos usuario
     * @throws SQLException
     */
    public List<Usuario> obtenerUsuarios() throws SQLException {
        try {
            return usuarioDAO.obtenerUsuarios();
        } catch (SQLException e) {
            e.printStackTrace(); 
            throw new SQLException("No pudo obtener usuarios de la base de datos.");
        }
    }

    /**
     * Obtiene usuario por email
     * @param email email de usuario
     * @return regresa objeto usuario
     * @throws SQLException
     */
    public Usuario obtenerPorEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return usuarioDAO.obtenerPorEmail(email);
    }

}
