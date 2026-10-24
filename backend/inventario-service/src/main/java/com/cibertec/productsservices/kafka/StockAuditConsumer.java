package com.cibertec.productsservices.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "messaging.consumers.enabled", havingValue = "true")
public class StockAuditConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(StockAuditConsumer.class);

	@KafkaListener(topics = "stock-movements", groupId = "audit-cg")
	public void handleAudit(StockMovementEvent event) {
		LOGGER.info("[audit-cg] Auditoria completa del evento: {}", event);
	}
}
