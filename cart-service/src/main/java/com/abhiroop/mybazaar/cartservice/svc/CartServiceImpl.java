package com.abhiroop.mybazaar.cartservice.svc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.abhiroop.mybazaar.cartservice.pojo.CartState;
import com.abhiroop.mybazaar.cartservice.pojo.entity.Cart;
import com.abhiroop.mybazaar.cartservice.pojo.entity.ProductInOrder;
import com.abhiroop.mybazaar.cartservice.pojo.transitobject.ApiResponse;
import com.abhiroop.mybazaar.cartservice.pojo.transitobject.JsProductObject;
import com.abhiroop.mybazaar.cartservice.pojo.transitobject.OrderRequestDto;
import com.abhiroop.mybazaar.cartservice.repo.CartRepository;
import com.abhiroop.mybazaar.cartservice.repo.ProductInOrderRepository;

@Service
@Transactional
public class CartServiceImpl implements CartService {

	public static final String STATUS_PROCESSED = "PROCESSED";
	public static final String STATUS_IN_PROCESS = "IN_PROCESS";
	@Value("${service.order.url}")
	private String orderSvcUrl;

	public String getOrderSvcUrl() {
		return orderSvcUrl;
	}

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	ProductInOrderRepository productInOrderRepository;

	@Autowired
	CartRepository cartRepository;

	private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

	@Override
	public Cart getCart(String userid) {
		Cart cart = null;
		List<Cart> cartList = cartRepository.findCartByUserId(userid);

		if (cartList == null || CollectionUtils.isEmpty(cartList)) {
			cart = new Cart(userid);
			cart.setCreationDate(new Date());
			cart.setCartstate(CartState.INITIATED);
		} else {
			Optional<Cart> co = cartList.stream() // convert list to stream
					.filter(c -> !CartState.CHECKEDOUT.equals(c.getCartstate())).findFirst();
			if (co.isPresent()) {
				cart = co.get();
				cart.setCartstate(CartState.LOADED);
				cart.getProducts().addAll(getCartProductList(cart.getCartId()));
			}else {
				cart = new Cart(userid);
				cart.setCreationDate(new Date());
				cart.setCartstate(CartState.INITIATED);
			}
		}
		cart = cartRepository.save(cart);

		return cart;
	}

	private List<ProductInOrder> getCartProductList(long cartId) {
		List<ProductInOrder> prodList = productInOrderRepository.findProductsByCartId(cartId);

		return prodList;
	}

	@Override
	public Cart addItemsToCart(Collection<ProductInOrder> productInOrders, Cart cart) {
		// Optional<Cart> cartOptional = cartRepository.findById(cartId);
		if (cart != null && !CartState.CHECKEDOUT.equals(cart.getCartstate())) {
			cart.getProducts().addAll(getCartProductList(cart.getCartId()));
			productInOrders.forEach(productInOrder -> {
				Set<ProductInOrder> set = cart.getProducts();
				Optional<ProductInOrder> old = set.stream()
						.filter(e -> e.getProductId().equals(productInOrder.getProductId())).findFirst();
				ProductInOrder prod;
				if (old.isPresent()) {
					prod = old.get();
					prod.setUnits(productInOrder.getUnits() + prod.getUnits());
				} else {
					prod = productInOrder;
					prod.setCartid(cart.getCartId());
					cart.getProducts().add(prod);
				}
				prod.setStatus(STATUS_IN_PROCESS);
				productInOrderRepository.save(prod);
			});

			cartRepository.save(cart);
			return cart;
		} else {
			return null;
		}

	}

	private boolean containsProduct(final List<ProductInOrder> list, final String prodId) {
		return list.stream().filter(o -> o.getProductId().equals(prodId)).findFirst().isPresent();
	}

	@Override
	public Cart addItemToCart(ProductInOrder productInOrder, long cartId) {
		Optional<Cart> cartOptional = cartRepository.findById(cartId);
		if (cartOptional.isPresent() && !CartState.CHECKEDOUT.equals(cartOptional.get().getCartstate())) {
			Cart finalCart = cartOptional.get();
			List<ProductInOrder> productInOrders = getCartProductList(cartId);

			productInOrder.setCartid(cartId);
			productInOrder.setStatus(STATUS_IN_PROCESS);
			if (containsProduct(productInOrders, productInOrder.getProductId())) {
				for (Iterator<ProductInOrder> it = productInOrders.iterator(); it.hasNext();) {
					ProductInOrder f = it.next();
					if (f.getProductId().equals(productInOrder.getProductId())) {
						f.setUnits(productInOrder.getUnits() + f.getUnits());
						productInOrderRepository.updateUnit(f.getId(), f.getUnits());
					}
				}
			} else {
				productInOrders.add(productInOrder);
				productInOrderRepository.save(productInOrder);
			}
			finalCart.getProducts().clear();
			finalCart.getProducts().addAll(productInOrders);
			return finalCart;
		} else {
			return null;
		}
	}

