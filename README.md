# Arabic Search Engine with TF-IDF

A complete, production-ready Arabic search engine implementation featuring TF-IDF vectorization, ISRI stemming, and a modern web interface.

## Features

- ✅ **TF-IDF Vectorization**: Advanced text search using Term Frequency-Inverse Document Frequency
- ✅ **Arabic Stemming**: ISRI stemmer integration for proper Arabic text processing
- ✅ **Multiple Indexes**: Support for multiple independent indexes
- ✅ **Cosine Similarity**: Efficient similarity search between documents
- ✅ **Advanced Filtering**: Built-in filters for score-based and title-based filtering
- ✅ **Related Documents**: Find documents similar to a given document
- ✅ **RESTful API**: Complete REST API for all operations
- ✅ **Web Interface**: Arabic-optimized web UI for easy interaction
- ✅ **Concurrent Support**: Thread-safe operations using ConcurrentHashMap

## Architecture

### Components

#### 1. **Model Classes** (`model/`)
- `Document.java`: Represents a document to be indexed
- `SearchResult.java`: Represents a search result with TF-IDF score
- `SearchResponse.java`: Response wrapper for search queries

#### 2. **Utilities** (`util/`)
- `ArabicTokenizer.java`: Tokenizes Arabic text using regex patterns
- `IndexMaps.java`: Manages inverted index and TF-IDF calculations
- `TFIDFVector.java`: Represents document vectors for similarity computation

#### 3. **Services** (`service/`)
- `IndexService.java`: Manages document indexing with TF-IDF
  - Multiple index support
  - Document storage and retrieval
  - Automatic TF-IDF vector generation
  
- `SearchService.java`: Handles search operations
  - Cosine similarity search
  - Related documents discovery
  - Multi-index search support
  - Custom filtering support
  
- `QueryProcessor.java`: Processes queries through tokenization and stemming

#### 4. **Analysis** (`analysis/`)
- `CustomArabicAnalyzer.java`: Lucene analyzer for Arabic text
- `CustomArabicStemFilter.java`: Token filter with ISRI stemming

#### 5. **Controllers** (`controller/`)
- `SearchController.java`: REST API endpoints
- `WebUIController.java`: Web interface serving

## API Endpoints

### Documents
```
POST   /api/documents                              - Add/Index a document
GET    /api/indexes/{indexName}/documents         - Get all documents in index
GET    /api/indexes/{indexName}/documents/{docId} - Get specific document
DELETE /api/indexes/{indexName}/documents/{docId} - Delete document
```

### Search
```
GET /api/search?q=query&index=indexName&limit=10 - Search documents
GET /api/search/related?docId=id&index=indexName  - Get related documents
```

### Indexes
```
GET    /api/indexes                  - List all indexes with statistics
GET    /api/indexes/{indexName}      - Get index information
DELETE /api/indexes/{indexName}      - Delete entire index
POST   /api/indexes/{indexName}/rebuild - Rebuild index TF-IDF values
```

## Document Structure

```json
{
  "id": "doc-001",
  "title": "Document Title",
  "content": "Document content...",
  "indexName": "default"
}
```

## How It Works

### Indexing Process

1. **Tokenization**: Document text is tokenized using `ArabicTokenizer`
   - Extracts Arabic words using Unicode ranges
   - Converts to lowercase

2. **Stemming**: Each token is stemmed using ISRI Stemmer
   - Removes common prefixes and suffixes
   - Normalizes word forms

3. **TF Calculation**: Term Frequency for each token
   - Count occurrences in document

4. **IDF Calculation**: Inverse Document Frequency
   - IDF(term) = log10(total_documents / documents_with_term)

5. **TF-IDF Vector**: Combined score for each term
   - TF-IDF(term) = TF * IDF

6. **Magnitude Calculation**: Vector normalization
   - magnitude = sqrt(sum of all squared weights)

### Search Process

1. **Query Processing**: Query is tokenized and stemmed like documents

