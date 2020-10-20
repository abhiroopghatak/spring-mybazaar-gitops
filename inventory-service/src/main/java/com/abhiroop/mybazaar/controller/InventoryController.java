package com.abhiroop.mybazaar.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abhiroop.mybazaar.pojo.ApiResponse;
import com.abhiroop.mybazaar.pojo.ProductInfo;
import com.abhiroop.mybazaar.svc.InventoryService;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	@Autowired
	InventoryService inventoryService;

	@GetMapping("/isok")
	public String isok() {
		return "OK";
	}

	@GetMapping("/health")
	public String health() {
		String r = "InventoryService  is " + ((inventoryService != null) ? "Up and Running" : "Not Ready");
		System.out.println(r);
		return r;
	}

	@GetMapping("/products")
	public List<ProductInfo> findAll(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "3") Integer size) {
		PageRequest request = PageRequest.of(page - 1, size);
		return inventoryService.findAll(request);
	}

	@GetMapping("/products/{categ}")
	public List<ProductInfo> findAllForCategory(@PathVariable("categ") String categ) {
		return inventoryService.findAllByCategory(categ);
	}

	@GetMapping("/product/{productId}")
	public ProductInfo showOne(@PathVariable("productId") String productId) {

		ProductInfo productInfo = inventoryService.findOne(productId);

		return productInfo;
	}

	@PostMapping("/products/new")
	public ApiResponse create(@Valid @RequestBody ProductInfo product, BindingResult bindingResult) {
		ApiResponse ar = new ApiResponse();
		ProductInfo productIdExists = inventoryService.findOne(product.getProductId());
		if (productIdExists != null) {
			bindingResult.rejectValue("productId", "error.product", "");

			ar.setMessage("There is already a product with the code provided");
			ar.setResult(product);
			ar.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
		} else {
			product = inventoryService.save(product);
			ar.setMessage("Product saved to inventory");
			ar.setResult(product);
			ar.setStatus(HttpStatus.ACCEPTED.value());
		}

		return ar;
	}

	@PutMapping("/seller/product/{id}/edit")
	public ResponseEntity edit(@PathVariable("id") String productId, @Valid @RequestBody ProductInfo product,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(bindingResult);
		}
		if (!productId.equals(product.getProductId())) {
			return ResponseEntity.badRequest().body("Id Not Matched");
		}

		return ResponseEntity.ok(inventoryService.save(product));
	}

	@DeleteMapping("/seller/product/{id}/delete")
	public ResponseEntity delete(@PathVariable("id") String productId) {
		inventoryService.delete(productId);
		return ResponseEntity.ok().build();
	}

}
