package com.abhiroop.mybazaar.svc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiroop.mybazaar.pojo.ProductCategory;
import com.abhiroop.mybazaar.pojo.ProductInfo;
import com.abhiroop.mybazaar.repo.InventoryRepository;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	InventoryRepository inventoryRepository;

	@Override
	public List<ProductInfo> findAll(PageRequest request) {

		return inventoryRepository.findAll();
	}

	@Override
	public ProductInfo findOne(String productId) {
		ProductInfo p = null;
		Optional<ProductInfo> productInfoOptional = inventoryRepository.findById(productId);

		if (productInfoOptional.isPresent()) {
			p = productInfoOptional.get();
		}
		return p;
	}
	
	@Override
	public List<ProductInfo> findAllByCategory(String categ) {
		List<ProductInfo> pList = Collections.emptyList();
		ProductCategory pcat = ProductCategory.valueOf(categ);
		
		if (pcat !=null)
			pList = inventoryRepository.findProductsByCategory(pcat);
		return pList;
	}

	@Override
	public ProductInfo save(@Valid ProductInfo productInfo) {
		return inventoryRepository.save(productInfo);
	}

	@Override
	public void delete(String productId) {
		ProductInfo productInfo = findOne(productId);
		if (productInfo != null)
			inventoryRepository.delete(productInfo);

	}

}
