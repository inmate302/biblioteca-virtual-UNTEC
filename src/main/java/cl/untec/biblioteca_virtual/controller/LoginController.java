package cl.untec.biblioteca_virtual.controller;

import cl.untec.biblioteca_virtual.service.UsuarioService;
import cl.untec.biblioteca_virtual.model.Usuario;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    
    private UsuarioService usuarioService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioService = new UsuarioService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            response.sendRedirect("dashboard");
            return;
        }
        
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try {
            Usuario usuarioAutenticado = usuarioService.autenticar(email, password);

            if (usuarioAutenticado != null) {
                if ("admin@untec.cl".equals(usuarioAutenticado.getEmail())) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("usuario", usuarioAutenticado);
                    session.setMaxInactiveInterval(30 * 60);
                    response.sendRedirect("dashboard");
                } else {
                    request.setAttribute("error", "Acceso no autorizado");
                    request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Email o contraseña incorrectos");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error en la base de datos");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
