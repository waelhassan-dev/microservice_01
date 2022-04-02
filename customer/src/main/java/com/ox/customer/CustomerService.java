package com.ox.customer;

import com.ox.amqp.RabbitMQMessageProducer;
import com.ox.clients.fraud.FraudCheckResponse;
import com.ox.clients.fraud.FraudClient;
import com.ox.clients.notification.NotificationClient;
import com.ox.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;



    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();

        //todo: check if email is valid
        //todo: check if email not taken
        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());
        log.info("checked .. is a fraudster customer: " + fraudCheckResponse.isFraudster());

        if (fraudCheckResponse.isFraudster())
            throw new IllegalStateException("fraudster !");

        log.info("sending notification to the customer");
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .toCustomerId(customer.getId())
                .toCustomerEmail(customer.getEmail())
                .message(String.format("Hi %s, welcome to microservice_01..", customer.getFirstName()))
                .build();

        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );

    }
}
