# Spring Cloud Stream Request/Reply with Solace
This repository is just me testing out how to do request/reply with Solace. It still needs some work to retry requests that don't receive a response. 

**This repo contains to Spring Cloud Stream Microservices**

1. cloud-stream-requestor
This microservice sends a request using the `sendRequest` method and listens for responses using the `responseReceiver` function. 
1. cloud-stream-replier
This microservice receives requests, processes them and sends responses to the topic specified by the `solace_replyTo` header. 

You can run both microservices using the following command: 
`mvnw clean spring-boot:run`


## Resources

For more information try these resources:

- The Solace Developer Portal website at: https://solace.dev
- Ask the [Solace Community](https://solace.community)

