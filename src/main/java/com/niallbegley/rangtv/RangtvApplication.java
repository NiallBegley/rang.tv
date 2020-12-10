package com.niallbegley.rangtv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RangtvApplication {

	public static void main(String[] args) {
		SpringApplication.run(RangtvApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
	        request.getHeaders().add("User-Agent", "com.niallbegley.rangtv:1.0");
	        return execution.execute(request, body);
	    })).build();
		
		return restTemplate;
	}

}
