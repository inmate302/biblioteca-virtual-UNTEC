package cl.untec.biblioteca_virtual.controller;

import cl.untec.biblioteca_virtual.service.LibroService;
import cl.untec.biblioteca_virtual.service.PrestamoService;
import cl.untec.biblioteca_virtual.dao.PrestamoDAO;
import cl.untec.biblioteca_virtual.model.Libro;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet({"/catalog", "/libro"})
public class LibroController extends HttpServlet {
    
    private LibroService libroService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.libroService = new LibroService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        PrestamoDAO prestamo = new PrestamoDAO();        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            if ("checkLoans".equals(action)) {
                long libroId = Long.parseLong(request.getParameter("libroId"));
                int activeLoans = prestamo.obtenerPorLibro(libroId).size();
                
                response.setContentType("application/json");
                response.getWriter().write("{\"activeLoans\": " + activeLoans + "}");
                return;
            } else if (action == null || action.equals("listar")) {
                listarLibros(request, response);
            } else if (action.equals("disponibles")) {
                listarDisponibles(request, response);
            } else {
                listarLibros(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al acceder a la base de datos");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println("POST action: " + action);
        
        if ("agregar".equals(action)) {
            try {
                String isbn = request.getParameter("isbn");
                String titulo = request.getParameter("titulo");
                String autor = request.getParameter("autor");
                String stockStr = request.getParameter("stock");
                
                System.out.println("Form data - ISBN: " + isbn + ", Titulo: " + titulo + 
                                ", Autor: " + autor + ", Stock: " + stockStr);
                
                int stock = Integer.parseInt(stockStr);
                long id = 0;
                Libro nuevoLibro = new Libro(id, isbn, titulo, autor, stock);
                
                boolean success = libroService.agregarLibro(nuevoLibro);
                System.out.println("agregarLibro returned: " + success);

                List<Libro> libros = libroService.obtenerTodos();
                System.out.println("Books after insert: " + libros.size());
                for (Libro l : libros) {
                    System.out.println("  - " + l.getTitulo());
                }

                
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/dashboard?view=catalog");
                } else {
                    request.setAttribute("error", "No se pudo agregar el libro. Verifique los datos.");
                    request.getRequestDispatcher("/catalog.jsp").forward(request, response);
                }
            } catch (SQLException | NumberFormatException e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "Error: " + e.getMessage());
                request.getRequestDispatcher("/catalog.jsp").forward(request, response);
            }
        } else if (action.equals("eliminar")) {
            try {
                    eliminarLibro(request, response);  
            } catch (SQLException | IllegalArgumentException e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Error: " + e.getMessage());
                    request.getRequestDispatcher("/catalog.jsp").forward(request, response);
            }    
        }
    }
    
    private void listarLibros(HttpServletRequest request, HttpServletResponse response) 
        throws SQLException, ServletException, IOException {
        List<Libro> libros = libroService.obtenerTodos();
        request.setAttribute("libros", libros);
        request.getRequestDispatcher("/WEB-INF/views/catalog.jsp").forward(request, response);
    }

    private void listarDisponibles(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        List<Libro> disponibles = libroService.obtenerDisponibles();
        request.setAttribute("libros", disponibles);
        request.getRequestDispatcher("/WEB-INF/views/libros/disponibles.jsp").forward(request, response);
    }
      
    public void eliminarLibro(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        PrestamoDAO prestamo = new PrestamoDAO();        
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            
            int activeLoans = prestamo.obtenerPorLibro(id).size();
            
            if (activeLoans > 0) {
                request.getSession().setAttribute("error", 
                    "No se puede eliminar este libro. Tiene " + activeLoans + " préstamo(s) activo(s).");
                response.sendRedirect(request.getContextPath() + "/dashboard?view=catalog");
                return;
            }
            
            libroService.eliminarLibro(id);
            
            request.getSession().setAttribute("message", "Libro eliminado exitosamente");
            response.sendRedirect(request.getContextPath() + "/dashboard?view=catalog");  
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "ID de libro inválido");
            response.sendRedirect(request.getContextPath() + "/dashboard?view=catalog");
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard?view=catalog");
        } catch (SQLException e) {
            request.getSession().setAttribute("error", "Error en la base de datos");
            response.sendRedirect(request.getContextPath() + "/dashboard?view=catalog");
        }
    }

}
