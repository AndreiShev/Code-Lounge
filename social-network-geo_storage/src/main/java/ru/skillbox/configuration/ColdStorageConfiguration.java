package ru.skillbox.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ColdStorageConfiguration {
  @Value("${s3.access_key_id}")
  private String s3AccessKey;
  @Value("${s3.secret_access_key}")
  private String s3SecretKey;
  @Value("${s3.service-endpoint}")
  private String serviceEndpoint;
  @Value("${s3.signing-region}")
  private String signingRegion;

  @Bean
  public AmazonS3 s3Client () {
    return AmazonS3ClientBuilder.standard()
          .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3AccessKey, s3SecretKey)))
          .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, signingRegion))
          .build();
  }
}
