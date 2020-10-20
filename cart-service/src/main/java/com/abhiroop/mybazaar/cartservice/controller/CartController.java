package com.abhiroop.mybazaar.cartservice.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.abhiroop.mybazaar.cartservice.pojo.CartState;
import com.abhiroop.mybazaar.cartservice.pojo.Product;
import com.abhiroop.mybazaar.cartservice.pojo.ProductCategory;
import com.abhiroop.mybazaar.cartservice.pojo.entity.Cart;
import com.abhiroop.mybazaar.cartservice.pojo.entity.ProductInOrder;
import com.abhiroop.mybazaar.cartservice.pojo.transitobject.ApiResponse;
import com.abhiroop.mybazaar.cartservice.pojo.transitobject.CartRequest;
import com.abhiroop.mybazaar.cartservice.pojo.transitobject.JsProductObject;
import com.abhiroop.mybazaar.cartservice.svc.CartService;

@RestController
@RequestMapping("/carts")
public class CartController {

	private static final Logger log = LoggerFactory.getLogger(CartController.class);

	@Value("${service.inventory.url}")
	private String invSvcUrl;

	public String getInvSvcUrl() {
		return invSvcUrl;
	}

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CartService cartService;

	@GetMapping("/health")
	public String health() {
		return "";
	}

	@GetMapping("/isok")
	public String isok() {
		return "OK";
	}

	@RequestMapping(value = "/loadCartForUser/{username}", method = RequestMethod.GET, headers = "Accept=application/json")
	public ApiResponse getCart(@PathVariable String username) {

		ApiResponse ar = null;
		List<JsProductObject> jsProdList = new ArrayList<JsProductObject>();
		Set<ProductInOrder> tempProdList = null;
		Cart cart = cartService.getCart(username);
		if (cart != null) {
			tempProdList = cart.getProducts();
			if (!CollectionUtils.isEmpty(tempProdList)) {
				Iterator<ProductInOrder> itr = tempProdList.iterator();

				while (itr.hasNext()) {
					ProductInOrder pino = itr.next();
					JsProductObject jsp = new JsProductObject();
					jsp.setProductId(pino.getProductId());
					jsp.setProductCategory(pino.getProductCategory().name());
					jsp.setUnits(pino.getUnits());
					jsp.setUnitPrice(pino.getUnitPrice());

					jsProdList.add(jsp);
				}

			}
			ar = new ApiResponse(HttpStatus.OK, "SUCCESS", jsProdList);
		} else {
			ar = new ApiResponse(HttpStatus.EXPECTATION_FAILED, "FAILURE", jsProdList);
		}
		return ar;
	}

	@PostMapping("/addtocart")
	public ApiResponse addToCart(@RequestBody CartRequest cr) {
		boolean isItemAdded = false;
		ApiResponse ar = null;
		log.info("Item add to cart request Object" + cr + " recieved");
		try {
			Product p = cr.getP();

			ProductInOrder productInOrder = new ProductInOrder();
			Cart c = cartService.getCart(cr.getUid());
			productInOrder.setCartid(c.getCartId());
			productInOrder.setProductCategory(ProductCategory.valueOf(p.getProductCategory()));
			productInOrder.setUnitPrice(p.getProductPrice());
			productInOrder.setUnits(cr.getUnits());
			productInOrder.setProductId(p.getProductId());
			productInOrder.setProductName(p.getProductName());
			c = cartService.addItemToCart(productInOrder, c.getCartId());
			if (c != null) {
				isItemAdded = true;
				ar = new ApiResponse(HttpStatus.OK, "SUCCESS", isItemAdded);
			}

		} catch (Exception e) {
			log.error("Exception in add product to cart=> " + e.getMessage());
			ar = new ApiResponse(HttpStatus.EXPECTATION_FAILED, "FAILURE", isItemAdded);
		}
		return ar;
	}

	@PostMapping("/addmanytocart")
	public boolean addManyToCart(@RequestBody List<ProductInOrder> pList, String username) {
		try {
			Cart c = cartService.getCart(username);
			c = cartService.addItemsToCart(pList, c);
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	@PostMapping(value = "/checkoutcart")
	public ApiResponse doCartCheckout(@RequestBody String username) {

		log.info(String.format("received request to checkout cart for  user with username %s", username));
		ApiResponse ar = null;
		try {
			Cart cart = cartService.getCart(username);
			ar = new ApiResponse(HttpStatus.ALREADY_REPORTED, "ALREADY_REPORTED", null);
			if (cart != null && !CartState.CHECKEDOUT.equals(cart.getCartstate())) {
				cart = cartService.checkout(cart, username);
				if (CartState.CHECKEDOUT.equals(cart.getCartstate()))
					ar = new ApiResponse(HttpStatus.OK, "SUCCESS", true);
				else
					ar = new ApiResponse(HttpStatus.BAD_REQUEST, "FAILURE", false);
			}
		} catch (Exception e) {
			System.out.println(e);
			ar = new ApiResponse(HttpStatus.EXPECTATION_FAILED, "EXCEPTION", false);
		}
		return ar;
	}

	@PostMapping(value = "/emptycart")
	public ApiResponse doCartEmpty(@RequestBody String username) {

		log.info(String.format("received request to emptycart for  user with username %s", username));
		ApiResponse ar = null;
		try {
			Cart cart = cartService.getCart(username);
			cart = cartService.emptyCart(cart);
			ar = new ApiResponse(HttpStatus.OK, "SUCCESS", cart);
		} catch (Exception e) {
			System.out.println(e);
			ar = new ApiResponse(HttpStatus.BAD_REQUEST, "EXCEPTION", null);
		}
		return ar;
	}

	@PostMapping("/removefromcart")
	public ApiResponse removeFromCart(@RequestBody CartRequest cr) {
		boolean isItemRemoved = false;
		ApiResponse ar = null;
		log.info("Item remove from cart request Object" + cr + " recieved");
		try {
			Product p = cr.getP();

			ProductInOrder productInOrder = new ProductInOrder();
			Cart c = cartService.getCart(cr.getUid());
			productInOrder.setCartid(c.getCartId());
			productInOrder.setProductCategory(ProductCategory.valueOf(p.getProductCategory()));
			productInOrder.setUnitPrice(p.getProductPrice());
			productInOrder.setUnits(cr.getUnits());
			productInOrder.setProductId(p.getProductId());
			c = cartService.removeItemFromCart(c, productInOrder, productInOrder.getUnits());

			if (c != null) {
				isItemRemoved = true;
				ar = new ApiResponse(HttpStatus.OK, "SUCCESS", isItemRemoved);
			}

		} catch (Exception e) {
			log.error("Exception in remove product to cart=> " + e.getMessage());
			ar = new ApiResponse(HttpStatus.EXPECTATION_FAILED, "FAILURE", isItemRemoved);
		}
		return ar;
	}

	private static boolean isServiceRunning(RestTemplate restTemplate, String uri) {
		if ("OK".equals(restTemplate.getForObject(uri + "/isok", String.class))) {
			return true;
		} else {
			return false;
		}
	}
}