	@Override
	public Cart removeItemFromCart(Cart cart, ProductInOrder productInOrder, int unit) {
		if (cart != null && !CartState.CHECKEDOUT.equals(cart.getCartstate())) {
			List<ProductInOrder> productInOrders = getCartProductList(cart.getCartId());

			if (containsProduct(productInOrders, productInOrder.getProductId())) {
				for (Iterator<ProductInOrder> it = productInOrders.iterator(); it.hasNext();) {
					ProductInOrder f = it.next();
					if (f.getProductId().equals(productInOrder.getProductId())) {
						if (f.getUnits() - productInOrder.getUnits() > 0) {
							f.setUnits(f.getUnits() - productInOrder.getUnits());
							productInOrderRepository.updateUnit(f.getId(), f.getUnits());

							// TODO : add to inventory

						} else if (f.getUnits() - productInOrder.getUnits() == 0) {
							it.remove();
							productInOrderRepository.delete(f);
							// TODO : add to inventory
						}
						break;
					}
				}
				cart.getProducts().clear();
				cart.getProducts().addAll(productInOrders);
			}
		}
		return cart;
	}

	@Override
	public Cart emptyCart(Cart cart) {
		if (cart != null && cart.getProducts() != null && !CartState.CHECKEDOUT.equals(cart.getCartstate())) {
			productInOrderRepository.deleteAll(cart.getProducts());
			cart.getProducts().clear();
			// TODO add inventory
		}
		return cart;
	}

	@SuppressWarnings("unused")
	private Cart checkout_Fallback(Cart cart, String username) {
		System.out.println("Order Service is down!!! CIRCUIT BREAKER ENABLED at Cart Checkout!!! fallback route enabled...");
		System.out.println("Chcekout failed for cart=>" +cart +" for user=>"+username);
		return cart;
	}
	
	@Override
//	@HystrixCommand(fallbackMethod = "checkout_Fallback")
	public Cart checkout(Cart cart, String username) {
		if (cart.getProducts() != null) {

			try {
				Set<ProductInOrder> tempProdList = cart.getProducts();
				OrderRequestDto ordto = new OrderRequestDto();
				BigDecimal totalPrice = BigDecimal.ZERO;
				ordto.setUsername(username);
				ordto.setShipPrice(new BigDecimal("100.00"));
				List<JsProductObject> jsProdList = new ArrayList<JsProductObject>();
				if (!CollectionUtils.isEmpty(tempProdList)) {
					Iterator<ProductInOrder> itr = tempProdList.iterator();

					while (itr.hasNext()) {
						ProductInOrder pino = itr.next();
						JsProductObject jsp = new JsProductObject();
						jsp.setProductName(pino.getProductName());
						jsp.setProductId(pino.getProductId());
						jsp.setProductCategory(pino.getProductCategory().name());
						jsp.setUnits(pino.getUnits());
						jsp.setUnitPrice(pino.getUnitPrice());
						jsp.setProductName(pino.getProductName());
						totalPrice = totalPrice.add((pino.getUnitPrice().multiply(new BigDecimal(pino.getUnits()))));
						jsProdList.add(jsp);
					}
					ordto.setTotalProductPrice(totalPrice);
					ordto.setTaxPrice(totalPrice.divide(new BigDecimal(10)));
					ordto.setProdList(jsProdList);

					ResponseEntity<ApiResponse> responseEntity = restTemplate
							.postForEntity(getOrderSvcUrl() + "/placeorder", ordto, ApiResponse.class);

					ApiResponse response = responseEntity.getBody();
					if (response.getStatus() == 200) {
						cart.setCartstate(CartState.CHECKEDOUT);
						cartRepository.updateCartState(cart.getCartId(), cart.getCartstate().name());
						log.info("Order has been placed in the system. User will recieve confirmation email");
					} else if (response.getStatus() == 417) {
						log.error("Order generation is not successful. Cause => " + response.getMessage());
					} else if (response.getStatus() == 400) {
						log.error("Order generation is not successful. Cause => " + response.getMessage());
					} else {
						log.error("Intra service connectivity issue. please connect system admin.");
					}

				}
			} catch (Exception e) {
				log.error("Exception at cart checkout " + e.getMessage());
			}
		}
		return cart;
	}

}
