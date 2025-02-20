package com.example.payment.Service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import java.util.Base64;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class RazorpayService {
    private RazorpayClient razorpayClient;

    @Value("${payment.gateway.secretKey}")
    private String secretKey;

    @Value("${payment.gateway.publishableKey}")
    private String publishableKey;

    private static final String PAYMENT_LINK_API_URL = "https://api.razorpay.com/v1/payment_links";



    public RazorpayService() {
        try {
            razorpayClient = new RazorpayClient(secretKey, publishableKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    /**
     * This method creates a Razorpay order and returns the order details.
     *
     * @param amount the order amount in INR (multiplied by 100 to convert into paise)
     * @return JSON string containing the order details, including order ID
     */
    public String createOrder(int amount) {
        JSONObject options = new JSONObject(); // Prepare order options
        options.put("amount", amount * 100);   // Convert amount to paise (Razorpay accepts only paise)
        options.put("currency", "INR");       // Specify currency (INR)
        options.put("receipt", "TEST1"); // Custom receipt identifier for tracking orders
        options.put("payment_capture", 1);    // Automatically capture payments

        try {
            System.out.println("Requesting Razorpay Order Creation: " + options.toString());
            RazorpayClient razorpayClient = new RazorpayClient(publishableKey, secretKey);
            
            String response = razorpayClient.Orders.create(options).toString();
            System.out.println("Order Created Successfully: " + ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON).body(response));
            
            
            
            return response;
        } catch (Exception e) {
            System.out.println("Order Creation Failed: " + e.getMessage());
            e.printStackTrace(); // Print detailed stack trace
            return null;
        }
    }
    
    public boolean verifyRazorpayCredentials() {
        try {
            // Encode API Key for Basic Auth
            String credentials = publishableKey + ":" + secretKey;
            String base64Encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

            System.out.println("Basic " + base64Encoded); // Log to verify

            // Initialize client (test authentication)
            RazorpayClient razorpayClient = new RazorpayClient(publishableKey, secretKey);
            razorpayClient.Orders.fetch("order_Px9YtooVJZweQS"); // Test API call

            return true; // Authentication successful
        } catch (RazorpayException e) {
            System.out.println("Authentication Failed: " + e.getMessage());
            return false;
        }
    }
    
    // Generate a UPI Payment Link
    public String createUPIPaymentLink(String upiId, int amount) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            String auth = publishableKey + ":" + secretKey;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("Content-Type", "application/json");

            JSONObject requestBody = new JSONObject();
            requestBody.put("amount", amount * 100); // Amount in paise
            requestBody.put("currency", "INR");
            requestBody.put("description", "UPI Payment");
            requestBody.put("expire_by", System.currentTimeMillis() / 1000 + (20 * 60)); // 20 minutes expiry
            requestBody.put("callback_url", "https://yourwebsite.com/payment-success");
            requestBody.put("callback_method", "get");

            JSONObject customer = new JSONObject();
            customer.put("name", "John Doe");
            customer.put("email", "johndoe@example.com");
            customer.put("contact", "9876543210");
            requestBody.put("customer", customer);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(PAYMENT_LINK_API_URL, entity, String.class);

            return response.getBody(); // Returns payment link details
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to create UPI payment link\"}";
        }
    }
}