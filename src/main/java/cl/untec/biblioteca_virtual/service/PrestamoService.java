package cl.untec.biblioteca_virtual.service;

import cl.untec.biblioteca_virtual.dao.PrestamoDAO;
import cl.untec.biblioteca_virtual.dao.LibroDAO;
import cl.untec.biblioteca_virtual.model.Libro;
import cl.untec.biblioteca_virtual.model.Prestamo;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PrestamoService {
    
    private PrestamoDAO prestamoDAO;
    private LibroDAO libroDAO;
    
    public PrestamoService() {
        this.prestamoDAO = new PrestamoDAO();
        this.libroDAO = new LibroDAO();
    }

    /**
     * Crea un nuevo registro de prestamo con validación
     * y administra stock de libro.
     * @param usuarioId id de usuario solicitante
     * @param libroId id de libro a solicitar
     * @param tituloLibro titulo de libro a solicitar
     * @param isbnLibro identificador de libro a solicitar
     * @return si validación falla regresa false, de lo contrario crea objeto prestamo
     * y regresa true
     * @throws SQLException
     */
    public boolean prestarLibro(long usuarioId, long libroId, String tituloLibro, String isbnLibro) throws SQLException {
        // Valida ingreso
        if (usuarioId <= 0 || libroId <= 0 || 
            tituloLibro == null || tituloLibro.trim().isEmpty() ||
            isbnLibro == null || isbnLibro.trim().isEmpty()) {
            return false;
        }
        
        // Revisa si el libro está disponible
        Libro lib = libroDAO.obtenerPorId(libroId);
        if (lib == null || !lib.isDisponible()) {
            return false;
        }
        
        // Crea registro de préstamo (objeto prestamo)
        Prestamo prestamo = new Prestamo(
            0,
            usuarioId,
            libroId,
            tituloLibro,
            isbnLibro,
            LocalDate.now(),
            null,
            false
        );
        
        // Inserta prestamo y decrementa stock
        prestamoDAO.insertar(prestamo);
        libroDAO.disminuirStock(libroId);
        
        return true;
    }
    
    /**
     * Procesa la devolución de un prestamo
     * @param prestamoId id de prestamo
     * @return si id de prestamo no es válida o ya esta devuelto regresa false
     * de lo contrario marca como devuelto y regresa true
     * @throws SQLException
     */
    public boolean devolverLibro(long prestamoId) throws SQLException {
        Prestamo prestamo = prestamoDAO.obtenerPorId(prestamoId);
        
        if (prestamo == null || prestamo.isDevuelto()) {
            return false;
        }
        
        // Marca como devuelto
        prestamoDAO.marcarDevuelto(prestamoId, LocalDate.now());
        
        // Incrementa stock
        libroDAO.aumentarStock(prestamo.getLibroId());
        
        return true;
    }
    
    /**
     * Obtiene lista de prestamos por id
     * @param id id de prestamo
     * @return lista de objetos prestamo
     * @throws SQLException
     */
    public Prestamo obtenerPorId(long id) throws SQLException {
        return prestamoDAO.obtenerPorId(id);
    }
    
    /**
     * Obtiene todos los prestamos de un usuario específico ordenados por el más reciente
     * @param usuarioId id de usuario
     * @return lista de objetos prestamo
     * @throws SQLException
     */
    public List<Prestamo> obtenerHistorialUsuario(long usuarioId) throws SQLException {
        return prestamoDAO.obtenerPorUsuario(usuarioId);
    }
    

    /**
     * Obtiene lista de prestamos activos de usuario específico
     * @param usuarioId ide de usuario
     * @return lista de objetos prestamo
     * @throws SQLException
     */
    public List<Prestamo> obtenerPrestamosActivos(long usuarioId) throws SQLException {
        return prestamoDAO.obtenerPrestamosActivosPorUsuario(usuarioId);
    }
    
    /**
     * Obtiene lista de prestamos de un libro específico
     * @param libroId id de libro
     * @return lista de objetos prestamo
     * @throws SQLException
     */
    public List<Prestamo> obtenerPrestamosLibro(long libroId) throws SQLException {
        return prestamoDAO.obtenerPorLibro(libroId);
    }
    
    /**
     * Obtiene todos los prestamos en sistema.
     * @return lista de objetos prestamo
     * @throws SQLException
     */
    public List<Prestamo> obtenerTodos() throws SQLException {
        return prestamoDAO.obtenerTodos();
    }

    /**
     * Cuenta la cantidad de prestamos activos de un usuario
     * @param usuarioId id de usuario
     * @return número entero de cantidad de prestamos activos
     * @throws SQLException
     */
    public int contarPrestamosActivos(long usuarioId) throws SQLException {
        return prestamoDAO.obtenerPrestamosActivosPorUsuario(usuarioId).size();
    }
    
    /**
     * Elimina un registro de prestamo
     * @param prestamoId id de prestamo
     * @return si no encuentra id regresa false, de lo contrario elimina prestamo
     * y regresa true
     * @throws SQLException
     */
    public boolean eliminarPrestamo(long prestamoId) throws SQLException {
        Prestamo prestamo = prestamoDAO.obtenerPorId(prestamoId);
        if (prestamo == null) {
            return false;
        }
        prestamoDAO.eliminar(prestamoId);
        return true;
    }
}
