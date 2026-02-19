// Determine application context path at runtime and build API URLs.
// This makes the frontend work whether the app is deployed at `/` or at `/api` (or any other context path).
const CONTEXT_PATH = (function() {
    const p = window.location.pathname || '/';
    // If running at root ("/"), return empty string so URLs become "/books"; otherwise strip trailing slash
    if (p === '/') return '';
    return p.replace(/\/$/, '');
})();

const API_URL = `${CONTEXT_PATH}/books`;
const HEALTH_URL = `${CONTEXT_PATH}/health`;

// Global variable to store current book ID
let currentBookId = null;

// Check server connection
async function checkServerConnection() {
    try {
        console.log('Checking server connection at', HEALTH_URL);
        const response = await fetch(HEALTH_URL, {
            method: 'GET'
        });

        if (response.ok) {
            console.log('✅ Server is running');
            updateStatusIndicator(true);
        } else {
            console.warn('⚠️ Server responded with status:', response.status);
            updateStatusIndicator(false);
        }
    } catch (error) {
        console.error('❌ Cannot connect to server:', error);
        updateStatusIndicator(false);
    }
}

// Update status indicator
function updateStatusIndicator(isConnected) {
    const indicator = document.getElementById('statusIndicator');
    const statusText = document.getElementById('statusText');

    if (isConnected) {
        indicator.classList.remove('disconnected');
        indicator.classList.add('connected');
        statusText.textContent = '✅ Server Connected';
        statusText.style.color = '#28a745';
    } else {
        indicator.classList.remove('connected');
        indicator.classList.add('disconnected');
        statusText.textContent = '❌ Cannot connect to server. Make sure Spring Boot is running on http://localhost:8080';
        statusText.style.color = '#dc3545';
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    checkServerConnection();
    loadBooks();
    setupFormSubmit();
});

// Load all books
async function loadBooks() {
    try {
        console.log('Loading books from:', API_URL);
        const response = await fetch(API_URL);

        console.log('Response received:', response);
        console.log('Response status:', response.status);
        console.log('Response ok:', response.ok);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const books = await response.json();
        console.log('Books loaded:', books);
        displayBooks(books);
    } catch (error) {
        console.error('Error loading books:', error);
        console.error('Error message:', error.message);
        console.error('Error stack:', error.stack);
        showToast('Error loading books. Please check console and ensure backend is running.', 'error');
        document.getElementById('booksContainer').innerHTML = '<p class="loading">Failed to load books. Make sure the backend server is running on http://localhost:8080</p>';
    }
}

// Display books in grid
function displayBooks(books) {
    const container = document.getElementById('booksContainer');

    if (books.length === 0) {
        container.innerHTML = '<p class="loading">No books found. Add a new book to get started!</p>';
        return;
    }

    container.innerHTML = books.map(book => `
        <div class="book-card">
            <h3>${escapeHtml(book.title)}</h3>
            <div class="author">by ${escapeHtml(book.author)}</div>
            <div class="book-info">
                <strong>Genre:</strong> ${escapeHtml(book.genre)}
            </div>
            <div class="book-info">
                <strong>Published:</strong> ${formatDate(book.publishedDate)}
            </div>
            <div class="book-price">$${book.price.toFixed(2)}</div>
            <div class="book-actions">
                <button class="btn-view" onclick="viewBookDetails(${book.id})">👁️ View</button>
                <button class="btn-delete" onclick="promptDelete(${book.id})">🗑️ Delete</button>
            </div>
        </div>
    `).join('');
}

// View book details in modal
async function viewBookDetails(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const book = await response.json();
        currentBookId = id;

        const detailsHtml = `
            <div class="book-detail-modal">
                <h2>${escapeHtml(book.title)}</h2>
                <div class="book-detail-item">
                    <span class="book-detail-label">Author</span>
                    <span class="book-detail-value">${escapeHtml(book.author)}</span>
                </div>
                <div class="book-detail-item">
                    <span class="book-detail-label">Genre</span>
                    <span class="book-detail-value">${escapeHtml(book.genre)}</span>
                </div>
                <div class="book-detail-item">
                    <span class="book-detail-label">Price</span>
                    <span class="book-detail-value">$${book.price.toFixed(2)}</span>
                </div>
                <div class="book-detail-item">
                    <span class="book-detail-label">Published Date</span>
                    <span class="book-detail-value">${formatDate(book.publishedDate)}</span>
                </div>
                <div class="book-detail-item">
                    <span class="book-detail-label">Book ID</span>
                    <span class="book-detail-value">#${book.id}</span>
                </div>
            </div>
        `;

        document.getElementById('bookDetails').innerHTML = detailsHtml;
        document.getElementById('bookModal').classList.add('show');
    } catch (error) {
        console.error('Error fetching book details:', error);
        showToast('Error loading book details.', 'error');
    }
}

// Edit book
function editBook() {
    if (!currentBookId) return;

    fetch(`${API_URL}/${currentBookId}`)
        .then(res => res.json())
        .then(book => {
            closeModal();
            showSection('add-book', null);
            document.getElementById('formTitle').textContent = 'Edit Book';
            document.getElementById('submitBtn').textContent = 'Update Book';
            document.getElementById('bookId').value = book.id;
            document.getElementById('title').value = book.title;
            document.getElementById('author').value = book.author;
            document.getElementById('price').value = book.price;
            document.getElementById('genre').value = book.genre;
            document.getElementById('publishedDate').value = book.publishedDate;

            // Scroll to form
            document.querySelector('.form-container').scrollIntoView({ behavior: 'smooth' });
        })
        .catch(error => {
            console.error('Error fetching book for edit:', error);
            showToast('Error loading book for edit.', 'error');
        });
}

