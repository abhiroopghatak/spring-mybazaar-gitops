package com.abhiroop.mybazaar.cartservice.pojo;



import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public class Product implements Serializable {


	@Override
	public String toString() {
		return "Product [productId=" + productId + ", productName=" + productName + ", producer=" + producer
				+ ", shippingWeight=" + shippingWeight + ", productPrice=" + productPrice + ", productStock="
				+ productStock + ", productDescription=" + productDescription + ", productIcon=" + productIcon
				+ ", productStatus=" + productStatus + ", productCategory=" + productCategory + ", createTime="
				+ createTime + ", updateTime=" + updateTime + "]";
	}
	private static final long serialVersionUID = 1L;

	private String productId;

	private String productName;

	private String producer;

	private double shippingWeight;

	@Transient
    private MultipartFile productImage;

	private BigDecimal productPrice;

	private Integer productStock;

	private String productDescription;

	private String productIcon;

	/** 0: on-sale 1: off-sale */

	private Integer productStatus;

	private String productCategory;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTime;
	private Date updateTime;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getProductStock() {
		return productStock;
	}

	public void setProductStock(Integer productStock) {
		this.productStock = productStock;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductIcon() {
		return productIcon;
	}

	public void setProductIcon(String productIcon) {
		this.productIcon = productIcon;
	}

	public Integer getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public MultipartFile getProductImage() {
		return productImage;
	}

	public void setProductImage(MultipartFile productImage) {
		this.productImage = productImage;
	}

	public double getShippingWeight() {
		return shippingWeight;
	}

	public void setShippingWeight(double shippingWeight) {
		this.shippingWeight = shippingWeight;
	}

	public Product() {
		System.out.println("New Product Object Instantiated");
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        return productId.equals(product.productId);
    }
    @Override
    public int hashCode() {
        return productId.hashCode();
    }

}
