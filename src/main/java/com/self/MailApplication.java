package com.self;

import com.self.util.HttpClientHelper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableAsync
@EnableRetry
public class MailApplication {

  @Bean
  @LoadBalanced
  public RestTemplate createRestTemplate() {
    return new RestTemplate(new HttpComponentsClientHttpRequestFactory(HttpClientHelper.createHttpClient()));
  }

  public static void main(String[] args) throws Exception {
    ApplicationContext applicationContext = new SpringApplicationBuilder(MailApplication.class).web(true).run(args);
  }

}