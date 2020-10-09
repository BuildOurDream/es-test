package test.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: nobody
 * @Date: 2020-10-05 13:10
 * @Desc:
 */
@Configuration
public class EsConfig {

    @Bean
    public RestHighLevelClient highLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
    }
}
