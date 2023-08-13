package com.org.ps.martek.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderHealthController {
	 
	    
	@GetMapping("/actuator/health")
    public String healthy() {
			    
        return "Success";
    }

	
	
}
