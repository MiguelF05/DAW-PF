package com.cibertec.salesservices.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "messaging.consumers.enabled", havingValue = "true")
public class StockLowAlertConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(StockLowAlertConsumer.class);

	@RabbitListener(queues = RabbitMQConfig.STOCK_LOW_QUEUE)
	public void onStockLow(StockLowAlertEvent event) {
		LOGGER.warn("[sales-services] Alerta de stock bajo recibida: {}", event);
	}
}