// Delete book
async function deleteBook() {
    if (!currentBookId) return;

    if (!confirm('Are you sure you want to delete this book?')) return;

    try {
        const response = await fetch(`${API_URL}/${currentBookId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        closeModal();
        showToast('Book deleted successfully!', 'success');
        loadBooks();
    } catch (error) {
        console.error('Error deleting book:', error);
        showToast('Error deleting book.', 'error');
    }
}

// Prompt delete confirmation
function promptDelete(id) {
    currentBookId = id;
    if (confirm('Are you sure you want to delete this book?')) {
        deleteBook();
    }
}

// Setup form submission
function setupFormSubmit() {
    document.getElementById('bookForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        await submitBook();
    });
}

// Submit book form
async function submitBook() {
    const bookId = document.getElementById('bookId').value;
    const book = {
        title: document.getElementById('title').value,
        author: document.getElementById('author').value,
        price: parseFloat(document.getElementById('price').value),
        genre: document.getElementById('genre').value,
        publishedDate: document.getElementById('publishedDate').value
    };

    try {
        let response;
        let method;
        let url = API_URL;

        if (bookId) {
            // Update existing book
            url = `${API_URL}/${bookId}`;
            method = 'PUT';
        } else {
            // Create new book
            method = 'POST';
        }

        console.log('Submitting book:', book);
        console.log('URL:', url);
        console.log('Method:', method);

        response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(book)
        });

        console.log('Response status:', response.status);
        console.log('Response ok:', response.ok);
        // Log response headers for debugging
        try {
            const headersArr = Array.from(response.headers.entries());
            console.log('Response headers:', headersArr);
        } catch (hErr) {
            console.warn('Could not read response headers:', hErr);
        }

        // Handle non-OK responses first and try to extract error details
        if (!response.ok) {
            let errMsg = `HTTP error! status: ${response.status}`;
            try {
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const errBody = await response.json();
                    errMsg += ` - ${JSON.stringify(errBody)}`;
                } else {
                    const text = await response.text();
                    if (text) errMsg += ` - ${text}`;
                }
            } catch (parseErr) {
                console.warn('Failed to parse error response body', parseErr);
            }
            throw new Error(errMsg);
        }

        // For successful responses, parse JSON only if present and content-type is JSON
        let responseData = null;
        try {
            const contentType = response.headers.get('content-type');
            if (response.status !== 204 && contentType && contentType.includes('application/json')) {
                // parse safely
                responseData = await response.json();
                console.log('Response data:', responseData);
            } else {
                console.log('No JSON body to parse or 204 No Content');
            }
        } catch (parseErr) {
            console.warn('Failed to parse success response body (ignored):', parseErr);
        }

        if (bookId) {
            showToast('Book updated successfully!', 'success');
        } else {
            showToast('Book added successfully!', 'success');
        }

        resetForm();
        // Wait for loadBooks to finish so any UI errors surface here
        await loadBooks();
        showSection('books-list', null);
    } catch (error) {
        console.error('Error submitting book:', error);
        // Show the specific error message if available to aid debugging
        const message = error && error.message ? error.message : 'Error saving book. Please try again.';
        showToast(message, 'error');
    }
}

// Reset form
function resetForm() {
    document.getElementById('bookForm').reset();
    document.getElementById('bookId').value = '';
    document.getElementById('formTitle').textContent = 'Add New Book';
    document.getElementById('submitBtn').textContent = 'Add Book';
}

// Show/hide sections
function showSection(sectionId, ev) {
    // fallback to window.event if available (some browsers provide a global event)
    ev = ev || (window.event || null);

    // Hide all sections
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });

    // Remove active class from all nav buttons
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    // Show selected section
    const section = document.getElementById(sectionId);
    if (section) {
        section.classList.add('active');
    }

    // Safely set active nav button using the event target if passed, otherwise fallback
    try {
        let activeBtn = null;
        const evTarget = ev?.target || ev?.srcElement || null;
        if (evTarget) {
            activeBtn = evTarget.closest ? evTarget.closest('.nav-btn') : evTarget;
        }
        if (activeBtn && activeBtn.classList && activeBtn.classList.contains('nav-btn')) {
            activeBtn.classList.add('active');
        } else {
            // Fallback: choose button by sectionId
            if (sectionId === 'books-list') {
                const first = document.querySelector('.nav-btn');
                if (first) first.classList.add('active');
            } else if (sectionId === 'add-book') {
                const btns = document.querySelectorAll('.nav-btn');
                if (btns && btns[1]) btns[1].classList.add('active');
            }
        }
    } catch (err) {
        console.warn('Could not set active nav button:', err);
    }

    // Reset form when switching to add-book section
    if (sectionId === 'add-book' && !document.getElementById('bookId').value) {
        resetForm();
    }
}

// Search books
async function searchBooks() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();

    if (!searchTerm.trim()) {
        loadBooks();
        return;
    }

    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const allBooks = await response.json();

        const filteredBooks = allBooks.filter(book =>
            book.title.toLowerCase().includes(searchTerm) ||
            book.author.toLowerCase().includes(searchTerm)
        );

        displayBooks(filteredBooks);
    } catch (error) {
        console.error('Error searching books:', error);
        showToast('Error searching books.', 'error');
    }
}

// Close modal
function closeModal() {
    document.getElementById('bookModal').classList.remove('show');
    currentBookId = null;
}

// Show toast notification
function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast show ${type}`;

    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Format date
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('en-US', options);
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

// Allow Enter key to trigger search
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                searchBooks();
            }
        });
    }
});
