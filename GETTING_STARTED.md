# 🚀 Getting Started - Arabic Search Engine

Welcome! This is your starting point for the complete Arabic Search Engine with TF-IDF.

## ⚡ Quick Start (5 Minutes)

### 1️⃣ Build the Project
```bash
cd /Users/pro/IdeaProjects/arabic-search-engine
mvn clean install
```

### 2️⃣ Run the Application
```bash
mvn exec:java -Dexec.mainClass="io.zaaim.arindexer.Main"
```

You should see:
```
╔════════════════════════════════════════════════════════════════╗
║          🔍 Arabic Search Engine with TF-IDF                  ║
╠════════════════════════════════════════════════════════════════╣
║  Server is running at: http://localhost:8080                  ║
║  Web UI:              http://localhost:8080/                  ║
...
```

### 3️⃣ Open Web Interface
Visit in your browser:
```
http://localhost:8080
```

### 4️⃣ Add a Test Document

Click **Add Document** tab and fill:
- **Title**: الذكاء الاصطناعي
- **Content**: الذكاء الاصطناعي هو محاكاة للذكاء البشري في الآلات
- **Index**: default

Click **Add Document**

### 5️⃣ Search

Click **Search** tab and:
1. Enter: الذكاء
2. Click **Search**
3. See results with scores

**That's it! You're ready to go! 🎉**

---

## 📚 Documentation Map

Choose your path based on what you want to do:

### 🎯 I want to...

**... get it running quickly**
→ You're already doing it! Just follow steps above.

**... understand how it works**
→ Read: ARCHITECTURE.md

**... use the API**
→ Read: QUICKSTART.md

**... solve a problem**
→ Read: FAQ_AND_TROUBLESHOOTING.md

**... see code examples**
→ Check: SearchEngineExample.java

**... understand what's implemented**
→ Read: IMPLEMENTATION_SUMMARY.md

**... get complete details**
→ Read: README.md

---

## 🎯 Core Features

### ✅ Web Interface
- Search documents
- Add documents  
- Manage indexes
- View statistics
- Arabic optimized (RTL)

### ✅ REST API
- Document management
- Search functionality
- Index control
- JSON format
- Error handling

### ✅ Search Engine
- TF-IDF vectorization
- Cosine similarity
- Multi-index support
- Related documents
- Scoring & ranking

### ✅ Arabic Support
- ISRI stemming
- Unicode tokenization
- Right-to-left layout
- UTF-8 encoding

---

## 🔗 Important API Endpoints

```
Web Interface:
  GET  /                          → Main UI
  GET  /ui                        → Alternative UI path

Search:
  GET  /api/search?q=term&index=name&limit=10     → Search
  GET  /api/search/related?docId=id&index=name    → Related docs

Documents:
  POST /api/documents             → Add document
  GET  /api/indexes/{name}/documents              → List all
  GET  /api/indexes/{name}/documents/{id}         → Get one
  DELETE /api/indexes/{name}/documents/{id}       → Delete

Indexes:
  GET  /api/indexes               → List all
  GET  /api/indexes/{name}        → Get info
  DELETE /api/indexes/{name}      → Delete index
  POST /api/indexes/{name}/rebuild                → Rebuild
```

---

## 💡 Example Workflows

### Workflow 1: Simple Search

**Via Web UI**:
1. Start application → http://localhost:8080
2. Add Document tab → Add a document
3. Search tab → Search for keywords
4. View results

**Via API**:
```bash
# Add
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{"id":"1","title":"عنوان","content":"محتوى","indexName":"default"}'

# Search
curl "http://localhost:8080/api/search?q=محتوى&index=default&limit=10"
```

### Workflow 2: Multiple Indexes

Create separate indexes for different content:
```bash
# News index
curl -X POST http://localhost:8080/api/documents \
  -d '{"id":"n1","title":"خبر","content":"محتوى خبر","indexName":"news"}'

# Blog index  
curl -X POST http://localhost:8080/api/documents \
  -d '{"id":"b1","title":"مدونة","content":"محتوى مدونة","indexName":"blogs"}'

# Search each index separately
curl "http://localhost:8080/api/search?q=خبر&index=news"
curl "http://localhost:8080/api/search?q=مدونة&index=blogs"
```

### Workflow 3: Find Related Documents

