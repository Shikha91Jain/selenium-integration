package com.test.automation.listener;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import com.test.automation.service.SeleniumService;

/**
 * SQSListener component
 *
 */
@Component
public class SQSListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(SQSListener.class);
	
	@Autowired
	private SeleniumService service;
	
	/**
	 * SQS Listener for test queue
	 * 
	 * @param message - Message received from SQS queue
	 * @throws MalformedURLException 
	 */
	@SqsListener(value = "${test.queue.name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void receiveMessage(String message) {
		LOG.debug("Received message in SqsListener: {}", message);
		service.processMessage(message);
	}

}
