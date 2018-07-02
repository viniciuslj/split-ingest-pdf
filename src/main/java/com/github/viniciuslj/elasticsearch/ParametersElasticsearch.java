package com.github.viniciuslj.elasticsearch;

public interface ParametersElasticsearch {
    String getHost();
    int getPort();
    String getClusterName();
    String getIndexName();
}
