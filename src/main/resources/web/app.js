const API_BASE = 'http://localhost:9001';

// State management
let currentIndex = '';
let allIndexes = [];
let allDocuments = [];

document.addEventListener('DOMContentLoaded', () => {
    initializeTabs();
    initializeSearchListeners();
    initializeDocumentListeners();
    initializeIndexListeners();
    initializeAddDocumentForm();

    // Load initial data
    loadIndexes();
    loadDocuments();
});

// Tab Management
function initializeTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tabName = button.getAttribute('data-tab');

            // Remove active class from all tabs
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            // Add active class to clicked tab
            button.classList.add('active');
            document.getElementById(`${tabName}-tab`).classList.add('active');

            // Reload data when switching tabs
            if (tabName === 'documents') {
                loadDocuments();
            } else if (tabName === 'indexes') {
                loadIndexes();
            }
        });
    });
}

// Search Functionality
function initializeSearchListeners() {
    const searchBtn = document.getElementById('searchBtn');
    const searchQuery = document.getElementById('searchQuery');

    searchBtn.addEventListener('click', performSearch);
    searchQuery.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') performSearch();
    });
}

async function performSearch() {
    const query = document.getElementById('searchQuery').value.trim();
    const indexSelect = document.getElementById('indexSelect').value;
    const limit = document.getElementById('limitSelect').value;

    if (!query) {
        showMessage('الرجاء إدخال كلمة البحث - Please enter a search query', 'error');
        return;
    }

    const resultsSection = document.getElementById('resultsSection');
    const resultsDiv = document.getElementById('searchResults');
    const resultsCount = document.getElementById('resultsCount');

    resultsDiv.innerHTML = '<div class="loading">جاري البحث... Searching...</div>';
    resultsSection.style.display = 'block';

    try {
        let url = `${API_BASE}/search?query=${encodeURIComponent(query)}&limit=${limit}`;
        if (indexSelect) {
            url += `&index=${encodeURIComponent(indexSelect)}`;
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('Search failed');

        const data = await response.json();
        displayResults(data, resultsCount);
    } catch (error) {
        console.error('Search error:', error);
        resultsDiv.innerHTML = `<div class="error">خطأ في البحث: ${error.message}<br>Error: ${error.message}</div>`;
        resultsCount.textContent = '';
    }
}

function displayResults(data, countElement) {
    const resultsDiv = document.getElementById('searchResults');

    if (!data.results || data.results.length === 0) {
        resultsDiv.innerHTML = '<div class="empty-state"><p>لا توجد نتائج - No results found</p></div>';
        countElement.textContent = '0 results';
        return;
    }

    const results = Array.isArray(data.results) ? data.results : Object.entries(data.results).map(([content, score]) => ({
        content,
        score,
        documentId: 'Document'
    }));

    countElement.textContent = `${results.length} result${results.length > 1 ? 's' : ''}`;

    const html = results.map((result, index) => `
        <div class="result-item">
            <h3>📄 ${result.documentId || `Result ${index + 1}`}</h3>
            <div class="score">Score: ${typeof result.score === 'number' ? result.score.toFixed(4) : result.score}</div>
            <div class="content">${escapeHtml(truncate(result.content || '', 300))}</div>
        </div>
    `).join('');

    resultsDiv.innerHTML = html;
}

// Documents Management
function initializeDocumentListeners() {
    const refreshBtn = document.getElementById('refreshDocuments');
    refreshBtn.addEventListener('click', loadDocuments);
}

async function loadDocuments() {
    const documentsList = document.getElementById('documentsList');
    documentsList.innerHTML = '<div class="loading">جاري التحميل... Loading documents...</div>';

    try {
        const response = await fetch(`${API_BASE}/documents`);
        if (!response.ok) throw new Error('Failed to load documents');

        const documents = await response.json();
        allDocuments = documents;
        displayDocuments(documents);
    } catch (error) {
        console.error('Load documents error:', error);
        documentsList.innerHTML = `<div class="error">خطأ في تحميل المستندات: ${error.message}<br>Error loading documents: ${error.message}</div>`;
    }
}

function displayDocuments(documents) {
    const documentsList = document.getElementById('documentsList');

    if (!documents || documents.length === 0) {
        documentsList.innerHTML = '<div class="empty-state"><p>لا توجد مستندات - No documents available</p></div>';
        return;
    }

    const html = documents.map(doc => `
        <div class="document-item">
            <h3>📄 ${escapeHtml(doc.name || 'Untitled')}</h3>
            <div class="document-info">📂 Path: ${escapeHtml(doc.path || 'N/A')}</div>
            <div class="document-info">📏 Size: ${formatBytes(parseInt(doc.size) || 0)}</div>
            <div class="document-info">🕒 Modified: ${formatDate(doc.modified)}</div>
            ${doc.preview ? `<div class="document-preview">${escapeHtml(doc.preview)}</div>` : ''}
        </div>
    `).join('');

    documentsList.innerHTML = html;
}

// Index Management
function initializeIndexListeners() {
    const refreshBtn = document.getElementById('refreshIndexes');
    const startIndexingBtn = document.getElementById('startIndexing');

    refreshBtn.addEventListener('click', loadIndexes);
    startIndexingBtn.addEventListener('click', startIndexing);
}

async function loadIndexes() {
    const indexesList = document.getElementById('indexesList');
    const indexSelect = document.getElementById('indexSelect');

    indexesList.innerHTML = '<div class="loading">جاري التحميل... Loading indexes...</div>';

    try {
        const response = await fetch(`${API_BASE}/indexes`);
        if (!response.ok) throw new Error('Failed to load indexes');

        const indexes = await response.json();
        allIndexes = indexes;
        displayIndexes(indexes);
        updateIndexSelect(indexes);
    } catch (error) {
        console.error('Load indexes error:', error);
        indexesList.innerHTML = `<div class="error">خطأ في تحميل الفهارس: ${error.message}<br>Error loading indexes: ${error.message}</div>`;
    }
}

function displayIndexes(indexes) {
    const indexesList = document.getElementById('indexesList');

    if (!indexes || indexes.length === 0) {
        indexesList.innerHTML = '<div class="empty-state"><p>لا توجد فهارس - No indexes available<br>انقر "بدء الفهرسة" لإنشاء فهرس جديد<br>Click "Start Indexing" to create a new index</p></div>';
        return;
    }

    const html = indexes.map(index => {
        const indexName = typeof index === 'string' ? index : (index.name || 'Unknown');
        const isXml = indexName.endsWith('.xml');
        const isSer = indexName.endsWith('.ser');

        return `
            <div class="index-item">
                <h3>📚 ${escapeHtml(indexName)}</h3>
                <span class="index-type">${isXml ? 'XML' : isSer ? 'SERIALIZED' : 'INDEX'}</span>
                <p>Created: ${index.created || extractTimestamp(indexName) || 'N/A'}</p>
            </div>
        `;
    }).join('');

    indexesList.innerHTML = html;
}

function updateIndexSelect(indexes) {
    const indexSelect = document.getElementById('indexSelect');
    const currentValue = indexSelect.value;

    // Keep the "All Indexes" option and add new ones
    indexSelect.innerHTML = '<option value="">-- كل الفهارس All Indexes --</option>';

    indexes.forEach(index => {
        const indexName = typeof index === 'string' ? index : (index.name || index);
        // Only add .ser files to the select
        if (indexName.endsWith('.ser') || indexName.endsWith('.xml')) {
            const option = document.createElement('option');
            option.value = indexName;
            option.textContent = indexName;
            indexSelect.appendChild(option);
        }
    });

    // Restore previous selection if still available
    if (currentValue) {
        indexSelect.value = currentValue;
    }
}

async function startIndexing() {
    const modal = document.getElementById('loadingModal');
    modal.classList.add('show');

    try {
        const response = await fetch(`${API_BASE}/startIndexing`);
        if (!response.ok) throw new Error('Indexing failed');

        const result = await response.text();
        modal.classList.remove('show');

        showMessage(`✅ ${result}`, 'success');

        // Reload indexes after a short delay
        setTimeout(() => loadIndexes(), 1000);
    } catch (error) {
        modal.classList.remove('show');
        console.error('Indexing error:', error);
        showMessage(`خطأ في الفهرسة: ${error.message}<br>Indexing error: ${error.message}`, 'error');
    }
}

// Add Document Form
function initializeAddDocumentForm() {
    const form = document.getElementById('addDocumentForm');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        await addDocument();
    });
}

