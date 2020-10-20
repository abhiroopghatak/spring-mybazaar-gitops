package com.abhiroop.mybazaar.svc;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;

import com.abhiroop.mybazaar.pojo.ProductInfo;

public interface InventoryService {

	List<ProductInfo> findAll(PageRequest request);

	ProductInfo save(@Valid ProductInfo product);

	ProductInfo findOne(String productId);

	void delete(String productId);

	List<ProductInfo> findAllByCategory(String categ);


}
