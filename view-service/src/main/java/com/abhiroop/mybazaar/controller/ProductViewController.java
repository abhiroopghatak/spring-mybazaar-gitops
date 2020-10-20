package com.abhiroop.mybazaar.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiroop.mybazaar.pojo.ApiResponse;
import com.abhiroop.mybazaar.pojo.JsProductObject;
import com.abhiroop.mybazaar.pojo.OrderRequestDto;
import com.abhiroop.mybazaar.pojo.Product;
import com.abhiroop.mybazaar.svc.ViewFacadeService;
import com.abhiroop.mybazaar.util.Mappings;

@RestController
public class ProductViewController {

	@Autowired
	private ViewFacadeService viewFacadeService;

	private static final Logger log = LoggerFactory.getLogger(ProductViewController.class);

	@GetMapping(Mappings.PROD_LIST)
	public ApiResponse getProdList() {

		log.info("prod list request recieved .");
		ApiResponse ar = null;
		List<Product> prodList = null;
		try {
			prodList = viewFacadeService.findAllProducts();

			ar = new ApiResponse(HttpStatus.OK, "SUCCESS", prodList);
		} catch (Exception e) {

			e.printStackTrace();
			ar = new ApiResponse(HttpStatus.EXPECTATION_FAILED, "FAILURE", prodList);
		}
		return ar;

	}

	@PostMapping(Mappings.ADD_TO_CART)
	public boolean addToCart(String productId) {
		boolean isItemAdded = false;
		int units = 1;
		log.info("Product of id " + productId + " to add to cart recieved");
		try {
			isItemAdded = viewFacadeService.addToCart(productId, units);
			log.info("Product of id " + productId + "added to cart.");
		} catch (Exception e) {
			log.error("Add to cart => Exception in ViewServiceImpl.");
		}
		return isItemAdded;

	}
	
	@PostMapping(Mappings.REMOVE_FROM_CART)
	public boolean removeFromCart(JsProductObject jo) {
		boolean isItemRemoved = false;
		try {
			isItemRemoved = viewFacadeService.removeFromCart(jo);
			log.info("Product of id " + jo.getProductId() + "  removeed from cart");
		} catch (Exception e) {
			log.error("Exception in ViewServiceImpl.");
		}
		return isItemRemoved;

	}
	
	@PostMapping(Mappings.DO_CART_EMPTY)
	public boolean doCartEmpty() {
		boolean result = false;
		try {
			result = viewFacadeService.emptyCart();
		} catch (Exception e) {
			log.error("Exception in ViewServiceImpl.");
		}
		return result;

	}
	
	
	@GetMapping(Mappings.LOAD_CART)
	public List<JsProductObject> loadCart(String cartid) {
		 List<JsProductObject> prodInCart =viewFacadeService.loadCart();
		
		return prodInCart;

	}
	
	@PostMapping(Mappings.DO_CART_CHECKOUT)
	public boolean doCartCheckout() {
		boolean result = false;
		try {
			result = viewFacadeService.doCartCheckout();
		} catch (Exception e) {
			log.error("Exception in ViewServiceImpl.");
		}
		return result;

	}

	@GetMapping(Mappings.LOAD_ORDER_BY_ID)
	public OrderRequestDto loadOrderById(long oid) {
		log.info(Mappings.LOAD_ORDER_BY_ID + " received request for order id "+ oid);
		OrderRequestDto o =viewFacadeService.getOrderDetailsById(oid);
		
		if(o==null) {
			log.error("No Order Object received from order service. Creating dummy object with order id  0 and returning to view.");
			o= new OrderRequestDto();o.setOid(0);
		}
		return o;

	}
	@PostMapping(Mappings.ORDER_TO_EMAIL)
	public boolean sendOrderToemail(String oid) {
		boolean isMailSent = false;
		int units = 1;
		log.info("Email send request for Order of id " + oid + " recieved");
		try {
			isMailSent = viewFacadeService.sendOrderToemail(oid);
		} catch (Exception e) {
			log.error("send Order email => Exception in ViewServiceImpl.");
		}
		return isMailSent;

	}
}
