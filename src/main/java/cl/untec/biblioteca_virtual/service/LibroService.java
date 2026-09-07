package cl.untec.biblioteca_virtual.service;

import cl.untec.biblioteca_virtual.dao.LibroDAO;
import cl.untec.biblioteca_virtual.model.Libro;
import net.creativecouple.validation.isbn.ISBN;
import net.creativecouple.validation.isbn.ISBNValidator;
import static net.creativecouple.validation.isbn.ISBNValidatorBuilder.HyphenationOption.CORRECT_OR_NONE;


import java.sql.SQLException;
import java.util.List;

public class LibroService {
    
    private LibroDAO libroDAO;
    
    public LibroService() {
        this.libroDAO = new LibroDAO();
    }
    
    /**
     * Agrega un nuevo libro al catálogo después de validación.
     * @param nuevoLibro objeto libro
     * @return si validaciones fallan regresa false de lo contrario regresa true
     * @throws SQLException
     */
    public boolean agregarLibro(Libro nuevoLibro) throws SQLException {
        ISBNValidator validator = ISBNValidator.isbn13().hyphenation(CORRECT_OR_NONE);
        // Valida ingreso
        if (nuevoLibro == null || 
            nuevoLibro.getIsbn() == null || nuevoLibro.getIsbn().trim().isEmpty() ||
            nuevoLibro.getTitulo() == null || nuevoLibro.getTitulo().trim().isEmpty() ||
            nuevoLibro.getAutor() == null || nuevoLibro.getAutor().trim().isEmpty()) {
            return false;
        }
        //Stock no debe ser negativo
        if (nuevoLibro.getStock() < 0) {
            return false;
        }
        
        //Revisa si ISBN es válido
        if (!validator.test(nuevoLibro.getIsbn())){
            throw new IllegalArgumentException("ISBN inválido");
        }

        //Revisa si ISBN ya existe   
        if (libroDAO.obtenerPorIsbn(nuevoLibro.getIsbn()) != null) {
            return false;
        }

        //Tomamos el isbn ingresado y reingresamos en formato correcto.
        String isbn = ISBN.valueOf(nuevoLibro.getIsbn()).toString();
        nuevoLibro.setIsbn(isbn);
        libroDAO.insertar(nuevoLibro);
        return true;
    }
    
    /**
     * Obtiene libro por ID
     * @param id id de libro
     * @return objeto libro
     * @throws SQLException
     */
    public Libro obtenerPorId(long id) throws SQLException {
        return libroDAO.obtenerPorId(id);
    }

    /**
     * Obtiene libro por ISBN
     * @param isbn cadena de texto de identificador de libro
     * @return lista de objetos libro
     * @throws SQLException
     */
    public Libro obtenerPorIsbn(String isbn) throws SQLException {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        return libroDAO.obtenerPorIsbn(isbn);
    }
    
    /**
     * Busca libros por título
     * @param titulo cadena de texto de título de libro
     * @return lista de objetos libro
     * @throws SQLException
     */
    public List<Libro> buscarPorTitulo(String titulo) throws SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            return libroDAO.obtenerTodos();
        }
        return libroDAO.buscarPorTitulo(titulo);
    }
    
    /**
     * Busca libros por autor
     * @param autor cadena de texto con nombre de autor
     * @return lista de objetos libro
     * @throws SQLException
     */
    public List<Libro> buscarPorAutor(String autor) throws SQLException {
        if (autor == null || autor.trim().isEmpty()) {
            return libroDAO.obtenerTodos();
        }
        return libroDAO.buscarPorAutor(autor);
    }
    
    /**
     * Obtiene todos los libros
     * @return lista de objetos libro
     * @throws SQLException
     */
    public List<Libro> obtenerTodos() throws SQLException {
        return libroDAO.obtenerTodos();
    }
    
    /**
     * Obtiene sólo aquellos libros que estén disponibles
     * (que su stock sea mayor a cero)
     * @return lista de objetos libro
     * @throws SQLException
     */
    public List<Libro> obtenerDisponibles() throws SQLException {
        return libroDAO.obtenerDisponibles();
    }
    
    /**
     * Verifica si libro está disponible para préstamo
     * @param libroId id de libro a verificar
     * @return objeto libro
     * @throws SQLException
     */
    public boolean esDisponible(long libroId) throws SQLException {
        Libro lib = libroDAO.obtenerPorId(libroId);
        return lib != null && lib.isDisponible();
    }
    
    /**
     * Entrega libro actualizado en DAO
     * @param libroActualizado 
     * @return false si campos están vacíos o no válidos de lo contrario regresa true
     * @throws SQLException
     */
    public boolean actualizarLibro(Libro libroActualizado) throws SQLException {
        if (libroActualizado == null || libroActualizado.getId() <= 0) {
            return false;
        }
        
        Libro libroExistente = libroDAO.obtenerPorId(libroActualizado.getId());
        if (libroExistente == null) {
            return false;
        }
        
        if (libroActualizado.getTitulo() == null || libroActualizado.getTitulo().trim().isEmpty() ||
            libroActualizado.getAutor() == null || libroActualizado.getAutor().trim().isEmpty()) {
            return false;
        }
        
        libroDAO.actualizar(libroActualizado);
        return true;
    }
    
    /**
     * Agrega stock a un libro
     * @param libroId id de libro a agregar stock
     * @param cantidad número entero int
     * @throws SQLException
     */
    public void agregarStock(long libroId, int cantidad) throws SQLException {
        Libro lib = libroDAO.obtenerPorId(libroId);
        if (lib != null && cantidad > 0) {
            lib.setStock(lib.getStock() + cantidad);
            libroDAO.actualizar(lib);
        }
    }
    
    /**
     * Borra un libro del catálogo después de verificar si existe préstamo.
     * @param libroId id de libro de prestamo a borrar
     * @return true si prestamo fue borrado con éxito de lo contrario regresa false
     * @throws SQLException
     */
    public boolean eliminarLibro(long libroId) throws SQLException {
        Libro lib = libroDAO.obtenerPorId(libroId);
        if (lib == null) {
            return false;
        }

        if (libroDAO.tienePrestamo(libroId)) {
            throw new IllegalArgumentException("No se puede eliminar un libro con préstamos");
        }

        libroDAO.eliminar(libroId);
        return true;
    }

}
