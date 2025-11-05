# Quick Start Guide - Arabic Search Engine

## Installation & Setup

### Prerequisites
- Java 25 or higher
- Maven 3.6+
- SAFAR library (for ISRI stemmer)

### Step 1: Add SAFAR Dependency

The project uses SAFAR library for Arabic stemming. You need to add the SAFAR JAR file to your local Maven repository:

```bash
# Download SAFAR from: https://github.com/UKPLab/safar
# Or add to your maven repository

# If you have the SAFAR JAR:
mvn install:install-file -Dfile=safar-core.jar -DgroupId=safar \
  -DartifactId=safar-core -Dversion=1.0 -Dpackaging=jar
```

### Step 2: Build the Project

```bash
cd /Users/pro/IdeaProjects/arabic-search-engine
mvn clean install
```

### Step 3: Run the Application

```bash
mvn exec:java -Dexec.mainClass="io.zaaim.arindexer.Main"
```

Or:

```bash
java -cp target/classes:target/dependency/* io.zaaim.arindexer.Main
```

## Access the Application

Once running, open your browser and navigate to:
- **Web Interface**: http://localhost:8080
- **API Documentation**: http://localhost:8080/api/indexes

## API Usage Examples

### 1. Add a Document

```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{
    "id": "doc-1",
    "title": "تعلم البرمجة",
    "content": "البرمجة هي فن وعلم كتابة الأكواد البرمجية للحاسوب",
    "indexName": "default"
  }'
```

### 2. Search Documents

```bash
curl "http://localhost:8080/api/search?q=البرمجة&index=default&limit=10"
```

### 3. Get All Indexes

```bash
curl http://localhost:8080/api/indexes
```

### 4. Get Documents in Index

```bash
curl "http://localhost:8080/api/indexes/default/documents"
```

### 5. Get Related Documents

```bash
curl "http://localhost:8080/api/search/related?docId=doc-1&index=default&limit=5"
```

### 6. Delete Document

```bash
curl -X DELETE "http://localhost:8080/api/indexes/default/documents/doc-1"
```

### 7. Delete Index

```bash
curl -X DELETE "http://localhost:8080/api/indexes/default"
```

### 8. Rebuild Index

```bash
curl -X POST "http://localhost:8080/api/indexes/default/rebuild"
```

## Web Interface Features

### Search Tab
- Enter Arabic text to search
- Select index to search in
- View results with TF-IDF scores
- See execution time

### Add Document Tab
- Add new documents to index
- Specify document ID (auto-generated if not provided)
- Add to custom index
- Immediate indexing

### Indexes Tab
- View all created indexes
- See document count and unique terms
- Delete indexes

## Project Structure

```
src/main/java/io/zaaim/arindexer/
├── Main.java                    # Entry point, web server setup
├── analysis/
│   ├── CustomArabicAnalyzer.java
│   └── CustomArabicStemFilter.java
├── controller/
│   ├── SearchController.java    # API endpoint handlers
│   └── WebUIController.java     # Web UI server
├── model/
│   ├── Document.java            # Document model
│   ├── SearchResult.java        # Search result model
│   └── SearchResponse.java      # API response wrapper
├── service/
│   ├── IndexService.java        # Document indexing with TF-IDF
│   ├── SearchService.java       # Search logic
│   └── QueryProcessor.java      # Query processing
└── util/
    ├── ArabicTokenizer.java     # Text tokenization
    ├── IndexMaps.java           # Inverted index management
    └── TFIDFVector.java         # Vector representation
```

## How TF-IDF Works

### Indexing Phase
1. **Tokenization**: Arabic text is tokenized into words
   - Uses Unicode ranges for Arabic characters
   - Lowercase conversion

2. **Stemming**: Words are stemmed using ISRI algorithm
   - Removes prefixes and suffixes
   - Normalizes word forms

3. **TF Calculation**: Term Frequency
   - How many times a word appears in a document

4. **IDF Calculation**: Inverse Document Frequency
   - IDF = log10(total_docs / docs_with_term)

5. **TF-IDF Vector**: Combined score
   - TF-IDF = TF × IDF
   - Vector is normalized (magnitude calculated)

### Search Phase
1. **Query Processing**: Same tokenization and stemming as documents
2. **Query Vector**: Build TF-IDF vector for query
3. **Similarity**: Calculate cosine similarity with all documents
4. **Ranking**: Sort by similarity score (descending)
5. **Results**: Return top N results

## Multiple Indexes

Create separate indexes for different document collections:

```bash
# Index 1: News articles
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{"id": "news-1", "title": "خبر", "content": "محتوى الخبر", "indexName": "news"}'

# Index 2: Blog posts
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{"id": "blog-1", "title": "مدونة", "content": "محتوى المدونة", "indexName": "blogs"}'

# Search in specific index
curl "http://localhost:8080/api/search?q=خبر&index=news"
curl "http://localhost:8080/api/search?q=مدونة&index=blogs"
```

## Advanced Features

### Custom Filtering
The SearchService supports custom filters. Implement the `SearchFilter` interface:

```java
SearchService.SearchFilter filter = result -> result.getScore() > 0.5;
```

### Related Documents
Find documents similar to a given document:

```bash
curl "http://localhost:8080/api/search/related?docId=doc-1&index=default&limit=5"
```

## Performance Tips

1. **Batch Indexing**: Index multiple documents before searching
2. **Rebuild Index**: After many deletions, rebuild to optimize scores
3. **Index Limits**: Keep indexes reasonably sized for best performance
4. **Query Optimization**: Use precise search terms

## Troubleshooting

### Port Already in Use
Change port in Main.java:
```java
.port(8081)  // Change from 8080
```

### SAFAR Dependency Error
Ensure SAFAR library is in classpath. Add to pom.xml:
```xml
<dependency>
    <groupId>safar</groupId>
    <artifactId>safar-core</artifactId>
    <version>1.0</version>
</dependency>
```

### No Results Found
1. Check index name matches
2. Verify documents were indexed
3. Try simpler search terms
4. Check console for indexing errors

## Future Enhancements

- [ ] Persistence (Database backend)
- [ ] Advanced Arabic NLP (morphological analysis)
- [ ] Query suggestions/autocomplete
- [ ] Spelling correction
- [ ] Faceted search
- [ ] Distributed indexing
- [ ] Performance optimization (caching)

## License

MIT

## Support

For issues and questions, check the README.md file.

