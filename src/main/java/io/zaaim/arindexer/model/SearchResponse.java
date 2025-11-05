package io.zaaim.arindexer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SearchResponse {
    @JsonProperty("query")
    private String query;

    @JsonProperty("results")
    private List<SearchResult> results;

    @JsonProperty("totalResults")
    private int totalResults;

    @JsonProperty("executionTimeMs")
    private long executionTimeMs;

    public SearchResponse() {}

    public SearchResponse(String query, List<SearchResult> results, int totalResults, long executionTimeMs) {
        this.query = query;
        this.results = results;
        this.totalResults = totalResults;
        this.executionTimeMs = executionTimeMs;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
}
package io.zaaim.arindexer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Document {
    @JsonProperty("id")
    private String id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("title")
    private String title;

    @JsonProperty("indexName")
    private String indexName;

    public Document() {}

    public Document(String id, String content, String title, String indexName) {
        this.id = id;
        this.content = content;
        this.title = title;
        this.indexName = indexName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", indexName='" + indexName + '\'' +
                ", content length=" + (content != null ? content.length() : 0) +
                '}';
    }
}

