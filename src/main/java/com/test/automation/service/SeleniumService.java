package com.test.automation.service;

import org.springframework.stereotype.Service;

/**
 * Service class
 *
 */
@Service
public interface SeleniumService {
	
	/**
	 * Method to process the messages received from
	 * the SQS topic
	 * 
	 * @param message - Message received from SQS queue
	 */
	public void processMessage(String message);
	
	/**
	 * Method to send message to the SQS topic
	 * 
	 * @param message - Message to be sent
	 */
	public void sendMessage(String message);

}
