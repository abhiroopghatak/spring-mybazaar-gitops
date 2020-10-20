package com.abhiroop.mybazaar.cartservice.pojo.entity;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.abhiroop.mybazaar.cartservice.pojo.ProductCategory;


@Entity
@Table(name = "product_in_order")
public class ProductInOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private long cartid;

	
	private String productId;
	private String productName;
	@Enumerated(EnumType.STRING)
	private ProductCategory productCategory;
	private BigDecimal unitPrice;
	private int units;
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public long getCartid() {
		return cartid;
	}
	public void setCartid(long cartid) {
		this.cartid = cartid;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productid) {
		this.productId = productid;
	}
	public ProductCategory getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}
	public int getUnits() {
		return units;
	}
	public void setUnits(int units) {
		this.units = units;
	}
	
	
	 public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        if (!super.equals(o)) return false;
	        ProductInOrder that = (ProductInOrder) o;
	        return Objects.equals(id, that.id) &&
	                Objects.equals(productId, that.productId) &&
	                Objects.equals(unitPrice, that.unitPrice) ;
	    }

	    @Override
	    public int hashCode() {

	        return Objects.hash(super.hashCode(), id, productId, unitPrice);
	    }
}
