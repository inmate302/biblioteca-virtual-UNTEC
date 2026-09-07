<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Biblioteca Virtual - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/styles.css">
</head>
<body>
    <header class="dashboard-header">
        <div class="header-content">
            <h1>Bienvenido, <span class="username">${sessionScope.usuario.nombre}</span></h1>
        </div>
    </header>

    <nav class="dashboard-nav">
        <div class="nav-left">
            <a href="${pageContext.request.contextPath}/dashboard?view=catalog" class="nav-btn">
                Catálogo
            </a>
            <a href="${pageContext.request.contextPath}/dashboard?view=loans" class="nav-btn">
                Préstamos
            </a>
        </div>
        <div class="nav-right">
            <form action="${pageContext.request.contextPath}/logout" method="GET" style="margin: 0;">
                <button type="submit" class="nav-btn logout-btn">
                    Cerrar Sesión
                </button>
            </form>
        </div>
    </nav>

    <main class="dashboard-main">
        <c:choose>
            <c:when test="${view == 'catalog'}">
                <jsp:include page="/WEB-INF/views/catalog.jsp" />
            </c:when>
            <c:when test="${view == 'loans'}">
                <jsp:include page="/WEB-INF/views/loans.jsp" />
            </c:when>
            <c:otherwise>
                <section class="welcome-section">
                    <div class="welcome-card">
                        <h2>Bienvenido a Biblioteca Virtual</h2>
                        <p>Utiliza el menú superior para explorar nuestro catálogo <br>
                            o administrar los préstamos de los usuarios.</p>
                        </div>
                    </div>
                </section>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>
