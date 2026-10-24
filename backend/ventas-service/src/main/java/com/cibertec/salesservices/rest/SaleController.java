package com.cibertec.salesservices.rest;

import com.cibertec.salesservices.dto.SaleBatchRequest;
import com.cibertec.salesservices.dto.SaleBatchResponse;
import com.cibertec.salesservices.dto.SaleRequest;
import com.cibertec.salesservices.dto.SaleResponse;
import com.cibertec.salesservices.dto.SaleWithProductResponse;
import com.cibertec.salesservices.negocio.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SaleController {

	private final SaleService saleService;

	public SaleController(SaleService saleService) {
		this.saleService = saleService;
	}

	@GetMapping("/sales")
	public ResponseEntity<List<SaleResponse>> getAllSales() {
		return ResponseEntity.ok(saleService.getAllSales());
	}

	@GetMapping("/sales/{id}")
	public ResponseEntity<SaleResponse> getSaleById(@PathVariable Long id) {
		return ResponseEntity.ok(saleService.getSaleById(id));
	}

	@GetMapping("/sales/{id}/details")
	public ResponseEntity<SaleWithProductResponse> getSaleDetailsWithFeign(@PathVariable Long id) {
		return ResponseEntity.ok(saleService.getSaleDetailsWithFeign(id));
	}

	@GetMapping("/sales/{id}/details-resiliente")
	public ResponseEntity<SaleWithProductResponse> getSaleDetailsResiliente(@PathVariable Long id) {
		return ResponseEntity.ok(saleService.getSaleDetailsResiliente(id));
	}

	@PostMapping("/sales")
	public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request) {
		return ResponseEntity.ok(saleService.createSale(request));
	}

	@PostMapping("/sales/batch")
	public ResponseEntity<SaleBatchResponse> createSaleBatch(@Valid @RequestBody SaleBatchRequest request) {
		return ResponseEntity.ok(saleService.createSaleBatch(request));
	}

	@PostMapping("/sales/rabbit-reserve")
	public ResponseEntity<SaleResponse> createSaleWithRabbitReserve(@Valid @RequestBody SaleRequest request) {
		return ResponseEntity.ok(saleService.createSaleWithRabbitReserve(request));
	}

	@PutMapping("/sales/{id}")
	public ResponseEntity<SaleResponse> updateSale(@PathVariable Long id, @Valid @RequestBody SaleRequest request) {
		return ResponseEntity.ok(saleService.updateSale(id, request));
	}

	@DeleteMapping("/sales/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
		saleService.deleteSale(id);
		return ResponseEntity.noContent().build();
	}
}
