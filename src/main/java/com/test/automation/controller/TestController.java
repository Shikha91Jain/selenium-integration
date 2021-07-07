package com.test.automation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.automation.service.SeleniumService;

/**
 * Rest Controller
 *
 */
@RestController
@RequestMapping(value = "/")
public class TestController {
	
	@Autowired
	private SeleniumService service;

    @PostMapping(value = "/send",
    		consumes = { "application/json; charset=UTF-8" },
        produces = { "application/json; charset=UTF-8" })
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
    	service.sendMessage(message);
    	return ResponseEntity.ok("{\"status\":\"Success\"}");
    }

}
