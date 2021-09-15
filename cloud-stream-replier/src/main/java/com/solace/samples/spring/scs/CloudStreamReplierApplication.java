package com.solace.samples.spring.scs;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.BinderHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;



@SpringBootApplication
public class CloudStreamReplierApplication {

	private static String REPLYTO_DESTINATION_KEY = "solace_replyTo";
	private static String CORRELATION_ID_KEY = "solace_correlationId";
	
	public static void main(String[] args) {
		SpringApplication.run(CloudStreamReplierApplication.class, args);
	}

	@Bean
	public Function<Message<String>, Message<String>> replier() {
		return request -> {
			// Process event
			String payload = request.getPayload();
			String uppercasedPayload = payload.toUpperCase();
			
			// Get the Topic to replyTo and correlation ID
			String replyToTopic = request.getHeaders().get(REPLYTO_DESTINATION_KEY).toString();
			String cid = request.getHeaders().get(CORRELATION_ID_KEY).toString();
			
			System.out.println("Processing request with cid of: " + cid);
			System.out.println("ReplyTo Topic: " + replyToTopic);
			
			// Return Response Message w/ target destination set
			return MessageBuilder.withPayload(uppercasedPayload)
					.setHeader(BinderHeaders.TARGET_DESTINATION, replyToTopic)
					.setHeader(CORRELATION_ID_KEY, cid)
					.build();
		};
	}
}
