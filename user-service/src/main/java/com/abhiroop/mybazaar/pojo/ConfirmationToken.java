package com.abhiroop.mybazaar.pojo;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.abhiroop.mybazaar.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
public class ConfirmationToken {
	public ConfirmationToken() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String confirmationToken;

	private LocalDate createdDate;

	
	private Long userid;

	public ConfirmationToken(UserDto user) {
		this.userid = user.getId();
		this.createdDate = LocalDate.now();
		this.confirmationToken = UUID.randomUUID().toString();
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}



	public Long getUserid() {
		return userid;
	}


	public void setUserid(Long userid) {
		this.userid = userid;
	}


	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

}
