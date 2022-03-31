package com.ox.customer;

import com.ox.clients.fraud.FraudCheckResponse;
import com.ox.clients.fraud.FraudClient;
import com.ox.clients.notification.NotificationClient;
import com.ox.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;


    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();

        //todo: check if email is valid
        //todo: check if email not taken
        customerRepository.saveAndFlush(customer);

        //todo: check if fraudster
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster())
            throw new IllegalStateException("fraudster !");

        //todo: send notification
        notificationClient.sendNotification(
                NotificationRequest.builder()
                        .toCustomerId(customer.getId())
                        .toCustomerEmail(customer.getEmail())
                        .message(String.format("Hi %s, welcome to microservice_01..", customer.getFirstName()))
                        .build()
        );

    }
}
