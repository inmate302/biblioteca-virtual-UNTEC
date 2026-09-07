package cl.untec.biblioteca_virtual.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cl.untec.biblioteca_virtual.model.Libro;
import cl.untec.biblioteca_virtual.model.Usuario;
import cl.untec.biblioteca_virtual.model.Prestamo;
import cl.untec.biblioteca_virtual.service.LibroService;
import cl.untec.biblioteca_virtual.service.PrestamoService;
import cl.untec.biblioteca_virtual.service.UsuarioService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    private LibroService libroService = new LibroService();
    private UsuarioService usuarioService = new UsuarioService();
    private PrestamoService prestamoService = new PrestamoService();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String view = request.getParameter("view");

        try {
            if ("catalog".equals(view)) {
                cargarCatalogo(request, response);
                List<Usuario> usuarios = usuarioService.obtenerUsuarios();
                request.setAttribute("usuarios", usuarios);
            } else if ("loans".equals(view)) {
                cargarPrestamos(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }

        request.setAttribute("view", view);

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }

    private void cargarCatalogo(HttpServletRequest request, HttpServletResponse response) 
        throws SQLException {
        List<Libro> libros = this.libroService.obtenerTodos();
        request.setAttribute("libros", libros);

        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        request.setAttribute("usuarios", usuarios);
    }

    private void cargarPrestamos(HttpServletRequest request, HttpServletResponse response)
        throws SQLException {
        List<Prestamo> prestamos = this.prestamoService.obtenerTodos();
        request.setAttribute("prestamos", prestamos);
    }

}