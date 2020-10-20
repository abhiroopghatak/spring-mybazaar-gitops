package com.abhiroop.mybazaar.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.abhiroop.mybazaar.pojo.ProductCategory;
import com.abhiroop.mybazaar.pojo.ProductInfo;

@Repository
public interface InventoryRepository extends JpaRepository<ProductInfo, String>{

	
	@Query("SELECT p FROM ProductInfo p where p.productCategory = :productCategory")
	List<ProductInfo>  findProductsByCategory(ProductCategory productCategory );

}
