package com.scheme.reactive.scheme_reactive.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@EnableScheduling
public class AWSConfig {
    private final Logger log = LoggerFactory.getLogger(AWSConfig.class);

    @Value("${amazon.aws.accesskey}")
    private String accessKey;

    @Value("${amazon.aws.secretkey}")
    private String secretKey;

    @Value("${amazon.aws.region}")
    private String region;

    @Bean
    public AWSCredentials getAWSCredentials(){
        log.info("getAWSCredentials");
        AWSCredentials credentials;
        if (!"NONE".equals(accessKey)) {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            credentials = new AWSStaticCredentialsProvider(awsCreds).getCredentials();
        } else {
            credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        }
        return credentials;
    }

    @Bean
    public DynamoDbClient getDynamoDbClient() {
        log.info("getDynamoDbClient");
        AwsCredentialsProvider credentialsProvider;
        if (!"NONE".equals(accessKey)) {
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
            credentialsProvider = StaticCredentialsProvider.create(awsCredentials);
        } else {
            credentialsProvider = DefaultCredentialsProvider.builder().build();
        }
        return DynamoDbClient.builder().region(Region.of(region))
                .credentialsProvider(credentialsProvider).build();
    }

    @Bean
    public AWSSecretsManager awsSecretsManagerDev() {
        log.info("awsCognitoIdentityClient");
        if (!"NONE".equals(accessKey)) {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            return AWSSecretsManagerClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();
        } else {
            InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider(true);
            return AWSSecretsManagerClientBuilder.standard()
                    .withCredentials(provider)
                    .build();
        }
    }


    @Bean
    public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getDynamoDbClient())
                .build();
    }
}
