package com.abhiroop.mybazaar.cartservice.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.abhiroop.mybazaar.cartservice.pojo.entity.ProductInOrder;

public interface ProductInOrderRepository extends JpaRepository<ProductInOrder, Long> {

	@Query("SELECT t FROM ProductInOrder t where t.cartid = :id")
	List<ProductInOrder> findProductsByCartId(@Param("id") long id);
	
	
	@Modifying
	@Query("update ProductInOrder p set p.units= :units where p.id = :id")
	int updateUnit(@Param("id") Long id , @Param("units") int units);
}
