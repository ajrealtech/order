package com.org.ps.martek.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.ps.martek.data.ProductRepository;
import com.org.ps.martek.data.ProductWorkFlow;
import com.org.ps.martek.data.ProductWorkflowRepository;
import com.org.ps.martek.dto.OrderConfirm;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;


@Service
public class OrderService {

	
	//@Autowired
	private static ProductRepository productRepository;
	
	private static ProductWorkflowRepository productFlowRepository;
	
	

	Logger log = LoggerFactory.getLogger(OrderService.class);
	
	
	//private static  List<CustomerDTO> outTimeList ;
	
	@PostConstruct
	public void init() {
		OrderService.productRepository = wiredrepository;
		OrderService.productFlowRepository = wiredFlowrepository;
	}
	
	@Autowired
	ProductRepository wiredrepository;
	
	@Autowired
	ProductWorkflowRepository wiredFlowrepository;
	
	
	@Autowired
	EntityManager entity;
	
	
	public String findProduct(OrderConfirm product) {
		triggerWorkFlow(product);
		 return checkResponse(product);
	}
	
	private void triggerWorkFlow(OrderConfirm product) {
		publishWorkflow(product);
		publishToKafka(product);
	}
	
	private void publishToKafka(OrderConfirm product) {
		String brokers = "glider.srvs.cloudkafka.com:9094";
		String username = "eidflxfs";
		String password = "PxIX3BTEcbFJbljRiixvpCVAuuB1DkIN";
		KafkaService c = new KafkaService(brokers, username, password);
        c.produce(product);
		}
	
	private void publishWorkflow(OrderConfirm response) {
		ProductWorkFlow productWorkflow = productFlowRepository.getSearch(response.getFlowId(), "ORDER_COMPLETE");
		if(productWorkflow==null)
		productWorkflow = new ProductWorkFlow();
		String message="";
		try {
			 message = new ObjectMapper().writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean b= productWorkflow.getId()!=null;
		Long id = b==true?productWorkflow.getId():productFlowRepository.getLastID();
		productWorkflow.setId(id==null?1:id);
		productWorkflow.setMessage(message);
		productWorkflow.setToday(LocalDate.now().toString());
		productWorkflow.setWorkflowId(response.getFlowId());
		productWorkflow.setStatus("ORDER_PROGRESS");
		productWorkflow.setUsername(response.getUsername());
		productFlowRepository.saveAndFlush(productWorkflow);
	}
	
	private String checkResponse(OrderConfirm response) {
		entity.clear();
		while(true) {
			ProductWorkFlow pp = productFlowRepository.getSearch(response.getFlowId(), "ORDER_COMPLETE");
			if(pp!=null) {
				return pp.getMessage();
			}
		}
	}

}
