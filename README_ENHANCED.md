# 🔍 Arabic Search Engine - محرك البحث العربي

An advanced Arabic Information Retrieval (IR) system with a modern web interface.

## ✨ Features

### 🎯 Core Functionality
- **Arabic Text Processing**: Full support for Arabic language processing with stemming
- **TF-IDF Indexing**: Term Frequency-Inverse Document Frequency ranking algorithm
- **Multiple Indexes**: Support for creating and managing multiple search indexes
- **Document Management**: Add, view, and manage documents

### 🖥️ Enhanced User Interface
The application now features a modern, intuitive web interface with:

#### 📱 Four Main Tabs

1. **🔍 Search Tab (البحث)**
   - Advanced search with query input
   - Select specific index for searching or search across all indexes
   - Adjustable result limits (5, 10, 20, 50)
   - Real-time search results with relevance scores
   - Document previews in results

2. **📄 Documents Tab (المستندات)**
   - View all available documents
   - Document information: name, path, size, modification date
   - Document content preview
   - Refresh functionality to update document list

3. **📚 Indexes Tab (الفهارس)**
   - View all created indexes (XML and Serialized formats)
   - Start new indexing process with one click
   - Index metadata (type, creation date)
   - Refresh to see newly created indexes

4. **➕ Add Document Tab (إضافة)**
   - Add new documents to the system
   - Simple form with document name and content
   - Arabic text support with RTL layout
   - Automatic indexing integration

### 🎨 UI Enhancements
- **Modern Design**: Purple gradient theme with smooth animations
- **Responsive Layout**: Works on desktop and mobile devices
- **Bilingual Interface**: Arabic (RTL) and English support
- **Loading States**: Visual feedback for all async operations
- **Error Handling**: User-friendly error messages
- **Toast Notifications**: Success/error messages

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Modern web browser

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd arabic-search-engine
```

2. **Install dependencies**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn exec:java -Dexec.mainClass="io.zaaim.arindexer.Main"
```

Or build and run with:
```bash
mvn clean package
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes:lib/*:$(cat cp.txt)" io.zaaim.arindexer.Main
```

4. **Access the application**
Open your browser and navigate to:
```
http://localhost:9001
```

## 📖 Usage Guide

### Adding Documents
1. Click on the **"➕ إضافة Add"** tab
2. Enter a document name (e.g., `document1.txt`)
3. Enter or paste Arabic text content
4. Click **"💾 حفظ المستند Save Document"**
5. The document will be added to the `saved-texts` directory

### Creating an Index
1. Navigate to the **"📚 الفهارس Indexes"** tab
2. Click **"▶️ بدء الفهرسة Start Indexing"**
3. Wait for the indexing process to complete
4. New indexes will appear in both XML and SER formats

### Searching Documents
1. Go to the **"🔍 البحث Search"** tab
2. Enter your search query in Arabic or English
3. (Optional) Select a specific index from the dropdown
4. (Optional) Adjust the number of results
5. Click **"بحث Search"**
6. View ranked results with relevance scores

### Viewing Documents
1. Click on the **"📄 المستندات Documents"** tab
2. Browse all available documents
3. View document metadata and previews
4. Click **"🔄 تحديث Refresh"** to update the list

## 📡 API Endpoints

### Search
- `GET /search?query={text}&index={indexName}&limit={n}` - Search with optional index
- `GET /search/{index}?q={query}&limit={n}` - Search specific index

### Documents
- `GET /documents` - List all documents
- `POST /document/add?name={filename}` - Add new document (body: document content)

### Indexes
- `GET /indexes` - List all indexes
- `GET /startIndexing` - Create new index from all documents

### Utilities
- `GET /greet` - Health check
- `GET /stem/{word}` - Get stem of Arabic word

## 🛠️ Technology Stack

### Backend
- **Helidon SE 3.2.2**: Lightweight microservices framework
- **Jackson**: JSON processing
- **Custom Arabic Stemmer**: Based on Khoja algorithm

### Frontend
- **Vanilla JavaScript**: No framework dependencies
- **Modern CSS3**: Gradients, animations, flexbox, grid
- **HTML5**: Semantic markup

### IR Components
- **Tokenizer**: Arabic text tokenization
- **Stop Words Filter**: Remove common words
- **Stemmer**: Khoja stemming algorithm
- **TF-IDF Calculator**: Document ranking
- **Inverted Index**: Efficient term lookup

## 📁 Project Structure

```
arabic-search-engine/
├── src/
│   ├── main/
│   │   ├── java/io/zaaim/arindexer/
│   │   │   ├── controller/        # API Controllers
│   │   │   │   ├── IndexController.java
│   │   │   │   ├── SearchController.java
│   │   │   │   └── WebController.java
│   │   │   ├── model/            # Data models
│   │   │   ├── service/          # Business logic
│   │   │   ├── stemmer/          # Arabic stemming
│   │   │   └── util/             # Utilities
│   │   └── resources/
│   │       ├── stopwords.txt     # Arabic stop words
│   │       └── web/              # Frontend files
│   │           ├── index.html
│   │           ├── styles.css
│   │           └── app.js
│   └── test/
├── lib/                          # External libraries
├── indexes/                      # Generated indexes
├── saved-texts/                  # Document storage
└── pom.xml                      # Maven configuration
```

## 🎯 Key Features Implementation

### Arabic Text Processing
The system uses specialized Arabic NLP components:
- **Normalization**: Handles different Arabic letter forms
- **Diacritics Removal**: Removes harakat (tashkeel)
- **Stemming**: Reduces words to their root forms
- **Stop Words**: Filters common Arabic words

### TF-IDF Scoring
Documents are ranked using:
- **Term Frequency (TF)**: How often a term appears in a document
- **Inverse Document Frequency (IDF)**: How unique a term is across all documents
- **Score = TF × IDF**: Combined relevance score

### Responsive Design
The UI adapts to different screen sizes:
- Desktop: Full multi-column layouts
- Tablet: Adjusted grid layouts
- Mobile: Single-column responsive design

## 🔧 Configuration

### Storage Directories
You can configure storage locations using JVM properties:

```bash
java -Dsave.dir=/path/to/documents -Dindex.dir=/path/to/indexes ...
```

Default directories:
- Documents: `saved-texts/`
- Indexes: `indexes/`

### Server Port
Default port: `9001`

To change the port, modify `Main.java`:
```java
WebServer webServer = WebServer.builder()
    .port(YOUR_PORT)
    ...
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## 📝 License

This project is available for educational and research purposes.

## 👨‍💻 Author

Developed as an advanced Arabic Information Retrieval system with modern web interface.

## 🙏 Acknowledgments

- Khoja Arabic Stemmer algorithm
- Helidon microservices framework
- Arabic NLP community

---

**النظام يدعم اللغة العربية بشكل كامل**

For questions or support, please open an issue on the repository.

