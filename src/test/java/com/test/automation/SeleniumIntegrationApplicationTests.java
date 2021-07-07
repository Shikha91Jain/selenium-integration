package com.test.automation;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.test.automation.controller.TestController;
import com.test.automation.listener.SQSListener;
import com.test.automation.service.SeleniumService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class SeleniumIntegrationApplicationTests {
	
	@Autowired
	TestController controller;
	
	@Autowired
	SeleniumService seleniumService;
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	SQSListener sqsListener;
	
	@MockBean
	QueueMessagingTemplate messagingTemplate;
	
	@Mock
	RemoteWebDriver driverMock;
	
	@Mock
	WebElement webElementMock;
	
	@Mock
	WebDriverWait webDriverWaitMock;
	
	@Mock
	Options optionsMock;
	
	@Mock
	Timeouts timeoutMock;

	
	private static final String CHROME_PAYLOAD = "{\"search\":\"GTA V\", \"browser\":\"chrome\"}";
	private static final String FIREFOX_PAYLOAD = "{\"search\":\"Fall Guys\", \"browser\":\"firefox\"}";
	
	@Test
	void sendMessage() throws Exception {
		doNothing().when(messagingTemplate).convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
		
		this.mockMvc.perform(post("/send").contentType(MediaType.APPLICATION_JSON_VALUE).content(CHROME_PAYLOAD))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.status", is("Success")));
	}
	
	@Test
	void processMessageChrome() throws Exception {
		this.sqsListener.receiveMessage(CHROME_PAYLOAD);
	}
	
	@Test
	void processMessageFirefox() throws Exception {
		this.sqsListener.receiveMessage(FIREFOX_PAYLOAD);
	}

}
