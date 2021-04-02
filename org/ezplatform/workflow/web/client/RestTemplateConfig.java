package org.ezplatform.workflow.web.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * 实际开发中要避免每次http请求都实例化httpclient
 * restTemplate默认会复用连接,保证restTemplate单例即可
 * 参考资料：
 * https://www.cnblogs.com/xrq730/p/10963689.html
 * https://halfrost.com/advance_tcp/
 */

@Configuration
public class RestTemplateConfig {

    @Bean
    RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter c : messageConverters) {
            if (c instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) c).setDefaultCharset(Charset.forName("utf-8"));
            }
        }

        return restTemplate;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.resttemplate")
    HttpClientProperties httpClientProperties() {
        return new HttpClientProperties();
    }

    @Bean
    ClientHttpRequestFactory clientHttpRequestFactory(HttpClientProperties httpClientProperties) {
        //如果不使用HttpClient的连接池，则使用restTemplate默认的SimpleClientHttpRequestFactory,底层基于HttpURLConnection
        if (!httpClientProperties.isUseHttpClientPool()) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(httpClientProperties.getConnectTimeout());
            factory.setReadTimeout(httpClientProperties.getReadTimeout());
            return factory;
        }

        //HttpClient4.3及以上版本不手动设置HttpClientConnectionManager,默认就会使用连接池PoolingHttpClientConnectionManager
        /*HttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(httpClientProperties.getMaxTotalConnect()).setr
                .setMaxConnPerRoute(httpClientProperties.getMaxConnectPerRoute()).evictExpiredConnections().build();
        */
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();  //创建http访问连接池
        int port=Integer.valueOf(httpClientProperties.getKingPort());
        SchemeRegistry schemeRegistry = new SchemeRegistry();  
		schemeRegistry.register(new Scheme("http", port, PlainSocketFactory.getSocketFactory()));  
		
		cm.setMaxTotal(httpClientProperties.getMaxTotalConnect());//设置最大连接数  
		cm.setDefaultMaxPerRoute(httpClientProperties.getMaxConnectPerRoute());//设置路由的默认最大连接 
        HttpHost localhost = new HttpHost(httpClientProperties.getKingIp(),port );
        cm.setMaxPerRoute(new HttpRoute(localhost), httpClientProperties.getMaxRoute());

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,int executionCount, HttpContext context) {
                if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }
 
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
 
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).setRetryHandler(httpRequestRetryHandler).build();
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(httpClientProperties.getConnectTimeout());
        factory.setReadTimeout(httpClientProperties.getReadTimeout());
        factory.setConnectionRequestTimeout(httpClientProperties.getConnectionRequestTimeout());
        return factory;
    }

}

