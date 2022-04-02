package com.ox.notification;

import com.ox.amqp.RabbitMQMessageProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {
        "com.ox.notification", "com.ox.amqp"
})
@EnableEurekaClient
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }

    //to test publishing message into rabbit mq
    /*
    @Bean
    public CommandLineRunner commandLineRunner(RabbitMQMessageProducer producer, NotificationConfig notificationConfig) {
        return args -> {
            producer.publish(new Person("wael", 27), notificationConfig.getInternalExchange(), notificationConfig.getInternalNotificationRoutingKey());
        };
    }

    record Person(String name, int age) {}
     */
}
