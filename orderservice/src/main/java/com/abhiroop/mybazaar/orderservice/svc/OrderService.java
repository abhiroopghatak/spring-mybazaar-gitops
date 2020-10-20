package com.abhiroop.mybazaar.orderservice.svc;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiroop.mybazaar.orderservice.pojo.OrderEntity;

@Service
@Transactional
public interface OrderService {

	OrderEntity insertOrder(OrderEntity order);

	OrderEntity getOrder(long oid);

	List<OrderEntity> getOrdersByUserId(String uid);

}
