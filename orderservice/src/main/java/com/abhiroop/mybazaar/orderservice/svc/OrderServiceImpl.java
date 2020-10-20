package com.abhiroop.mybazaar.orderservice.svc;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiroop.mybazaar.orderservice.pojo.OrderEntity;
import com.abhiroop.mybazaar.orderservice.pojo.OrderItemDTO;
import com.abhiroop.mybazaar.orderservice.pojo.OrderState;
import com.abhiroop.mybazaar.orderservice.repository.OrderProductRepository;
import com.abhiroop.mybazaar.orderservice.repository.OrderRepository;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private OrderProductRepository orderProductRepository;

	@Override
	public OrderEntity insertOrder(OrderEntity order) {
		if (order == null || order.getItemlist() == null || order.getItemlist().isEmpty()) {
			throw new RuntimeException("Invalid Order. Item List can not be empty to place order");
		}
		order = orderRepo.saveAndFlush(order);
		List<OrderItemDTO> itemList = order.getItemlist();
		for (Iterator<OrderItemDTO> it = itemList.iterator(); it.hasNext();) {
			OrderItemDTO otdo = it.next();
			otdo.setOid(order.getOid());
		}

		orderProductRepository.saveAll(itemList);
		if (order.getOdate() == null) {
			order.setOdate(Calendar.getInstance());
		}
		order.setOrderState(OrderState.ACCEPTED);
		return order;
	}

	@Override
	public OrderEntity getOrder(long oid) {
		Optional<OrderEntity> optionaloe = orderRepo.findById(oid);
		OrderEntity oe = null;
		if (optionaloe.isPresent()) {
			oe = optionaloe.get();
			oe.getItemlist().addAll(getItemsPerOrderId(oe.getOid()));
		}

		return oe;
	}

	@Override
	public List<OrderEntity> getOrdersByUserId(String uname) {
		List<OrderEntity> oList = orderRepo.findOrderByUserName(uname);

		return oList;
	}

	private List<OrderItemDTO> getItemsPerOrderId(long oid) {
		List<OrderItemDTO> itemlist = orderProductRepository.findAll();
		List<OrderItemDTO> orderitemlist = null;
		if (itemlist != null) {
			orderitemlist = itemlist.stream() // convert list to stream
					.filter(orderItem -> oid == orderItem.getOid()).collect(Collectors.toList());

		}
		return orderitemlist;
	}

	// validate order items before processing
	// - assuming there are no multiple entries for one inventory item in the order
	// - if one order item entry fails, the whole order fails.
	/*
	 * private ItemStockLevel processOrderItem(Long inventoryItemId, Long
	 * qtyOrdered) {
	 * 
	 * final InventoryItem inventoryItem =
	 * inventoryItemRepository.findOne(inventoryItemId); if (inventoryItem == null)
	 * { throw new InvalidOrderException("Invalid order", ENTITY_NAME,
	 * "invalidorder"); }
	 * 
	 * // find item stock level final Optional<ItemStockLevel> itemStockLevel =
	 * itemStockLevelRepository.findTopByInventoryItemOrderByStockDateDesc(
	 * inventoryItem); if (!itemStockLevel.isPresent()) { throw new
	 * InvalidOrderException("Invalid order", ENTITY_NAME, "invalidorder"); }
	 * 
	 * // check if quantity available Long qtyCurrent =
	 * itemStockLevel.get().getQuantity(); Long newqty = qtyCurrent - qtyOrdered; if
	 * (newqty < 0L) { throw new InvalidOrderException("Invalid order", ENTITY_NAME,
	 * "invalidorder"); }
	 * 
	 * // construct new item stock level ItemStockLevel itemStockLevelNew = new
	 * ItemStockLevel(); itemStockLevelNew.setInventoryItem(inventoryItem);
	 * itemStockLevelNew.setQuantity(newqty);
	 * itemStockLevelNew.setStockDate(ZonedDateTime.now(ZoneId.systemDefault()));
	 * return itemStockLevelNew; }
	 * 
	 * }
	 */

}
