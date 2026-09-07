package cl.untec.biblioteca_virtual.controller;

import cl.untec.biblioteca_virtual.service.PrestamoService;
import cl.untec.biblioteca_virtual.service.LibroService;
import cl.untec.biblioteca_virtual.model.Prestamo;
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

@WebServlet("/prestamos")
public class PrestamosController extends HttpServlet {
    
    private PrestamoService prestamoService;
    private LibroService libroService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.prestamoService = new PrestamoService();
        this.libroService = new LibroService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }
        
        try {
            List<Prestamo> prestamos = prestamoService.obtenerTodos();
            request.setAttribute("prestamos", prestamos);
            request.getRequestDispatcher("/WEB-INF/views/loans.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al acceder a la base de datos");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            if (action != null && action.equals("prestar")) {
                prestarLibroAUsuario(request, response);
            } else if (action != null && action.equals("devolver")) {
                devolverLibro(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void prestarLibroAUsuario(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, ServletException {
        
        String libroIdParam = request.getParameter("libroId");
        String usuarioIdParam = request.getParameter("usuarioId");
        String mensaje = "";
        String redirectUrl = "dashboard?view=catalog";
        
        if (libroIdParam == null || usuarioIdParam == null) {
            mensaje = "Parámetros inválidos";
            response.sendRedirect(redirectUrl + "&error=" + mensaje);
            return;
        }
        
        try {
            long libroId = Long.parseLong(libroIdParam);
            long usuarioId = Long.parseLong(usuarioIdParam);
            
            if (prestamoService.contarPrestamosActivos(usuarioId) >= 5) {
                mensaje = "El usuario ya ha alcanzado el máximo de préstamos activos (5 libros)";
                response.sendRedirect(redirectUrl + "&error=" + mensaje);
                return;
            }
            
            Libro libro = libroService.obtenerPorId(libroId);
            if (libro == null) {
                mensaje = "El libro no existe";
                response.sendRedirect(redirectUrl + "&error=" + mensaje);
                return;
            }
            
            if (!libro.isDisponible()) {
                mensaje = "El libro no está disponible";
                response.sendRedirect(redirectUrl + "&error=" + mensaje);
                return;
            }
            
            if (prestamoService.prestarLibro(usuarioId, libroId, libro.getTitulo(), libro.getIsbn())) {
                mensaje = "Libro prestado exitosamente a " + libro.getTitulo();
                response.sendRedirect(redirectUrl + "&success=" + mensaje);
            } else {
                mensaje = "Error al procesar el préstamo";
                response.sendRedirect(redirectUrl + "&error=" + mensaje);
            }
        } catch (NumberFormatException e) {
            mensaje = "Parámetros inválidos";
            response.sendRedirect(redirectUrl + "&error=" + mensaje);
        }
    }
    
    private void devolverLibro(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String prestamoIdParam = request.getParameter("prestamoId");
        String redirectUrl = "dashboard?view=loans";
        
        if (prestamoIdParam == null) {
            response.sendRedirect(redirectUrl + "&error=ID de préstamo inválido");
            return;
        }
        
        try {
            long prestamoId = Long.parseLong(prestamoIdParam);
            
            if (prestamoService.devolverLibro(prestamoId)) {
                response.sendRedirect(redirectUrl + "&success=Libro devuelto exitosamente");
            } else {
                response.sendRedirect(redirectUrl + "&error=Error al devolver el libro");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectUrl + "&error=ID de préstamo inválido");
        }
    }
}