2. **Query Vector Creation**: TF-IDF vector for query terms

3. **Similarity Calculation**: Cosine similarity between query and all documents
   - similarity = dot_product / (query_magnitude * doc_magnitude)

4. **Ranking**: Results sorted by similarity score (descending)

5. **Filtering** (Optional): Apply custom filters to results

## Usage Examples

### Add Document

```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{
    "id": "article-1",
    "title": "الذكاء الاصطناعي",
    "content": "الذكاء الاصطناعي هو محاكاة للذكاء البشري...",
    "indexName": "articles"
  }'
```

### Search

```bash
curl "http://localhost:8080/api/search?q=الذكاء&index=articles&limit=10"
```

### List Indexes

```bash
curl http://localhost:8080/api/indexes
```

### Get Related Documents

```bash
curl "http://localhost:8080/api/search/related?docId=article-1&index=articles"
```

## Dependencies

```xml
<!-- Core -->
<dependency>
    <groupId>io.helidon.webserver</groupId>
    <artifactId>helidon-webserver</artifactId>
    <version>4.3.1</version>
</dependency>

<!-- JSON Processing -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.0</version>
</dependency>

<!-- Lucene -->
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-core</artifactId>
    <version>9.9.2</version>
</dependency>

<!-- ISRI Stemmer (to be added) -->
<!-- Custom dependency for Arabic stemming -->
```

## Building and Running

### Prerequisites
- Java 25+
- Maven 3.6+

### Build
```bash
mvn clean install
```

### Run
```bash
mvn exec:java -Dexec.mainClass="io.zaaim.arindexer.Main"
```

Or compile and run:
```bash
mvn clean compile
java -cp target/classes:target/dependency/* io.zaaim.arindexer.Main
```

### Access Web Interface
Open browser to: `http://localhost:8080`

## Performance Characteristics

- **Indexing**: O(n*m) where n = documents, m = average tokens per document
- **Search**: O(m*n) where m = query tokens, n = documents in index
- **Memory**: Depends on vocabulary size and document count
- **Cosine Similarity**: O(vocabulary_size) per comparison

## Configuration

Key configurations in `Main.java`:
```java
.port(8080)  // Server port
```

## Advanced Features

### Multiple Indexes
- Create separate indexes for different document collections
- Each index maintains its own TF-IDF statistics
- Search can target specific index or multiple indexes

### Custom Filters
Implement `SearchService.SearchFilter` interface:
```java
public class MyFilter implements SearchService.SearchFilter {
    public boolean matches(SearchResult result) {
        // Custom filtering logic
        return true;
    }
}
```

### Index Rebuilding
Recalculate TF-IDF scores after bulk modifications:
```bash
curl -X POST http://localhost:8080/api/indexes/articles/rebuild
```

## File Structure

```
src/main/java/io/zaaim/arindexer/
├── Main.java                          # Application entry point
├── analysis/
│   ├── CustomArabicAnalyzer.java     # Lucene analyzer
│   └── CustomArabicStemFilter.java   # Stemming token filter
├── controller/
│   ├── SearchController.java          # REST API endpoints
│   └── WebUIController.java          # Web interface
├── model/
│   ├── Document.java
│   ├── SearchResult.java
│   └── SearchResponse.java
├── service/
│   ├── IndexService.java              # Indexing logic
│   ├── SearchService.java             # Search logic
│   └── QueryProcessor.java            # Query processing
└── util/
    ├── ArabicTokenizer.java           # Tokenization
    ├── IndexMaps.java                 # Inverted index
    └── TFIDFVector.java               # TF-IDF representation
```

## Future Enhancements

- [ ] Persistence layer (Database support)
- [ ] Query suggestion/autocomplete
- [ ] Spelling correction
- [ ] Faceted search
- [ ] Distributed indexing
- [ ] Real-time indexing updates
- [ ] Query analytics
- [ ] Advanced Arabic NLP features

## License

MIT License

## Author

Built with ❤️ for Arabic NLP

