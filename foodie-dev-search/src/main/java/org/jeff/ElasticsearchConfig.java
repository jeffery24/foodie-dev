//package org.jeff;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
//import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
//
////@ConfigurationProperties(prefix = "elasticsearch")
//@Configuration
//public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {
//
//    @Value("182.92.131.93")
//    private String host;
//
//    @Value(value = "9200")
//    private Integer port;
//
//    //重写父类方法
//    @Override
//    public RestHighLevelClient elasticsearchClient() {
//        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
//        return new RestHighLevelClient(builder);
//    }
//
//    @Bean("esRestTemplate")
//    public ElasticsearchRestTemplate esRestTemplate() {
//        return new ElasticsearchRestTemplate(elasticsearchClient());
//    }
//
//
//    public String getHost() {
//        return host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public Integer getPort() {
//        return port;
//    }
//
//    public void setPort(Integer port) {
//        this.port = port;
//    }
//
//    public ElasticsearchConfig() {
//    }
//
//
//}
