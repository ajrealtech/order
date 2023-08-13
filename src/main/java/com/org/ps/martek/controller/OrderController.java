package com.org.ps.martek.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.org.ps.martek.dto.OrderConfirm;
import com.org.ps.martek.service.OrderService;

@RestController
public class OrderController {
	
	  @Autowired
	 private   OrderService productService;
	 
	  /*
	   * name	-  	  Product Name
	   * location -   Delivery location
	   * store-id  -  store in which product is scanned
	   */
	  
	@PostMapping("/order")
	public String allProduct(@RequestBody OrderConfirm product) {
		///Product p = new Product();
		//p.setName(name);
		return productService.findProduct(product);
	}
}