```bash
# After adding a document with ID "doc-1"
curl "http://localhost:8080/api/search/related?docId=doc-1&index=default"
```

---

## 🆘 Quick Troubleshooting

### "Port 8080 already in use"
Change port in Main.java (line 11):
```java
.port(8081)  // instead of 8080
```

### "No search results"
- Add more documents first
- Try simpler search terms
- Check document index name matches

### "Arabic text shows as ????"
- Ensure UTF-8 encoding in requests
- Check browser encoding (should be UTF-8)
- Try different browser

### "Cannot build / Maven error"
- Install Maven: `brew install maven`
- Or use Java IDE for compilation

---

## 📖 Learn Step by Step

### Level 1: Beginner (Start Here)
1. Follow "Quick Start" above
2. Add 3-4 test documents
3. Try different searches
4. Explore web UI tabs

**Time**: 10 minutes

### Level 2: Intermediate
1. Read QUICKSTART.md
2. Try API examples with curl
3. Create multiple indexes
4. Learn search parameters

**Time**: 30 minutes

### Level 3: Advanced
1. Read ARCHITECTURE.md
2. Understand TF-IDF algorithm
3. Study code in SearchEngineExample.java
4. Explore customization options

**Time**: 1-2 hours

---

## 🏗️ Project Structure

```
arabic-search-engine/
├── README.md                    ← Full documentation
├── QUICKSTART.md               ← Detailed setup guide  
├── ARCHITECTURE.md             ← System design
├── IMPLEMENTATION_SUMMARY.md   ← What's implemented
├── FAQ_AND_TROUBLESHOOTING.md  ← Common issues
├── PROJECT_COMPLETION_SUMMARY.md
├── GETTING_STARTED.md          ← You are here
│
├── pom.xml                      ← Maven config
│
└── src/main/java/io/zaaim/arindexer/
    ├── Main.java               ← Start here
    ├── controller/             ← Web API
    ├── service/                ← Business logic
    ├── model/                  ← Data models
    ├── util/                   ← Helper classes
    ├── analysis/               ← Arabic processing
    └── examples/               ← Code examples
```

---

## ✨ Features at a Glance

| Feature | Status | Details |
|---------|--------|---------|
| TF-IDF Search | ✅ | Full implementation |
| Arabic Support | ✅ | ISRI stemming, tokenization |
| Web Interface | ✅ | RTL optimized, responsive |
| REST API | ✅ | Complete endpoints |
| Multiple Indexes | ✅ | Independent indexes |
| Related Docs | ✅ | Document similarity |
| Filtering | ✅ | Custom filters |
| Statistics | ✅ | Index info, scores |

---

## 🎓 Key Concepts

### TF-IDF
**Term Frequency-Inverse Document Frequency**
- Ranks documents by relevance
- Accounts for common words
- Mathematical approach

### Cosine Similarity
**How similar are two documents?**
- Compares vectors in high-dimensional space
- Score 0-1 (0=no match, 1=exact)
- Used for ranking results

### Stemming
**Normalize Arabic words**
- "كتاب" (book) = "كتب" (books) after stemming
- Better matching of word variations
- Uses ISRI algorithm

### Inverted Index
**Fast document lookup**
- Maps each term to documents containing it
- Like book index but for documents
- Enables fast searching

---

## 🚀 What's Next?

1. ✅ Get it running (done!)
2. ✅ Explore web interface (next)
3. ✅ Try some searches (next)
4. ✅ Read documentation as needed
5. ✅ Experiment with API
6. ✅ Integrate into your project

---

## 📞 Need Help?

1. **Basic Questions** → FAQ_AND_TROUBLESHOOTING.md
2. **How Things Work** → ARCHITECTURE.md
3. **Setup Issues** → QUICKSTART.md
4. **Code Examples** → SearchEngineExample.java
5. **Complete Info** → README.md

---

## 🎉 You're All Set!

**Application Ready**: ✅
**Documentation**: ✅  
**Examples**: ✅

Start exploring! Open http://localhost:8080 and enjoy your Arabic Search Engine! 🚀

---

**Questions?** → Check FAQ_AND_TROUBLESHOOTING.md
**Want details?** → Read ARCHITECTURE.md
**Need examples?** → See SearchEngineExample.java
**Get help?** → All documentation is in this folder!

**Happy Searching! 🔍**

