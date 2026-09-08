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

<div id="deleteBookModal" class="modal-overlay" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Eliminar Libro</h3>
            <button type="button" class="modal-close" onclick="closeDeleteModal()">&times;</button>
        </div>
        
        <div id="deleteConfirmSection">
            <p style="color: #333; margin-bottom: 1rem;">¿Estás seguro de que deseas eliminar el libro <strong id="bookTitleConfirm"></strong>?</p>
            <p style="color: #999; font-size: 0.9em; margin-bottom: 1.5rem;">Esta acción no se puede deshacer.</p>
            <div class="form-actions">
                <button type="button" class="btn-cancel" onclick="closeDeleteModal()">Cancelar</button>
                <button type="button" class="btn-danger" onclick="confirmDeleteBook()">Eliminar Libro</button>
            </div>
        </div>
        
        <div id="activeLoansSection" style="display: none;">
            <p style="color: #d32f2f; font-weight: bold; margin-bottom: 1rem;">⚠️ No se puede eliminar este libro</p>
            <p style="color: #333; margin-bottom: 0.5rem;">El libro <strong id="bookTitleWarning"></strong> tiene <span id="activeLoansCount"></span> préstamo(s) activo(s).</p>
            <p style="color: #999; font-size: 0.9em; margin-bottom: 1.5rem;">Debes esperar a que todos los préstamos sean devueltos antes de poder eliminar este libro.</p>
            <div class="form-actions">
                <button type="button" class="btn-primary" onclick="closeDeleteModal()">Entendido</button>
            </div>
        </div>
    </div>
</div>

<script>
let currentBookId = null;
let currentBookTitle = null;

function openDeleteModal(bookId, bookTitle) {
    currentBookId = bookId;
    currentBookTitle = bookTitle;
    deleteBookModal.style.display = 'flex';
    catalogSection.classList.add('blurred');
    
    fetch('${pageContext.request.contextPath}/libro?action=checkLoans&libroId=' + bookId)
        .then(response => response.json())
        .then(data => {
            const deleteConfirmSection = document.getElementById('deleteConfirmSection');
            const activeLoansSection = document.getElementById('activeLoansSection');
            
            if (data.activeLoans > 0) {
                deleteConfirmSection.style.display = 'none';
                activeLoansSection.style.display = 'block';
                document.getElementById('bookTitleWarning').textContent = bookTitle;
                document.getElementById('activeLoansCount').textContent = data.activeLoans;
            } else {
                deleteConfirmSection.style.display = 'block';
                activeLoansSection.style.display = 'none';
                document.getElementById('bookTitleConfirm').textContent = bookTitle;
            }
            
            document.getElementById('deleteBookModal').style.display = 'flex';
        })
        .catch(error => {
            console.error('Error checking loans:', error);
            alert('Error al verificar préstamos. Por favor, intenta de nuevo.');
        });
}

function closeDeleteModal() {
    document.getElementById('deleteBookModal').style.display = 'none';
    currentBookId = null;
    currentBookTitle = null;
    deleteBookModal.style.display = 'none';
    catalogSection.classList.remove('blurred');
}

function confirmDeleteBook() {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '${pageContext.request.contextPath}/libro?action=eliminar';
    
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'id';
    input.value = currentBookId;
    
    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
}

window.addEventListener('click', function(event) {
    let deleteModal = document.getElementById('deleteBookModal');
    if (event.target === deleteModal) {
        deleteModal.style.display = 'none';
    }
});
</script>

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
                            <button type="button" class="btn-action btn-delete" onclick="openDeleteModal(${libro.id}, '${libro.titulo}')">
                                Eliminar
                            </button>
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