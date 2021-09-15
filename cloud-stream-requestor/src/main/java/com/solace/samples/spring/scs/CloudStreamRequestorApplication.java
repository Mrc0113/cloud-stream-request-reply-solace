package com.solace.samples.spring.scs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.Topic;

@SpringBootApplication
public class CloudStreamRequestorApplication {

	private static String REPLYTO_DESTINATION_KEY = "solace_replyTo";
	private static String CORRELATION_ID_KEY = "solace_correlationId";
	
	private static String REPLYTO_TOPIC_PREFIX = "solace/scst/reply/example/";

	private Map<String, Message<?>> outstandingRequests = new HashMap<String, Message<?>>();

	@Autowired
	StreamBridge sb;

	public static void main(String[] args) {
		SpringApplication.run(CloudStreamRequestorApplication.class, args);
	}

	// TODO Add something that checks for old requests on a schedule and re-sends if
	// no response was received

	@Bean
	public Consumer<Message<String>> responseReceiver() {
		return msg -> {
			// Get Correlation ID
			String correlationId = (String) msg.getHeaders().get(CORRELATION_ID_KEY);
			System.out.println("Received response for correlation id: " + correlationId);

			// TODO Process Response here!

			// Remove from outstanding requests
			outstandingRequests.remove(correlationId);
		};
	}

	@Bean
	public Supplier<Message<?>> sendRequest(StreamBridge sb) {
		return () -> {
			// Create Correlation ID & Topic we want replier to return response on
			String correlationId = UUID.randomUUID().toString();
			Topic replyToTopic = JCSMPFactory.onlyInstance().createTopic(REPLYTO_TOPIC_PREFIX + correlationId);

			// Create your payload
			String payload = "Message Payload for Correlation ID of: " + correlationId;

			// Create request message with proper headers
			Message<?> requestMsg = MessageBuilder.withPayload(payload)
					.setHeader(REPLYTO_DESTINATION_KEY, replyToTopic)
					.setHeader(CORRELATION_ID_KEY, correlationId)
					.build();

			// Keep track of outstanding requests (if desired)
			outstandingRequests.put(correlationId, requestMsg);

			System.out.println("Sending request with cid: " + correlationId);
			return requestMsg;
		};
	}

}
