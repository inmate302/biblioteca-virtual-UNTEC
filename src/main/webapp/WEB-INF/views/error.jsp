<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Error - Biblioteca Virtual</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/styles.css">
</head>
<body>
    <div style="display: flex; justify-content: center; align-items: center; min-height: 100vh; background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);">
        <div class="modal-content" style="width: 100%; max-width: 500px;">
            <div class="modal-header">
                <h3>Error</h3>
            </div>
            <div style="padding: 20px; text-align: center;">
                <p style="color: #c62828; font-weight: 500; margin-bottom: 16px;">
                    ${error}
                </p>
                <a href="${pageContext.request.contextPath}/dashboard" class="btn-submit">Volver al Dashboard</a>
            </div>
        </div>
    </div>
</body>
</html>
 