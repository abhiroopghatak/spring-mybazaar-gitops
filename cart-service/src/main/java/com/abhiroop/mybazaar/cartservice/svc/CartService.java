package com.abhiroop.mybazaar.cartservice.svc;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiroop.mybazaar.cartservice.pojo.entity.Cart;
import com.abhiroop.mybazaar.cartservice.pojo.entity.ProductInOrder;


@Service
@Transactional
public interface CartService {
	Cart getCart(String userid);

    Cart addItemToCart(ProductInOrder productInOrder, long cartId);

    Cart emptyCart(Cart cart );

    Cart checkout(Cart cart, String username);

	Cart removeItemFromCart(Cart cart, ProductInOrder productInOrder, int unit);

	Cart addItemsToCart(Collection<ProductInOrder> productInOrders, Cart cart);
}
