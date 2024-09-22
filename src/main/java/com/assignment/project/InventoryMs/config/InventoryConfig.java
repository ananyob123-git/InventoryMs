package com.assignment.project.InventoryMs.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class InventoryConfig {

    @Autowired
    private EurekaDiscoveryClient discoveryClient;

    @Retryable(
            value = {RuntimeException.class}, // specify the exception types to retry on
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 1000, // initial delay in milliseconds
                    multiplier = 2, // multiplier for delay between attempts
                    maxDelay = 10000 // maximum delay in milliseconds
            )
    )
    public ServiceInstance getServiceInstance(String serviceName)
    {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            throw new RuntimeException("No instances found for "+serviceName);
        }
        return instances.get(0);
    }

    @Bean
    @LoadBalanced
    public WebClient getWebClient(WebClient.Builder webClientBuilder)
    {
        /*ServiceInstance instance = getServiceInstance("BookingMs");
        String hostname = instance.getHost();
        int port = instance.getPort();*/

        return webClientBuilder
                .baseUrl("http://localhost:8081")
                .filter(new LoggingWebClientFilter())
                .build();

    }
}
