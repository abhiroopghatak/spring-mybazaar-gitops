package com.abhiroop.mybazaar.orderservice.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.lang.NonNull;

@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long oid;
	private Calendar odate;
	@Enumerated(EnumType.STRING)
	private OrderState orderState;
	private String userName;

	private BigDecimal shipPrice;
	private BigDecimal taxPrice;
	private BigDecimal prodPrice;

	@Transient
	private List<OrderItemDTO> itemlist = new ArrayList<OrderItemDTO>();

	public OrderEntity() {
		super();
	}

	public long getOid() {
		return oid;
	}

	public void setOid(long oid) {
		this.oid = oid;
	}

	public Calendar getOdate() {
		return odate;
	}

	public void setOdate(Calendar odate) {
		this.odate = odate;
	}

	public OrderState getOrderState() {
		return orderState;
	}

	public void setOrderState(OrderState orderState) {
		this.orderState = orderState;
	}

	public List<OrderItemDTO> getItemlist() {
		return itemlist;
	}

	public void setItemlist(List<OrderItemDTO> itemlist) {
		this.itemlist = itemlist;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getShipPrice() {
		return shipPrice;
	}

	public void setShipPrice(BigDecimal shipPrice) {
		this.shipPrice = shipPrice;
	}

	public BigDecimal getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(BigDecimal taxPrice) {
		this.taxPrice = taxPrice;
	}

	public BigDecimal getProdPrice() {
		return prodPrice;
	}

	public void setProdPrice(BigDecimal prodPrice) {
		this.prodPrice = prodPrice;
	}

}
