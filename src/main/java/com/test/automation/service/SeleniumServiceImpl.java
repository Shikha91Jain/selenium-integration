package com.test.automation.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeleniumServiceImpl implements SeleniumService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumServiceImpl.class);

	@Value("${remote.web.driver.url.chrome}")
	private String chromeRemoteWebDriverUrl;

	@Value("${remote.web.driver.url.firefox}")
	private String firefoxRemoteWebDriverUrl;

	@Value("${test.queue.name}")
	private String queueName;

	@Value("${driver.implicit.wait.timeout}")
	private int implicitWaitTimeout;

	@Value("${driver.page.load.wait.timeout}")
	private int pageLoadTimeout;

	@Autowired
	private QueueMessagingTemplate template;


	@Override
	public void sendMessage(String message) {
		LOGGER.debug("In sendMessage method with message: {}", message);
		template.convertAndSend(queueName, message);
	}


	@Override
	public void processMessage(String message) {

		LOGGER.debug("In processMessage method with message: {}", message);

		// Convert the string payload into JSON and extract the
		// respective variables
		var json = new JSONObject(message);
		var searchString = json.getString("search");
		var browserName = json.getString("browser");


		// Define the capabilities and node url based 
		// on the browser name
		Capabilities capabilities;
		String nodeUrl;

		switch(browserName) {
		case "firefox":
			capabilities = new FirefoxOptions();
			nodeUrl = firefoxRemoteWebDriverUrl;
			break;
		case "chrome":
		default:
			capabilities = new ChromeOptions();
			nodeUrl = chromeRemoteWebDriverUrl;
			break;
		}
		
		LOGGER.debug("nodeUrl: {}", nodeUrl);
		performGoogleSearch(capabilities, nodeUrl, searchString);

		LOGGER.debug("In processMessage method end");
	}

	/**
	 * Method to peform gooogle search for the given search string and capabilities
	 * 
	 * @param capabilities - Desired capabilities to be configured in the
	 * remote driver
	 * @param nodeUrl - Remote web driver URL
	 * @param searchString - Search string for google search
	 */
	private void performGoogleSearch(Capabilities capabilities, String nodeUrl, String searchString) {
		try{
			// Create remote web driver with the nodeUrl and capabilities
			WebDriver driver = new RemoteWebDriver(new URL(nodeUrl), capabilities);

			// Configure settings for the driver
			// Implicit wait timeout
			driver.manage().timeouts().implicitlyWait(implicitWaitTimeout, TimeUnit.SECONDS);
			// Page load Timeout
			driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
			// Configure maximum window for driver session
			driver.manage().window().maximize();
			
			
			// Execute get on Google URL for perform search
			driver.get("https://www.google.com/");

			// Identify th query/input element
			WebElement p = driver.findElement(By.name("q"));

			// Enter the kets that needs to be searched and submit()
			p.sendKeys(searchString);
			p.submit();

			// Request driver to wait until the search page loads and we are
			// able to get the title containing the search string
			var w = new WebDriverWait(driver, 10);
			w.until(ExpectedConditions.titleContains(searchString));

			LOGGER.info("Browser Name: {}, Driver title: {}, URL: {}", ((RemoteWebDriver) driver).getCapabilities().getBrowserName(), 
					driver.getTitle(), driver.getCurrentUrl());

			// Close the driver
			driver.close();
		} catch(MalformedURLException e) {
			LOGGER.error("MalformedURLException in performGoogleSearch for nodeUrl: {}", nodeUrl, e);
		}
	}

}
