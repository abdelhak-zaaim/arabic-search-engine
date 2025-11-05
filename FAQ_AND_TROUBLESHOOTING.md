# FAQ & Troubleshooting Guide

## Frequently Asked Questions

### General Questions

**Q: What is TF-IDF?**
A: TF-IDF (Term Frequency-Inverse Document Frequency) is a numerical technique to evaluate how important a word is in a document relative to a collection of documents. It ranks search results by relevance.

**Q: Why use ISRI stemming?**
A: ISRI is specifically designed for Arabic text. It removes prefixes and suffixes common in Arabic, normalizing words to their root form for better matching.

**Q: Can I use this in production?**
A: The current implementation stores data in memory. For production, add a persistence layer (database) to save indexes.

**Q: How many documents can it handle?**
A: Currently limited by available RAM. With typical documents (~500 words), you can easily handle 10,000+ documents on modern hardware.

### Getting Started

**Q: How do I start the application?**
A:
```bash
cd /Users/pro/IdeaProjects/arabic-search-engine
mvn exec:java -Dexec.mainClass="io.zaaim.arindexer.Main"
```
Then visit: http://localhost:8080

**Q: Why do I see a blank page?**
A: Check that:
1. Server is running (check console output)
2. Port 8080 is accessible
3. JavaScript is enabled in your browser
4. Try http://localhost:8080/ui

**Q: Can I change the port?**
A: Yes, edit Main.java:
```java
.port(8081)  // Change from 8080
```

### API Usage

**Q: How do I add documents programmatically?**
A:
```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{
    "id": "doc-1",
    "title": "عنوان الوثيقة",
    "content": "محتوى الوثيقة",
    "indexName": "default"
  }'
```

**Q: What's the difference between "index" and "indexName"?**
A: 
- `indexName`: Used in API queries and document creation to specify which index
- `index`: The actual index object that stores documents

**Q: Can I search in multiple indexes at once?**
A: Currently, search targets one index. To search multiple, make separate requests and merge results.

**Q: How do I delete a document?**
A:
```bash
curl -X DELETE "http://localhost:8080/api/indexes/default/documents/doc-1"
```

### Search Features

**Q: Why am I not getting search results?**
A: Check:
1. Documents are in the correct index
2. Search terms match content after stemming
3. Index name is correct
4. Try simpler search terms

**Q: What does the score mean?**
A: The score is the cosine similarity (0-1 range) between query and document vectors. Higher = more relevant.

**Q: How can I get similar documents?**
A:
```bash
curl "http://localhost:8080/api/search/related?docId=doc-1&index=default&limit=5"
```

**Q: Can I filter search results?**
A: Yes, use the `searchWithFilter` method in SearchService or limit by score threshold.

### Indexing

**Q: What happens when I re-index a document?**
A: It replaces the old version. Old TF-IDF vectors are removed and new ones calculated.

**Q: When should I rebuild an index?**
A: After deleting many documents, to recalculate IDF values for better accuracy.

```bash
curl -X POST "http://localhost:8080/api/indexes/default/rebuild"
```

**Q: Can I have multiple indexes?**
A: Yes! Each index is independent. Create them by using different "indexName" values:
```bash
# Create in "news" index
-d '{"indexName": "news", ...}'

# Create in "blogs" index  
-d '{"indexName": "blogs", ...}'
```

### Performance

**Q: How long does indexing take?**
A: Typically < 10ms per document on modern hardware for 500-word documents.

**Q: Why is search slow sometimes?**
A: 
- First search in an index is slower (JIT compilation)
- Many documents slows cosine similarity calculation
- Consider pagination with the `limit` parameter

**Q: Can I improve search speed?**
A: 
- Use specific search terms (avoid common words)
- Limit results with `limit` parameter
- Periodically rebuild indexes
- Keep index sizes reasonable

## Troubleshooting

### Build Issues

**Issue: "Cannot find symbol ISRIStemmer"**

Solution: Add SAFAR dependency:
```xml
<dependency>
    <groupId>safar</groupId>
    <artifactId>safar-core</artifactId>
    <version>1.0</version>
</dependency>
```

**Issue: "Port 8080 already in use"**

Solution 1: Change port in Main.java to 8081
Solution 2: Kill process using port 8080:
```bash
lsof -i :8080
kill -9 <PID>
```

**Issue: Maven command not found**

Solution:
```bash
# Install Maven via Homebrew
brew install maven

# Or use included wrapper
chmod +x mvnw
./mvnw clean install
```

### Runtime Issues

**Issue: "No suitable constructor found for Document"**

Check that Document constructor matches your JSON:
```json
{
  "id": "required",
  "title": "optional",
  "content": "optional",
  "indexName": "optional, defaults to 'default'"
}
```

**Issue: "Index not found"**

Make sure:
1. Index name in query matches index name when adding documents
2. Documents have been indexed before searching
3. Try adding a test document first

**Issue: Search returns empty results**

