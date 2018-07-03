package com.github.viniciuslj.elasticsearch;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class ElasticsearchClient {
    private ParametersElasticsearch parameters;
    private TransportClient transportClient;
    private BulkRequestBuilder bulkRequest;

    private ElasticsearchClient(ParametersElasticsearch parameters) throws UnknownHostException {
        this.parameters = parameters;

        Settings settings = Settings.builder()
                .put("cluster.name", parameters.getClusterName()).build();

        transportClient = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress
                		.getByName(parameters.getHost()), parameters.getPort()));
    }

    public static ElasticsearchClient createFromParameters(ParametersElasticsearch parameters) throws UnknownHostException {
        return new ElasticsearchClient(parameters);
    }

    private IndexRequestBuilder prepareIndex(String id) {
        // "_doc" ? => https://www.elastic.co/guide/en/elasticsearch/reference/current/removal-of-types.html
        return transportClient.prepareIndex(parameters.getIndexName(), "_doc", id);
    }

    public IndexResponse addDocumnet(Map<String, Object> json, String id) {
        IndexResponse response = prepareIndex(id).setSource(json).get();
        if(response.getShardInfo().getSuccessful() == 0) {
            throw new IndexDocumentException();
        }

        return response;
    }

    public void prepareBulk() {
        bulkRequest = transportClient.prepareBulk();
    }

    public void addDocumentInBulk(Map<String, Object> json, String id) {
        bulkRequest.add(prepareIndex(id).setSource(json));
    }

    public boolean sendBulk() {
        BulkResponse bulkResponse = bulkRequest.get();
        bulkRequest = null;
        return !bulkResponse.hasFailures();
    }

    public void close() {
        transportClient.close();
    }
}
