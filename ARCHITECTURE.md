# Architecture & Implementation Guide

## Overview

The Arabic Search Engine is built with a modular, layered architecture that implements TF-IDF (Term Frequency-Inverse Document Frequency) vectorization for effective Arabic text search.

## System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Web Layer (HTTP)                      │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Helidon Web Server (Port 8080)                 │   │
│  │  - REST API Endpoints                           │   │
│  │  - Web UI (Arabic optimized)                    │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                Controller Layer                         │
│  ┌──────────────────────────────────────────────────┐   │
│  │  SearchController                               │   │
│  │  - API request handling                         │   │
│  │  - JSON serialization/deserialization          │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  WebUIController                                │   │
│  │  - HTML/CSS serving                             │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                Service Layer                           │
│  ┌──────────────────────────────────────────────────┐   │
│  │  IndexService                                    │   │
│  │  - Document storage & management                │   │
│  │  - TF-IDF vector generation                     │   │
│  │  - Multiple index support                       │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  SearchService                                   │   │
│  │  - Query processing & execution                 │   │
│  │  - Cosine similarity calculation                │   │
│  │  - Result ranking & filtering                   │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  QueryProcessor                                  │   │
│  │  - Tokenization                                 │   │
│  │  - Arabic stemming (ISRI)                       │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│              Analysis & Processing Layer                │
│  ┌──────────────────────────────────────────────────┐   │
│  │  ArabicTokenizer                                 │   │
│  │  - Text tokenization (regex-based)              │   │
│  │  - Unicode Arabic character detection           │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  CustomArabicAnalyzer (Lucene)                   │   │
│  │  - Tokenization with Lucene                     │   │
│  │  - Stemming filters                             │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│               Data & Index Layer                        │
│  ┌──────────────────────────────────────────────────┐   │
│  │  IndexMaps                                       │   │
│  │  - Inverted index                               │   │
│  │  - Term frequency tracking                      │   │
│  │  - IDF calculation                              │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  TFIDFVector                                     │   │
│  │  - Document vectors                             │   │
│  │  - Cosine similarity computation                │   │
│  │  - Vector magnitude                             │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Models                                          │   │
│  │  - Document                                     │   │
│  │  - SearchResult                                 │   │
│  │  - SearchResponse                               │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

## Component Details

### 1. IndexService
**Location**: `service/IndexService.java`

Manages document indexing and TF-IDF vector creation.

**Key Methods**:
- `indexDocument(Document)`: Add/update document in index
- `removeDocument(String, String)`: Remove document from index
- `getDocument(String, String)`: Retrieve document
- `getAllDocuments(String)`: Get all docs in index
- `getIndexInfo(String)`: Get index statistics
- `rebuildIndex(String)`: Recalculate TF-IDF values

**Data Structures**:
```
indexes: Map<String, IndexMaps>
  └─ Maintains inverted index for each index name

documents: Map<String, Map<String, Document>>
  └─ Stores actual documents by index and ID

tfidfVectors: Map<String, Map<String, TFIDFVector>>
  └─ Caches TF-IDF vectors for efficient search
```

### 2. SearchService
**Location**: `service/SearchService.java`

Performs search operations using cosine similarity.

**Key Methods**:
- `search(String, String, int)`: Search documents
- `getRelatedDocuments(String, String, int)`: Find similar docs
- `searchWithFilter(...)`: Search with custom filters

**Algorithm**:
1. Build query TF-IDF vector
2. Calculate cosine similarity with all document vectors
3. Sort by score (descending)
4. Return top N results

**Cosine Similarity**:
```
similarity = dotProduct / (queryMagnitude * docMagnitude)
```

### 3. IndexMaps
**Location**: `util/IndexMaps.java`

Manages the inverted index and TF-IDF calculations.

**Data Structures**:
```
invertedIndex: Map<String, Set<String>>
  └─ term -> {docId1, docId2, ...}

termDocumentFrequency: Map<String, Map<String, Integer>>
  └─ term -> {docId -> frequency}

idfValues: Map<String, Double>
  └─ term -> IDF value

documentTerms: Map<String, Set<String>>
  └─ docId -> {term1, term2, ...}
```

**Key Methods**:
- `addDocument(String, Set<String>)`: Add document to index
- `removeDocument(String)`: Remove from index
- `getTermFrequency(String, String)`: Get TF for term in doc
- `getIDF(String)`: Get IDF for term
- `getDocumentsWithTerm(String)`: Get all docs with term

### 4. TFIDFVector
**Location**: `util/TFIDFVector.java`

Represents a document or query as a vector in high-dimensional space.

**Key Properties**:
```
termWeights: Map<String, Double>
  └─ term -> TF-IDF weight

magnitude: double
  └─ Vector normalization factor

documentId: String
  └─ Reference to document
```

