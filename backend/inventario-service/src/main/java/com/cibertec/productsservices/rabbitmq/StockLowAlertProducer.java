package com.cibertec.productsservices.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class StockLowAlertProducer {

	private static final Logger LOGGER = LoggerFactory.getLogger(StockLowAlertProducer.class);

	private final RabbitTemplate rabbitTemplate;

	public StockLowAlertProducer(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void publish(StockLowAlertEvent event) {
		rabbitTemplate.convertAndSend(
				RabbitMQConfig.STOCK_EXCHANGE,
				RabbitMQConfig.STOCK_LOW_ROUTING_KEY,
				event
		);
		LOGGER.info("[products-services] Alerta de stock bajo publicada: {}", event);
	}
}
