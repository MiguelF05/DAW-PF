package com.cibertec.productsservices.kafka;

import java.time.Instant;

public record SaleCancellationRequestedEvent(
		Long saleId,
		Long productId,
		Integer quantity,
		Long customerId,
		String currentStatus,
		Instant timestamp
) {
}
