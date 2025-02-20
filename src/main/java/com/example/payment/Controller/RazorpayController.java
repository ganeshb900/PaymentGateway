package com.example.payment.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.payment.Service.RazorpayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/razorpay")  // Add base path
public class RazorpayController {

    @Autowired
    private RazorpayService razorpayService;

    private   String orderResponse ;
    ObjectMapper objectMapper = new ObjectMapper();
    
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam int amount) {
        
    	try {
    		  boolean isValid = razorpayService.verifyRazorpayCredentials();
    		  if (isValid) {
    	            System.out.print( ResponseEntity.ok("Razorpay API Keys are valid!"));
    	        } else {
    	            return ResponseEntity.status(401).body("Invalid Razorpay API Keys!");
    	        }
    		  orderResponse = razorpayService.createOrder(amount);
          
           

            
           
            
            if (orderResponse != null) {
            	
            	
            	
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(orderResponse);
            
            
            } else {
                return ResponseEntity.badRequest().body("Failed to create Razorpay order.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while creating the order.");
        }
    }
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> paymentData) {
        String paymentId = paymentData.get("razorpay_payment_id");
        String orderId = paymentData.get("razorpay_order_id");
        String signature = paymentData.get("razorpay_signature");

        // TODO: Implement signature verification logic

        return ResponseEntity.ok("Payment verified successfully!");
    }
    
    @GetMapping("/verify-credentials")
    public ResponseEntity<?> verifyCredentials() {
        boolean isValid = razorpayService.verifyRazorpayCredentials();
        if (isValid) {
            return ResponseEntity.ok("Razorpay API Keys are valid!");
        } else {
            return ResponseEntity.status(401).body("Invalid Razorpay API Keys!");
        }
    }
    
    @GetMapping("/getResponse")
    public ResponseEntity<?> getResponse() throws JsonMappingException, JsonProcessingException {
	
    	 Map<String, Object> responseMap = objectMapper.readValue(orderResponse, Map.class);
    	return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseMap);
       
       
    } 
    
    // Generate a UPI payment link
    @PostMapping("/create-upi-link")
    public ResponseEntity<?> createUPILink(@RequestParam String upiId, @RequestParam int amount) {
        String response = razorpayService.createUPIPaymentLink(upiId, amount);
        return ResponseEntity.ok(response);
    }

}
