package com.github.viniciuslj.elasticsearch;

import org.elasticsearch.ElasticsearchException;

public class IndexDocumentException extends ElasticsearchException {
    public IndexDocumentException() {
        super("Document indexing failed on all Shards");
    }
}
