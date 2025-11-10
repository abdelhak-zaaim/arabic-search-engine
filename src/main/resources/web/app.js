const API_BASE = 'http://localhost:9001';

document.addEventListener('DOMContentLoaded', () => {
    const searchBtn = document.getElementById('searchBtn');
    const searchQuery = document.getElementById('searchQuery');
    const refreshBtn = document.getElementById('refreshIndexes');

    searchBtn.addEventListener('click', performSearch);
    searchQuery.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') performSearch();
    });
    refreshBtn.addEventListener('click', loadIndexes);

    loadIndexes();
});

async function performSearch() {
    const query = document.getElementById('searchQuery').value.trim();
    if (!query) {
        alert('الرجاء إدخال كلمة البحث - Please enter a search query');
        return;
    }

    const resultsSection = document.getElementById('resultsSection');
    const resultsDiv = document.getElementById('searchResults');
    resultsDiv.innerHTML = '<div class="loading">جاري البحث... Searching...</div>';
    resultsSection.style.display = 'block';

    try {
        const response = await fetch(`${API_BASE}/search?query=${encodeURIComponent(query)}`);
        if (!response.ok) throw new Error('Search failed');

        const data = await response.json();
        displayResults(data);
    } catch (error) {
        resultsDiv.innerHTML = `<div class="error">خطأ في البحث: ${error.message}</div>`;
    }
}

function displayResults(data) {
    const resultsDiv = document.getElementById('searchResults');

    if (!data.results || data.results.length === 0) {
        resultsDiv.innerHTML = '<p>لا توجد نتائج - No results found</p>';
        return;
    }

    const html = data.results.map(result => `
        <div class="result-item">
            <h3>${result.documentId || 'Document'}</h3>
            <div class="score">Score: ${result.score?.toFixed(4) || 'N/A'}</div>
            <p>${result.content || ''}</p>
        </div>
    `).join('');

    resultsDiv.innerHTML = html;
}

async function loadIndexes() {
    const indexesList = document.getElementById('indexesList');
    indexesList.innerHTML = '<div class="loading">جاري التحميل... Loading...</div>';

    try {
        const response = await fetch(`${API_BASE}/indexes`);
        if (!response.ok) throw new Error('Failed to load indexes');

        const indexes = await response.json();
        displayIndexes(indexes);
    } catch (error) {
        indexesList.innerHTML = `<div class="error">خطأ في تحميل الفهارس: ${error.message}</div>`;
    }
}

function displayIndexes(indexes) {
    const indexesList = document.getElementById('indexesList');

    if (!indexes || indexes.length === 0) {
        indexesList.innerHTML = '<p>لا توجد فهارس - No indexes available</p>';
        return;
    }

    const html = indexes.map(index => `
        <div class="index-item">
            <h3>${index.name || index}</h3>
            <p>Created: ${index.created || 'N/A'}</p>
        </div>
    `).join('');

    indexesList.innerHTML = html;
}