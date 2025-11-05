package io.zaaim.arindexer.examples;

import io.zaaim.arindexer.model.Document;
import io.zaaim.arindexer.model.SearchResult;
import io.zaaim.arindexer.service.IndexService;
import io.zaaim.arindexer.service.SearchService;
import safar.basic.morphology.stemmer.impl.ISRIStemmer;

import java.util.List;

/**
 * Example usage of the Arabic Search Engine
 * This demonstrates how to use the search engine programmatically
 */
public class SearchEngineExample {
    public static void main(String[] args) {
        // Initialize stemmer and services
        ISRIStemmer stemmer = new ISRIStemmer();
        IndexService indexService = new IndexService(stemmer);
        SearchService searchService = new SearchService(indexService);

        // Example 1: Basic Document Indexing
        System.out.println("=== Example 1: Basic Document Indexing ===");
        indexBasicDocuments(indexService);

        // Example 2: Search
        System.out.println("\n=== Example 2: Search ===");
        performSearch(searchService);

        // Example 3: Multiple Indexes
        System.out.println("\n=== Example 3: Multiple Indexes ===");
        multipleIndexesExample(indexService, searchService);

        // Example 4: Related Documents
        System.out.println("\n=== Example 4: Related Documents ===");
        relatedDocumentsExample(searchService);
    }

    /**
     * Example 1: Index basic documents
     */
    private static void indexBasicDocuments(IndexService indexService) {
        // Create sample documents
        Document doc1 = new Document(
                "article-1",
                "الذكاء الاصطناعي هو محاكاة للذكاء البشري في الآلات",
                "الذكاء الاصطناعي",
                "default"
        );

        Document doc2 = new Document(
                "article-2",
                "تعلم الآلة هو فرع من فروع الذكاء الاصطناعي",
                "تعلم الآلة",
                "default"
        );

        Document doc3 = new Document(
                "article-3",
                "البرمجة هي عملية كتابة التعليمات للحاسوب",
                "البرمجة",
                "default"
        );

        // Index documents
        indexService.indexDocument(doc1);
        indexService.indexDocument(doc2);
        indexService.indexDocument(doc3);

        System.out.println("✓ Indexed 3 documents");
        System.out.println("✓ Total documents: " + indexService.getDocumentCount("default"));
    }

    /**
     * Example 2: Perform searches
     */
    private static void performSearch(SearchService searchService) {
        // Search 1: Single term search
        List<SearchResult> results1 = searchService.search("الذكاء", "default", 10);
        System.out.println("\nSearch 1: 'الذكاء'");
        System.out.println("Results: " + results1.size());
        for (SearchResult result : results1) {
            System.out.println("  - " + result.getTitle() + " (Score: " +
                    String.format("%.4f", result.getScore()) + ")");
        }

        // Search 2: Multi-term search
        List<SearchResult> results2 = searchService.search("تعلم آلة", "default", 10);
        System.out.println("\nSearch 2: 'تعلم آلة'");
        System.out.println("Results: " + results2.size());
        for (SearchResult result : results2) {
            System.out.println("  - " + result.getTitle() + " (Score: " +
                    String.format("%.4f", result.getScore()) + ")");
        }
    }

    /**
     * Example 3: Multiple indexes
     */
    private static void multipleIndexesExample(IndexService indexService,
                                                SearchService searchService) {
        // Create index 1: Technology
        Document techDoc = new Document(
                "tech-1",
                "البرمجة والتطوير هما من أهم مجالات التكنولوجيا الحديثة",
                "التكنولوجيا",
                "technology"
        );

        // Create index 2: Sports
        Document sportsDoc = new Document(
                "sports-1",
                "كرة القدم هي لعبة جماعية شهيرة جدا في العالم",
                "كرة القدم",
                "sports"
        );

        // Index documents in different indexes
        indexService.indexDocument(techDoc);
        indexService.indexDocument(sportsDoc);

        // Search in technology index
        System.out.println("\nTechnology Index Search:");
        List<SearchResult> techResults = searchService.search("تطوير", "technology", 5);
        for (SearchResult result : techResults) {
            System.out.println("  ✓ " + result.getTitle());
        }

        // Search in sports index
        System.out.println("\nSports Index Search:");
        List<SearchResult> sportsResults = searchService.search("لعبة", "sports", 5);
        for (SearchResult result : sportsResults) {
            System.out.println("  ✓ " + result.getTitle());
        }

        // List all indexes
        System.out.println("\nAll Indexes:");
        for (String indexName : indexService.getAllIndexNames()) {
            System.out.println("  - " + indexName + ": " +
                    indexService.getDocumentCount(indexName) + " documents");
        }
    }

    /**
     * Example 4: Find related documents
     */
    private static void relatedDocumentsExample(SearchService searchService) {
        // Find documents related to article-1
        System.out.println("\nDocuments related to 'الذكاء الاصطناعي':");
        List<SearchResult> related = searchService.getRelatedDocuments(
                "article-1",
                "default",
                3
        );

        if (related.isEmpty()) {
            System.out.println("  No related documents found");
        } else {
            for (SearchResult result : related) {
                System.out.println("  ✓ " + result.getTitle() +
                        " (Similarity: " + String.format("%.4f", result.getScore()) + ")");
            }
        }
    }
}

