package com.abhiroop.mybazaar.orderservice.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_item_mapping")
public class OrderItemDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2316595495347425507L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long mappingid;
	private String pid;
	private long oid;
	private String pname;
	private int units;
	private BigDecimal unitprice;

	public OrderItemDTO(String pid, long oid, String pname, int units, BigDecimal unitprice) {
		super();
		this.pid = pid;
		this.oid = oid;
		this.pname = pname;
		this.units = units;
		this.unitprice = unitprice;
	}

	public OrderItemDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public long getOid() {
		return oid;
	}

	public void setOid(long oid) {
		this.oid = oid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public BigDecimal getUnitprice() {
		return unitprice;
	}

	public void setUnitprice(BigDecimal unitprice) {
		this.unitprice = unitprice;
	}

}
