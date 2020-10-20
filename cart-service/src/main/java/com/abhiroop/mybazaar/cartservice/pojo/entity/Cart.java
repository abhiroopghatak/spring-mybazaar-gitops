package com.abhiroop.mybazaar.cartservice.pojo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.lang.NonNull;

import com.abhiroop.mybazaar.cartservice.pojo.CartState;

@Entity
@Table(name = "cart_detail")
public class Cart implements Serializable {
	public Cart() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2130925787754765062L;

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long cartId;

	private String userid;

	private Date creationDate;

	@Transient
	private Set<ProductInOrder> products = new HashSet<>();

	@Enumerated(EnumType.STRING)
	private CartState cartstate;

	public Cart(String userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "Cart{" + "cartId=" + cartId + ", products=" + products + '}';
	}

	public long getCartId() {
		return cartId;
	}

	public void setCartId(long cartId) {
		this.cartId = cartId;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Set<ProductInOrder> getProducts() {
		return products;
	}

	public CartState getCartstate() {
		return cartstate;
	}

	public void setCartstate(CartState cartstate) {
		this.cartstate = cartstate;
	}

	public void setProducts(Set<ProductInOrder> products) {
		this.products = products;
	}


	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
