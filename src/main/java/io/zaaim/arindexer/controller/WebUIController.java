package io.zaaim.arindexer.controller;

/**
 * Controller for serving web UI
 */
public class WebUIController {

    public String serveIndex() {
        return getIndexHtml();
    }

    private String getIndexHtml() {
        return """
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>محرك البحث العربي - Arabic Search Engine</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            max-width: 900px;
            width: 100%;
            padding: 40px;
        }

        .header {
            text-align: center;
            margin-bottom: 40px;
        }

        .header h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 2.5em;
        }

        .header p {
            color: #666;
            font-size: 1.1em;
        }

        .tabs {
            display: flex;
            gap: 10px;
            margin-bottom: 30px;
            border-bottom: 2px solid #eee;
        }

        .tab-btn {
            padding: 12px 24px;
            border: none;
            background: transparent;
            cursor: pointer;
            font-size: 1em;
            color: #666;
            border-bottom: 3px solid transparent;
            transition: all 0.3s ease;
        }

        .tab-btn.active {
            color: #667eea;
            border-bottom-color: #667eea;
        }

        .tab-btn:hover {
            color: #667eea;
        }

        .tab-content {
            display: none;
        }

        .tab-content.active {
            display: block;
        }

        .search-section {
            margin-bottom: 30px;
        }

        .input-group {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }

        input[type="text"], input[type="file"], select, textarea {
            flex: 1;
            padding: 12px 15px;
            border: 2px solid #eee;
            border-radius: 8px;
            font-size: 1em;
            transition: border-color 0.3s ease;
        }

        input[type="text"]:focus, input[type="file"]:focus, select:focus, textarea:focus {
            outline: none;
            border-color: #667eea;
        }

        button {
            padding: 12px 30px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1em;
            font-weight: 600;
            transition: background 0.3s ease;
        }

        button:hover {
            background: #764ba2;
        }

        .results {
            margin-top: 20px;
        }

        .result-item {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 15px;
            border-left: 4px solid #667eea;
            transition: all 0.3s ease;
        }

        .result-item:hover {
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        .result-title {
            font-size: 1.2em;
            font-weight: 600;
            color: #333;
            margin-bottom: 10px;
        }

        .result-content {
            color: #666;
            font-size: 0.95em;
            margin-bottom: 10px;
            line-height: 1.6;
        }

        .result-score {
            display: inline-block;
            background: #667eea;
            color: white;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 600;
        }

        .index-item {
            background: #f9f9f9;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .index-info {
            flex: 1;
        }

        .index-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }

        .index-stats {
            color: #666;
            font-size: 0.9em;
        }

        .status-message {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: none;
        }

        .status-message.success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .status-message.error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .status-message.show {
            display: block;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 600;
        }

        textarea {
            min-height: 150px;
            resize: vertical;
        }

        .loading {
            display: none;
            text-align: center;
            padding: 20px;
            color: #667eea;
        }

        .loading.show {
            display: block;
        }

        .spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #667eea;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
            margin: 0 auto;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        .delete-btn {
            background: #dc3545;
            padding: 8px 15px;
            font-size: 0.9em;
        }

        .delete-btn:hover {
            background: #c82333;
        }

        .no-results {
            text-align: center;
            color: #999;
            padding: 30px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔍 محرك البحث العربي</h1>
            <p>Arabic Search Engine with TF-IDF</p>
        </div>

        <div class="tabs">
            <button class="tab-btn active" onclick="switchTab('search')">البحث</button>
            <button class="tab-btn" onclick="switchTab('add')">إضافة وثيقة</button>
            <button class="tab-btn" onclick="switchTab('indexes')">الفهارس</button>
        </div>

        <!-- Search Tab -->
        <div id="search" class="tab-content active">
            <div class="search-section">
                <div class="status-message" id="searchStatus"></div>
                <div class="input-group">
                    <input type="text" id="searchQuery" placeholder="ابحث هنا..." />
                    <select id="searchIndex">
                        <option value="default">الفهرس الافتراضي</option>
                    </select>
                    <button onclick="performSearch()">بحث</button>
                </div>
                <div class="loading" id="searchLoading">
                    <div class="spinner"></div>
                    جاري البحث...
                </div>
                <div class="results" id="searchResults"></div>
            </div>
        </div>

        <!-- Add Document Tab -->
        <div id="add" class="tab-content">
            <div class="status-message" id="addStatus"></div>
            <form onsubmit="addDocument(event)">
                <div class="form-group">
                    <label for="docTitle">العنوان:</label>
                    <input type="text" id="docTitle" placeholder="عنوان الوثيقة" required />
                </div>
                <div class="form-group">
                    <label for="docId">معرّف الوثيقة (اختياري):</label>
                    <input type="text" id="docId" placeholder="Doc-001" />
                </div>
                <div class="form-group">
                    <label for="docContent">المحتوى:</label>
                    <textarea id="docContent" placeholder="محتوى الوثيقة" required></textarea>
                </div>
                <div class="form-group">
                    <label for="docIndexName">اسم الفهرس:</label>
                    <input type="text" id="docIndexName" placeholder="default" value="default" />
                </div>
                <div class="loading" id="addLoading">
                    <div class="spinner"></div>
                    جاري إضافة الوثيقة...
                </div>
                <button type="submit">إضافة وثيقة</button>
            </form>
        </div>

        <!-- Indexes Tab -->
        <div id="indexes" class="tab-content">
            <div class="status-message" id="indexesStatus"></div>
            <button onclick="refreshIndexes()" style="margin-bottom: 20px;">تحديث</button>
            <div class="loading" id="indexesLoading">
                <div class="spinner"></div>
                جاري تحميل الفهارس...
            </div>
            <div id="indexesList"></div>
        </div>
    </div>

    <script>
        const API_BASE = 'http://localhost:8080/api';

        function switchTab(tabName) {
            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.tab-btn').forEach(el => el.classList.remove('active'));
            document.getElementById(tabName).classList.add('active');
            event.target.classList.add('active');
            
            if (tabName === 'indexes') {
                refreshIndexes();
            }
        }

        async function performSearch() {
            const query = document.getElementById('searchQuery').value;
            const indexName = document.getElementById('searchIndex').value;
            
            if (!query.trim()) {
                showMessage('searchStatus', 'الرجاء إدخال نص للبحث', 'error');
                return;
            }

            showLoading('searchLoading', true);
            showMessage('searchStatus', '', '');

            try {
                const response = await fetch(`${API_BASE}/search?q=${encodeURIComponent(query)}&index=${indexName}&limit=10`);
                const data = await response.json();

                displaySearchResults(data.results);
                showMessage('searchStatus', `تم العثور على ${data.totalResults} نتيجة في ${data.executionTimeMs}ms`, 'success');
            } catch (error) {
                showMessage('searchStatus', 'خطأ في البحث: ' + error.message, 'error');
            } finally {
                showLoading('searchLoading', false);
            }
        }

        function displaySearchResults(results) {
            const resultsDiv = document.getElementById('searchResults');
            
            if (!results || results.length === 0) {
                resultsDiv.innerHTML = '<div class="no-results">لم يتم العثور على نتائج</div>';
                return;
            }

            resultsDiv.innerHTML = results.map(result => `
                <div class="result-item">
                    <div class="result-title">${result.title || 'بدون عنوان'}</div>
                    <div class="result-content">${result.content}</div>
                    <span class="result-score">النقاط: ${result.score.toFixed(4)}</span>
                </div>
            `).join('');
        }

        async function addDocument(event) {
            event.preventDefault();
            
            const doc = {
                id: document.getElementById('docId').value || 'doc-' + Date.now(),
                title: document.getElementById('docTitle').value,
                content: document.getElementById('docContent').value,
                indexName: document.getElementById('docIndexName').value || 'default'
            };

            showLoading('addLoading', true);
            showMessage('addStatus', '', '');

            try {
                const response = await fetch(`${API_BASE}/documents`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(doc)
                });

                if (response.ok) {
                    const data = await response.json();
                    showMessage('addStatus', 'تم إضافة الوثيقة بنجاح!', 'success');
                    document.getElementById('docTitle').value = '';
                    document.getElementById('docContent').value = '';
                    document.getElementById('docId').value = '';
                    
                    // Update search index dropdown
                    refreshIndexDropdown();
                } else {
                    showMessage('addStatus', 'خطأ: ' + response.statusText, 'error');
                }
            } catch (error) {
                showMessage('addStatus', 'خطأ في الإضافة: ' + error.message, 'error');
            } finally {
                showLoading('addLoading', false);
            }
        }

        async function refreshIndexes() {
            showLoading('indexesLoading', true);
            showMessage('indexesStatus', '', '');

            try {
                const response = await fetch(`${API_BASE}/indexes`);
                const data = await response.json();

                displayIndexes(data.indexes);
                refreshIndexDropdown();
                showMessage('indexesStatus', 'تم تحديث الفهارس', 'success');
            } catch (error) {
                showMessage('indexesStatus', 'خطأ: ' + error.message, 'error');
            } finally {
                showLoading('indexesLoading', false);
            }
        }

        function displayIndexes(indexes) {
            const list = document.getElementById('indexesList');
            
            if (!indexes || Object.keys(indexes).length === 0) {
                list.innerHTML = '<div class="no-results">لا توجد فهارس</div>';
                return;
            }

            list.innerHTML = Object.entries(indexes).map(([name, info]) => `
                <div class="index-item">
                    <div class="index-info">
                        <div class="index-name">${name}</div>
                        <div class="index-stats">
                            وثائق: ${info.totalDocuments || 0} | 
                            مصطلحات: ${info.uniqueTerms || 0}
                        </div>
                    </div>
                    <button class="delete-btn" onclick="deleteIndex('${name}')">حذف</button>
                </div>
            `).join('');
        }

        async function deleteIndex(indexName) {
            if (!confirm('هل أنت متأكد من حذف الفهرس ' + indexName + '؟')) {
                return;
            }

            try {
                const response = await fetch(`${API_BASE}/indexes/${indexName}`, {
                    method: 'DELETE'
                });

                if (response.ok) {
                    showMessage('indexesStatus', 'تم حذف الفهرس بنجاح', 'success');
                    refreshIndexes();
                } else {
                    showMessage('indexesStatus', 'خطأ: ' + response.statusText, 'error');
                }
            } catch (error) {
                showMessage('indexesStatus', 'خطأ: ' + error.message, 'error');
            }
        }

        async function refreshIndexDropdown() {
            try {
                const response = await fetch(`${API_BASE}/indexes`);
                const data = await response.json();
                
                const select = document.getElementById('searchIndex');
                const current = select.value;
                select.innerHTML = Object.keys(data.indexes).map(name => 
                    `<option value="${name}" ${name === current ? 'selected' : ''}>${name}</option>`
                ).join('');
            } catch (error) {
                console.error('خطأ في تحديث قائمة الفهارس:', error);
            }
        }

        function showLoading(id, show) {
            const element = document.getElementById(id);
            if (show) {
                element.classList.add('show');
            } else {
                element.classList.remove('show');
            }
        }

        function showMessage(id, message, type) {
            const element = document.getElementById(id);
            if (message) {
                element.textContent = message;
                element.className = 'status-message ' + type + ' show';
            } else {
                element.classList.remove('show');
            }
        }

        // Initialize on page load
        document.addEventListener('DOMContentLoaded', () => {
            refreshIndexes();
            refreshIndexDropdown();
        });

        // Allow search on Enter key
        document.getElementById('searchQuery')?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    </script>
</body>
</html>
        """;
    }
}

