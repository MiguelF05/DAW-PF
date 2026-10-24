package com.cibertec.salesservices.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleBatchResponse(
		String numeroBoleta,
		Long customerId,
		String vendedor,
		BigDecimal total,
		LocalDateTime fechaVenta,
		List<SaleResponse> items
) {
}
