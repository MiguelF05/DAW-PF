package com.cibertec.salesservices.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "messaging.kafka.enabled", havingValue = "true")
public class KafkaTopicConfig {

	public static final String STOCK_MOVEMENTS_TOPIC = "stock-movements";
	public static final String SALE_CANCELLATION_REQUESTS_TOPIC = "sale-cancellation-requests";

	@Bean
	public NewTopic stockMovementsTopic() {
		return TopicBuilder.name(STOCK_MOVEMENTS_TOPIC)
				.partitions(3)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic saleCancellationRequestsTopic() {
		return TopicBuilder.name(SALE_CANCELLATION_REQUESTS_TOPIC)
				.partitions(3)
				.replicas(1)
				.build();
	}
}
