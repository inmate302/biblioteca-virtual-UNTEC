<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="addBookModal" class="modal-overlay" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Agregar Nuevo Libro</h3>
            <button id="closeModalBtn" class="modal-close">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/libro?action=agregar" method="POST" class="book-form">
            <div class="form-group">
                <label for="isbn">ISBN:</label>
                <input type="text" id="isbn" name="isbn" placeholder="ej: 978-1-234-56789-0" required>
            </div>
            <div class="form-group">
                <label for="titulo">Título:</label>
                <input type="text" id="titulo" name="titulo" placeholder="Título del libro" required>
            </div>
            <div class="form-group">
                <label for="autor">Autor:</label>
                <input type="text" id="autor" name="autor" placeholder="Nombre del autor" required>
            </div>
            <div class="form-group">
                <label for="stock">Stock:</label>
                <input type="number" id="stock" name="stock" placeholder="Cantidad" min="1" required>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn-submit">Agregar Libro</button>
                <button type="button" id="cancelBtn" class="btn-cancel">Cancelar</button>
            </div>
        </form>
    </div>
</div>

<div id="loanBookModal" class="modal-overlay" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Prestar Libro</h3>
            <button id="closeLoanModalBtn" class="modal-close">&times;</button>
        </div>
        <form id="loanForm" action="${pageContext.request.contextPath}/prestamos?action=prestar" method="POST" class="book-form">
            <input type="hidden" id="loanLibroId" name="libroId">
            <div class="form-group">
                <label for="usuarioSelect">Prestar a:</label>
                <select id="usuarioSelect" name="usuarioId" required>
                    <option value="">-- Seleccionar usuario --</option>
                    <c:forEach var="usuario" items="${usuarios}">
                        <c:if test="${usuario.id != 1}">
                            <option value="${usuario.id}">${usuario.nombre}</option>
                        </c:if>
                    </c:forEach>
                </select>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn-submit">Prestar</button>
                <button type="button" id="cancelLoanBtn" class="btn-cancel">Cancelar</button>
            </div>
        </form>
    </div>
</div>

<div class="catalog-section">
    <div class="catalog-header">
        <h2>Catálogo de Libros</h2>
        <button id="addBookBtn" class="btn-primary">+ Agregar Nuevo Libro</button>
    </div>

    <div class="books-table-container">
        <h3>Libros Disponibles</h3>
        <table class="books-table">
            <thead>
                <tr>
                    <th class="isbn-col">ISBN</th>
                    <th>Título</th>
                    <th>Autor</th>
                    <th class="stock-col">Stock</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="libro" items="${libros}">
                    <tr class="${libro.stock > 0 ? 'disponible' : 'agotado'}">
                        <td class="isbn-col">${libro.isbn}</td>
                        <td>${libro.titulo}</td>
                        <td>${libro.autor}</td>
                        <td class="stock-col">${libro.stock}</td>
                        <td>
                            <span class="status-badge ${libro.stock > 0 ? 'status-available' : 'status-unavailable'}">
                                ${libro.stock > 0 ? 'Disponible' : 'Agotado'}
                            </span>
                        </td>
                        <td style="display: flex; gap: 6px; align-items: center; white-space: nowrap;">
                            <c:if test="${libro.stock > 0}">
                                <button type="button" class="btn-action btn-loan" data-libro-id="${libro.id}">Prestar</button>
                            </c:if>
                            <form method="post" action="${pageContext.request.contextPath}/libro?action=eliminar">
                                <input type="hidden" name="id" value="${libro.id}">
                                <button type="submit" class="btn-action btn-delete" onclick="return confirm('¿Está seguro de que desea eliminar este libro? No se podrá recuperar.');">
                                    Eliminar
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty libros}">
            <p class="no-books">No hay libros en el catálogo aún.</p>
        </c:if>
    </div>
</div>

<script>
    const addBookBtn = document.getElementById('addBookBtn');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const addBookModal = document.getElementById('addBookModal');
    const catalogSection = document.querySelector('.catalog-section');

    function openAddBookModal() {
        addBookModal.style.display = 'flex';
        catalogSection.classList.add('blurred');
    }

    function closeAddBookModal() {
        addBookModal.style.display = 'none';
        catalogSection.classList.remove('blurred');
    }

    addBookBtn.addEventListener('click', openAddBookModal);
    closeModalBtn.addEventListener('click', closeAddBookModal);
    cancelBtn.addEventListener('click', closeAddBookModal);

    addBookModal.addEventListener('click', (e) => {
        if (e.target === addBookModal) {
            closeAddBookModal();
        }
    });

    const loanBookModal = document.getElementById('loanBookModal');
    const closeLoanModalBtn = document.getElementById('closeLoanModalBtn');
    const cancelLoanBtn = document.getElementById('cancelLoanBtn');
    const loanLibroIdInput = document.getElementById('loanLibroId');
    const loanButtons = document.querySelectorAll('.btn-loan');

    function openLoanModal(libroId) {
        loanLibroIdInput.value = libroId;
        loanBookModal.style.display = 'flex';
        catalogSection.classList.add('blurred');
    }

    function closeLoanModal() {
        loanBookModal.style.display = 'none';
        catalogSection.classList.remove('blurred');
    }

    loanButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const libroId = btn.getAttribute('data-libro-id');
            openLoanModal(libroId);
        });
    });

    closeLoanModalBtn.addEventListener('click', closeLoanModal);
    cancelLoanBtn.addEventListener('click', closeLoanModal);

    loanBookModal.addEventListener('click', (e) => {
        if (e.target === loanBookModal) {
            closeLoanModal();
        }
    });
</script>
