package com.cibertec.productsservices.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "messaging.consumers.enabled", havingValue = "true")
public class StockReportingConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(StockReportingConsumer.class);

	@KafkaListener(topics = "stock-movements", groupId = "reporting-cg")
	public void handleReport(StockMovementEvent event) {
		LOGGER.info("[reporting-cg] Reporte de movimiento. saleId={}, productId={}, quantity={}, customerId={}, timestamp={}",
				event.saleId(), event.productId(), event.quantity(), event.customerId(), event.timestamp());
	}
}
