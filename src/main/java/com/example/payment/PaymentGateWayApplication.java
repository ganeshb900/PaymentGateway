package com.example.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.razorpay.RazorpayClient;

@SpringBootApplication
public class PaymentGateWayApplication {
	@Value("${payment.gateway.secretKey}")
    private String secretKey ="LUyTDxJ7Msx9ZCBA2A1mxnnn";

    @Value("${payment.gateway.publishableKey}")
    private String publishableKey="rzp_test_pAMKHorCYzf9Gi";

	

	public static void main(String[] args) {
		SpringApplication.run(PaymentGateWayApplication.class, args);
	}

	@Bean
    public RazorpayClient razorpayClient() throws Exception {
      return new RazorpayClient(publishableKey, secretKey);
    }
	
}
