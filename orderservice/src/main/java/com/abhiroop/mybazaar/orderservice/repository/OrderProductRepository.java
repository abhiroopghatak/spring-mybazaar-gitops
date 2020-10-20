package com.abhiroop.mybazaar.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiroop.mybazaar.orderservice.pojo.OrderItemDTO;

public interface OrderProductRepository extends JpaRepository<OrderItemDTO, Long>{

}
