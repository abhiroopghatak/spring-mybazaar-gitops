package com.abhiroop.mybazaar.orderservice.pojo.transitobject;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class OrderRequestDto {

	private String username;
	private List<JsProductObject> prodList;
	private BigDecimal shipPrice;
	private BigDecimal taxPrice;
	private BigDecimal totalProductPrice;
	private long oid;
	private String orderstate;
	private Calendar odate;
	public OrderRequestDto() {
		super();
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<JsProductObject> getProdList() {
		return prodList;
	}
	public void setProdList(List<JsProductObject> prodList) {
		this.prodList = prodList;
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
	public BigDecimal getTotalProductPrice() {
		return totalProductPrice;
	}
	public void setTotalProductPrice(BigDecimal totalProductPrice) {
		this.totalProductPrice = totalProductPrice;
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public String getOrderstate() {
		return orderstate;
	}
	public void setOrderstate(String orderstate) {
		this.orderstate = orderstate;
	}
	public Calendar getOdate() {
		return odate;
	}
	public void setOdate(Calendar odate) {
		this.odate = odate;
	}
	@Override
	public String toString() {
		return "OrderRequestDto [username=" + username + ", prodList=" + prodList + ", shipPrice=" + shipPrice
				+ ", taxPrice=" + taxPrice + ", totalProductPrice=" + totalProductPrice + ", oid=" + oid
				+ ", orderstate=" + orderstate + ", odate=" + odate + "]";
	}
	
	
	
	
}
