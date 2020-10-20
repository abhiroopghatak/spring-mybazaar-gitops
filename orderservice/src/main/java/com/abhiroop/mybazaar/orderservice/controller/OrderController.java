package com.abhiroop.mybazaar.orderservice.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.abhiroop.mybazaar.orderservice.pojo.OrderEntity;
import com.abhiroop.mybazaar.orderservice.pojo.OrderItemDTO;
import com.abhiroop.mybazaar.orderservice.pojo.OrderState;
import com.abhiroop.mybazaar.orderservice.pojo.transitobject.ApiResponse;
import com.abhiroop.mybazaar.orderservice.pojo.transitobject.JsProductObject;
import com.abhiroop.mybazaar.orderservice.pojo.transitobject.OrderRequestDto;
import com.abhiroop.mybazaar.orderservice.svc.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/isok")
	public String isok() {
		return "OK";
	}

	@GetMapping("/health")
	public String health() {

		return "order-svc";
	}

	@RequestMapping(value = "/getOrderDetails/{oid}", method = RequestMethod.GET, headers = "Accept=application/json")
	public ApiResponse getOrderDetails(@PathVariable long oid) {
		ApiResponse ar = null;

		List<JsProductObject> jsProdList = new ArrayList<JsProductObject>();
		OrderEntity oe = orderService.getOrder(oid);
		List<OrderItemDTO> itemlist = null;
		JsProductObject jsProdObject = null;
		OrderRequestDto ordto = null;
		if (oe != null) {
			ordto = get_OrderEntity_To_OrderRequestDto(oe);
			itemlist = oe.getItemlist();
			if (!CollectionUtils.isEmpty(itemlist)) {
				for (Iterator<OrderItemDTO> it = itemlist.iterator(); it.hasNext();) {
					OrderItemDTO otdo = it.next();
					jsProdObject = getJsProdObject_From_OrderItemDto(otdo);
					jsProdList.add(jsProdObject);
				}
			}
			ordto.setProdList(jsProdList);

			ar = new ApiResponse(HttpStatus.OK, "SUCCESS", ordto);
		} else {
			ar = new ApiResponse(HttpStatus.NO_CONTENT, "NODATA", ordto);
		}
		return ar;
	}

	private OrderRequestDto get_OrderEntity_To_OrderRequestDto(OrderEntity oe) {
		OrderRequestDto ordto = new OrderRequestDto();
		ordto.setOid(oe.getOid());
		ordto.setOrderstate(oe.getOrderState().name());
		ordto.setTaxPrice(oe.getTaxPrice());
		ordto.setShipPrice(oe.getShipPrice());
		ordto.setTotalProductPrice(oe.getProdPrice());
		ordto.setOdate(oe.getOdate());
		return ordto;
	}

	private JsProductObject getJsProdObject_From_OrderItemDto(OrderItemDTO otdo) {
		JsProductObject jsProdObject = new JsProductObject();
		jsProdObject.setProductId(otdo.getPid());
		jsProdObject.setProductName(otdo.getPname());
		jsProdObject.setUnitPrice(otdo.getUnitprice());
		jsProdObject.setUnits(otdo.getUnits());
		return jsProdObject;
	}

	@RequestMapping(value = "/getall/{username}", method = RequestMethod.GET, headers = "Accept=application/json")
	public ApiResponse getOrders(@PathVariable String username) {
		ApiResponse ar = null;
		System.out.println("Get all instance of orderentity for the user " + username);
		List<OrderEntity> olist = null;
		List<OrderRequestDto> orderListObject = new ArrayList<OrderRequestDto>();
		OrderRequestDto ordto = null;
		try {
			olist = orderService.getOrdersByUserId(username);
			if (CollectionUtils.isEmpty(olist)) {
				throw new RuntimeException("Empty order list received from service");
			} else {
				for (Iterator<OrderEntity> it = olist.iterator(); it.hasNext();) {
					OrderEntity oe = it.next();
					ordto = get_OrderEntity_To_OrderRequestDto(oe);
					orderListObject.add(ordto);
				}
				ar = new ApiResponse(HttpStatus.OK, "SUCCESS", orderListObject);
			}
		} catch (Exception e) {
			System.out.println("Exception occurred at getOrdersByUserId , for user " + username);
			System.out.println(e.getLocalizedMessage());
			ar = new ApiResponse(HttpStatus.NO_CONTENT, "METHODEXCEPTION", orderListObject);
		}
		return ar;
	}

	@RequestMapping(value = "/placeorder", method = RequestMethod.POST, headers = "Accept=application/json")
	public ApiResponse placeOrder(@RequestBody OrderRequestDto ord) {
		OrderEntity oe = new OrderEntity();
		ApiResponse ar = null;
		if (ord != null && !CollectionUtils.isEmpty(ord.getProdList())) {
			try {

				oe.setUserName(ord.getUsername());
				oe.setShipPrice(ord.getShipPrice());
				oe.setTaxPrice(ord.getTaxPrice());
				List<OrderItemDTO> itemlist = new ArrayList<OrderItemDTO>();
				List<JsProductObject> prodList = ord.getProdList();
				OrderItemDTO otdo = null;
				for (Iterator<JsProductObject> it = prodList.iterator(); it.hasNext();) {
					JsProductObject jpo = it.next();
					otdo = getOrderItemDTO_From_JsProductObject(jpo);
					otdo.setOid(oe.getOid());
					itemlist.add(otdo);
				}
				oe.setProdPrice(ord.getTotalProductPrice());
				oe.setItemlist(itemlist);
				oe = orderService.insertOrder(oe);
				if (OrderState.ACCEPTED.equals(oe.getOrderState()))
					ar = new ApiResponse(HttpStatus.OK, "SUCCESS", null);
				else {
					ar = new ApiResponse(HttpStatus.CONFLICT, "FAILURE", null);
				}

			} catch (Exception e) {
				System.out.println("Exception occurred at placeOrder , for user " + oe.getUserName());
				System.out.println(e.getLocalizedMessage());
				ar = new ApiResponse(HttpStatus.EXPECTATION_FAILED, e.getMessage(), e);
			}
		} else {
			ar = new ApiResponse(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", null);
		}
		return ar;
	}

	private OrderItemDTO getOrderItemDTO_From_JsProductObject(JsProductObject jpo) {
		OrderItemDTO otdo = new OrderItemDTO();

		otdo.setPid(jpo.getProductId());
		otdo.setPname(jpo.getProductName());
		otdo.setUnitprice(jpo.getUnitPrice());
		otdo.setUnits(jpo.getUnits());

		return otdo;
	}
}