**Key Methods**:
- `cosineSimilarity(TFIDFVector)`: Calculate similarity with another vector
- `getMagnitude()`: Get vector magnitude
- `getTermWeights()`: Get term weights

### 5. ArabicTokenizer
**Location**: `util/ArabicTokenizer.java`

Tokenizes Arabic text into individual words.

**Pattern**: `[\u0600-\u06FF]+` (Unicode Arabic range)

**Features**:
- Extracts Arabic words only
- Lowercase conversion
- Supports both Set and List output

### 6. QueryProcessor
**Location**: `service/QueryProcessor.java`

Processes user queries through tokenization and stemming.

**Process**:
1. Tokenize input query
2. Apply ISRI stemming to each token
3. Return set or list of stemmed tokens

## TF-IDF Algorithm

### Formula

```
TF-IDF(term, doc) = TF(term, doc) × IDF(term)

Where:
TF(term, doc) = frequency of term in document

IDF(term) = log10(totalDocuments / docsContainingTerm)

CosineSimilarity(q, d) = (q · d) / (||q|| × ||d||)
```

### Implementation Flow

#### Indexing
```
1. Receive Document
   ↓
2. Tokenize text (ArabicTokenizer)
   ↓
3. Apply stemming (ISRIStemmer)
   ↓
4. Calculate TF for each term
   ↓
5. Calculate IDF (recalculated for all terms)
   ↓
6. Calculate TF-IDF = TF × IDF
   ↓
7. Calculate vector magnitude (normalization)
   ↓
8. Store in memory (IndexMaps, TFIDFVector)
```

#### Searching
```
1. Receive Query
   ↓
2. Tokenize & stem query
   ↓
3. Build query TF-IDF vector
   ↓
4. For each document:
   a. Calculate cosine similarity
   b. Store score
   ↓
5. Sort by score (descending)
   ↓
6. Return top N results
```

## Multiple Index Support

Each index is independent:

```
Index "default"
├─ documents: {doc1, doc2, ...}
├─ invertedIndex: {term -> {doc1, doc2, ...}, ...}
└─ tfidfVectors: {doc1 -> vector1, ...}

Index "news"
├─ documents: {news1, news2, ...}
├─ invertedIndex: {term -> {news1, news2, ...}, ...}
└─ tfidfVectors: {news1 -> vector1, ...}

Index "blogs"
├─ documents: {blog1, blog2, ...}
├─ invertedIndex: {term -> {blog1, blog2, ...}, ...}
└─ tfidfVectors: {blog1 -> vector1, ...}
```

## API Endpoints

### Document Operations
```
POST   /api/documents
GET    /api/indexes/{indexName}/documents
GET    /api/indexes/{indexName}/documents/{docId}
DELETE /api/indexes/{indexName}/documents/{docId}
```

### Search Operations
```
GET    /api/search?q=query&index=name&limit=10
GET    /api/search/related?docId=id&index=name&limit=5
```

### Index Management
```
GET    /api/indexes
GET    /api/indexes/{indexName}
DELETE /api/indexes/{indexName}
POST   /api/indexes/{indexName}/rebuild
```

## Performance Characteristics

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| Index Document | O(m × log(n)) | m = tokens, n = vocabulary |
| Remove Document | O(m × log(n)) | m = tokens in document |
| Search | O(m × n) | m = query tokens, n = documents |
| Similar Docs | O(n × v) | n = documents, v = vocab size |
| Rebuild Index | O(d × m) | d = documents, m = avg tokens |

## Memory Usage

Approximate memory per 1000 documents with avg 500 words each:
- Vocabulary: ~50,000 terms
- Inverted index: ~2-3 MB
- TF-IDF vectors: ~5-7 MB
- Document storage: ~2-3 MB
- **Total: ~10-15 MB**

## Concurrency

Thread-safe operations using:
- `ConcurrentHashMap` for main data structures
- Atomic operations for counter updates
- No explicit locks (read-heavy workload)

## Stemming

Uses ISRI (Islamic Region Stemmer for Information retrieval):
- Removes Arabic prefixes: ال, و, ب, ل, etc.
- Removes suffixes: ات, ان, ين, etc.
- Normalizes character variations

## Error Handling

- Invalid queries return empty results
- Missing indexes return empty results
- Stemming failures fall back to original token
- JSON serialization errors return error response

## Future Optimizations

1. **Caching**: Cache frequently searched queries
2. **Indexing**: Use Lucene for disk-based indexing
3. **Compression**: Compress vectors and inverted index
4. **Distributed**: Implement sharding across multiple servers
5. **Analytics**: Track popular searches and queries
6. **Incremental**: Batch updates instead of per-document