async function addDocument() {
    const name = document.getElementById('docName').value.trim();
    const content = document.getElementById('docContent').value;

    if (!name || !content) {
        showMessage('الرجاء إدخال اسم المستند والمحتوى - Please enter document name and content', 'error');
        return;
    }

    const modal = document.getElementById('loadingModal');
    modal.classList.add('show');

    try {
        const response = await fetch(`${API_BASE}/document/add?name=${encodeURIComponent(name)}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain; charset=UTF-8'
            },
            body: content
        });

        if (!response.ok) throw new Error('Failed to add document');

        const result = await response.json();
        modal.classList.remove('show');

        showMessage(`✅ Document added successfully!<br>تم إضافة المستند بنجاح!`, 'success');

        // Clear form
        document.getElementById('docName').value = '';
        document.getElementById('docContent').value = '';

        // Reload documents
        loadDocuments();

        // Switch to documents tab
        document.querySelector('.tab-btn[data-tab="documents"]').click();
    } catch (error) {
        modal.classList.remove('show');
        console.error('Add document error:', error);
        showMessage(`خطأ في إضافة المستند: ${error.message}<br>Error adding document: ${error.message}`, 'error');
    }
}

// Utility Functions
function showMessage(message, type = 'info') {
    const container = document.querySelector('.tab-content.active');
    const messageDiv = document.createElement('div');
    messageDiv.className = type;
    messageDiv.innerHTML = message;

    container.insertBefore(messageDiv, container.firstChild);

    setTimeout(() => {
        messageDiv.style.transition = 'opacity 0.5s';
        messageDiv.style.opacity = '0';
        setTimeout(() => messageDiv.remove(), 500);
    }, 5000);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function truncate(text, maxLength) {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

function formatBytes(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

function formatDate(dateString) {
    if (!dateString || dateString === 'Unknown') return 'N/A';
    try {
        const date = new Date(dateString);
        return date.toLocaleString();
    } catch (e) {
        return dateString;
    }
}

function extractTimestamp(filename) {
    const match = filename.match(/index-(\d+)/);
    if (match) {
        const timestamp = parseInt(match[1]);
        return new Date(timestamp).toLocaleString();
    }
    return null;
}