Try:
1. Verify documents are in the index (GET /api/indexes/indexName/documents)
2. Use simpler search terms
3. Search terms might be getting stemmed differently than expected
4. Check Arabic encoding in your requests

**Issue: Web UI shows "Cannot connect to server"**

Check:
1. Application is running (see console output)
2. Port 8080 is not blocked by firewall
3. Try `curl http://localhost:8080` from terminal
4. Check browser console for JavaScript errors

### Data Issues

**Issue: "Document ID already exists"**

The system replaces documents with the same ID. If you want to keep both:
```bash
# Use different IDs
-d '{"id": "doc-1-v1", ...}'
-d '{"id": "doc-1-v2", ...}'
```

**Issue: Characters showing as ???? (encoding)**

Ensure:
1. Terminal is UTF-8 configured
2. JSON requests include: `Content-Type: application/json; charset=UTF-8`
3. Browser is UTF-8 (usually automatic)

**Issue: Arabic text not displaying properly in web UI**

Check:
1. Browser has Arabic font support
2. Page encoding is UTF-8 (check in browser settings)
3. Try a different browser
4. Clear browser cache and reload

### API Issues

**Issue: 404 Not Found errors**

Check:
1. API path is correct (case-sensitive)
2. Index name and document ID in URL match exactly
3. Use proper HTTP method (GET, POST, DELETE)

**Issue: "JSON parse error"**

Verify JSON is valid:
1. All strings quoted with double quotes
2. Proper comma placement
3. Use JSON validator: https://jsonlint.com/

**Issue: CORS errors (when using from browser)**

The API should work from same origin. If cross-origin:
- Check browser console for CORS errors
- May need to add CORS headers to API

## Performance Optimization Tips

1. **Batch Operations**
   - Add multiple documents in sequence
   - Index once, not after each document

2. **Query Optimization**
   - Use specific search terms
   - Avoid very common words
   - Limit results with `limit` parameter

3. **Index Management**
   - Rebuild periodically after many deletions
   - Remove old/unused indexes
   - Keep vocabulary size reasonable

4. **Memory**
   - Monitor available RAM
   - Remove large indexes when done
   - Consider pagination for large result sets

## Command Reference

### Add Document
```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{"id":"1","title":"Title","content":"Content","indexName":"default"}'
```

### Search
```bash
curl "http://localhost:8080/api/search?q=keyword&index=default&limit=10"
```

### Get All Indexes
```bash
curl http://localhost:8080/api/indexes
```

### Get Index Info
```bash
curl "http://localhost:8080/api/indexes/default"
```

### Get All Documents
```bash
curl "http://localhost:8080/api/indexes/default/documents"
```

### Get Single Document
```bash
curl "http://localhost:8080/api/indexes/default/documents/1"
```

### Get Related Documents
```bash
curl "http://localhost:8080/api/search/related?docId=1&index=default&limit=5"
```

### Delete Document
```bash
curl -X DELETE "http://localhost:8080/api/indexes/default/documents/1"
```

### Delete Index
```bash
curl -X DELETE "http://localhost:8080/api/indexes/default"
```

### Rebuild Index
```bash
curl -X POST "http://localhost:8080/api/indexes/default/rebuild"
```

## Common Workflows

### Workflow 1: Full Text Search Application

1. Start application
2. Visit http://localhost:8080
3. Click "Add Document" tab
4. Fill in document details
5. Click "Add Document"
6. Switch to "Search" tab
7. Enter search query
8. View results

### Workflow 2: Programmatic Indexing

```bash
# Index multiple documents
for i in {1..100}; do
  curl -X POST http://localhost:8080/api/documents \
    -H "Content-Type: application/json" \
    -d "{\"id\":\"doc-$i\",\"title\":\"Document $i\",\"content\":\"Content for document $i\",\"indexName\":\"bulk\"}"
done

# Search all documents
curl "http://localhost:8080/api/search?q=content&index=bulk"
```

### Workflow 3: Multi-Index Setup

```bash
# Create news index
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{"id":"news-1","title":"Breaking News","content":"...","indexName":"news"}'

# Create blogs index
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{"id":"blog-1","title":"Blog Post","content":"...","indexName":"blogs"}'

# Search specific index
curl "http://localhost:8080/api/search?q=news&index=news"
```

## Getting Help

1. Check QUICKSTART.md for basic setup
2. Read ARCHITECTURE.md for technical details
3. Look at SearchEngineExample.java for code examples
4. Check console output for error messages
5. Try simpler queries first
6. Verify documents are properly indexed

## Known Limitations

1. **In-Memory Only**: No persistence between restarts
2. **Single Node**: Not distributed
3. **Basic Stemming**: ISRI only, no advanced morphology
4. **No Caching**: Queries recalculated every time
5. **API Only**: No admin UI for index management
6. **Performance**: Limited by available RAM

## Future Improvements

- Database persistence layer
- Query result caching
- Distributed indexing
- Advanced Arabic morphology
- Admin dashboard
- Query suggestions
- Spell checking
- Faceted search
- Range queries
- Wildcard support

