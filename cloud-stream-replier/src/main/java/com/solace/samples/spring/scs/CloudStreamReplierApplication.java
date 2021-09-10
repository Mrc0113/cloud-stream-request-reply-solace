package com.solace.samples.spring.scs;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.BinderHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.solacesystems.jcsmp.Destination;

@SpringBootApplication
public class CloudStreamReplierApplication {

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
			Destination replyToTopic = (Destination) request.getHeaders().get("solace_replyTo");
			String cid = (String) request.getHeaders().get("solace_correlationId");
			
			System.out.println("Processing request with cid of: " + cid);
			System.out.println("ReplyTo Topic: " + replyToTopic.toString());
			
			// Return Response Message w/ target destination set
			return MessageBuilder.withPayload(uppercasedPayload)
					.setHeader(BinderHeaders.TARGET_DESTINATION, replyToTopic.toString())
					.setHeader("solace_correlationId", cid)
					.build();
		};
	}
}
