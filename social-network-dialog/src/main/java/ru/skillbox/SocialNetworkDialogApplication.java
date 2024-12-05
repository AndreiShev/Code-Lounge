package ru.skillbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.skillbox.client")
public class SocialNetworkDialogApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkDialogApplication.class, args);

	}

}
