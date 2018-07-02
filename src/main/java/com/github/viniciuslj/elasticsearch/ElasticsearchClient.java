package com.github.viniciuslj.elasticsearch;

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
    private IndexRequestBuilder index;

    private ElasticsearchClient(ParametersElasticsearch parameters) throws UnknownHostException {
        this.parameters = parameters;

        Settings settings = Settings.builder()
                .put("cluster.name", parameters.getClusterName()).build();

        transportClient = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.
                        getByName(parameters.getHost()), parameters.getPort()));

        index = prepareIndex();
    }

    public static ElasticsearchClient createFromParameters(ParametersElasticsearch parameters) throws UnknownHostException {
        return new ElasticsearchClient(parameters);
    }

    private IndexRequestBuilder prepareIndex() {
        // "_doc" ? => https://www.elastic.co/guide/en/elasticsearch/reference/current/removal-of-types.html
        return transportClient.prepareIndex(parameters.getIndexName(), "_doc");
    }

    public IndexResponse addDocumnet(Map<String, Object> json) {
        IndexResponse response = index.setSource(json).get();
        if(response.getShardInfo().getSuccessful() == 0) {
            throw new IndexDocumentException();
        }

        return response;
    }

    public void close() {
        transportClient.close();
    }
}
