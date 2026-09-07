<%@ page import="cl.untec.biblioteca_virtual.model.Prestamo" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>



    
<div class="loans-section">
    <div class="loans-header">
        <h2>Préstamos de Libros</h2>
            <div class="filter-buttons" style="margin-bottom: 20px;">
                <button class="btn-filter active" data-filter="todos">Todos</button>
                <button class="btn-filter" data-filter="activos">Activos</button>
                <button class="btn-filter" data-filter="historial">Devueltos</button>
                <button class="btn-filter" data-filter="vencidos">Vencidos</button>
            </div>
</div>

<div class="loans-table-container">
    <h3></h3>
    <table class="loans-table">
        <thead>
            <tr>
                <th>Usuario ID</th>
                <th>Título</th>
                <th>ISBN</th>
                <th>Fecha Préstamo</th>
                <th>Fecha Devolución</th>
                <th>Estado</th>
                <th>Acción</th>
            </tr>
        </thead>
        <tbody id="loansTableBody">
            <c:forEach var="prestamo" items="${prestamos}">
                <%
                    java.time.LocalDate hoy = java.time.LocalDate.now();
                    cl.untec.biblioteca_virtual.model.Prestamo p = (cl.untec.biblioteca_virtual.model.Prestamo) pageContext.getAttribute("prestamo");
                    
                    java.time.LocalDate fechaPrestamo = p.getFechaPrestamo();
                    java.time.LocalDate fechaVenc = fechaPrestamo.plusDays(15);
                    boolean devuelto = p.isDevuelto();
                    
                    // Convert LocalDate to java.util.Date for fmt:formatDate
                    java.util.Date fechaPrestamoDate = java.sql.Date.valueOf(fechaPrestamo);
                    java.util.Date fechaVencDate = (fechaVenc != null) ? java.sql.Date.valueOf(fechaVenc) : null;
                    
                    boolean isVencido = !devuelto && fechaVenc != null && fechaVenc.isBefore(hoy);
                    
                    // Determine status for data attribute
                    String status = devuelto ? "historial" : (isVencido ? "vencidos" : "activos");
                    
                    pageContext.setAttribute("fechaPrestamoDate", fechaPrestamoDate);
                    pageContext.setAttribute("fechaVencDate", fechaVencDate);
                    pageContext.setAttribute("isVencido", isVencido);
                    pageContext.setAttribute("devuelto", devuelto);
                    pageContext.setAttribute("status", status);
                %>
                
                <tr class="loan-row" data-status="${status}">
                    <td>${prestamo.usuarioId}</td>
                    <td><strong>${prestamo.tituloLibro}</strong></td>
                    <td class="isbn-col">${prestamo.isbnLibro}</td>
                    <td>
                        <fmt:formatDate value="${fechaPrestamoDate}" pattern="dd/MM/yyyy" type="date" />
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${fechaVencDate != null}">
                                <fmt:formatDate value="${fechaVencDate}" pattern="dd/MM/yyyy" type="date" />
                            </c:when>
                            <c:otherwise>
                                <span style="color: #999; font-style: italic;">Pendiente</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${devuelto}">
                                <span class="status-badge status-returned">Devuelto</span>
                            </c:when>
                            <c:when test="${isVencido}">
                                <span class="status-badge status-overdue">Vencido</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-badge status-active">Activo</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${!devuelto}">
                            <form action="${pageContext.request.contextPath}/prestamos?action=devolver" method="POST" style="display: inline;">
                                <input type="hidden" name="prestamoId" value="${prestamo.id}">
                                <button type="submit" class="btn-action btn-return">Devolver</button>
                            </form>
                        </c:if>
                        <c:if test="${devuelto}">
                            <span class="action-placeholder">—</span>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const filterButtons = document.querySelectorAll('.btn-filter');
    const loanRows = document.querySelectorAll('.loan-row');
    
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            const filter = this.getAttribute('data-filter');
            filterButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            loanRows.forEach(row => {
                if (filter === 'todos') {
                    row.style.display = '';
                } else {
                    const rowStatus = row.getAttribute('data-status');
                    row.style.display = rowStatus === filter ? '' : 'none';
                }
            });
        });
    });
});
</script>