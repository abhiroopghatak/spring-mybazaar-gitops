package com.abhiroop.mybazaar.orderservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.abhiroop.mybazaar.orderservice.pojo.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	
	@Query("SELECT c FROM OrderEntity c where c.userName = :userName")
	List<OrderEntity> findOrderByUserName(@Param("userName") String userName);
}
