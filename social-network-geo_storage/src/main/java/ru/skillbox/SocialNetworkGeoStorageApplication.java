package ru.skillbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
@EnableAspectJAutoProxy
public class SocialNetworkGeoStorageApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkGeoStorageApplication.class, args);
	}

}
