package com.test.automation.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

/**
 * Configuration class for AWS SQS
 *
 */
@Configuration
@EnableSqs
public class AwsSQSConfig {
	
	// Region to be used
	@Value("${sqs.aws.region}")
	private String region;

	// Access Key
	@Value("${sqs.aws.credentials.access-key}")
	private String accessKey;

	// Secret Key
	@Value("${sqs.aws.credentials.secret-key}")
	private String secretKey;
	
	@Autowired
	private SimpleMessageListenerContainer listenerContainer;

	/**
	 * Queue messaging Template bean
	 * 
	 * @param amazonSQSAsync - AmazonSQSAsync interface to be configured 
	 * in template
	 * @return QueueMessagingTemplate - Created bean
	 */
	@Bean
	public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
		return new QueueMessagingTemplate(amazonSQSAsync);
	}

	/**
	 * AmazonSQSAsync bean
	 * 
	 * @param basicAWSCredentials - Credentials to be used to connect
	 * to AWS
	 * @return AmazonSQSAsync - Created bean
	 */
	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAsync(BasicAWSCredentials basicAWSCredentials) {
		
		return AmazonSQSAsyncClientBuilder.standard()
		.withRegion(region)
		.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
		.build();

	}
	
	/**
	 * BasicAWSCredentials bean
	 * 
	 * @return BasicAWSCredentials - Created bean
	 */
	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}
	
	/**
	 * SimpleMessageListenerContainerFactory bean
	 * 
	 * @param amazonSQSAsync - AmazonSQSAsync client used in
	 * the listener
	 * @return SimpleMessageListenerContainerFactory - Created bean
	 */
	@Bean
	public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSQSAsync) {
	    var msgListenerContainerFactory = new SimpleMessageListenerContainerFactory();
	    msgListenerContainerFactory.setAmazonSqs(amazonSQSAsync);
	    msgListenerContainerFactory.setWaitTimeOut(20);
	    return msgListenerContainerFactory;
	}
	
	@PostConstruct
	public void setupListenerContainer() {
		listenerContainer.setQueueStopTimeout(25000);
	}

}
