package com.abhiroop.mybazaar.orderservice.pojo.transitobject;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

public class ApiResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int status;
	private String message;
	private Object result;
	public ApiResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ApiResponse(HttpStatus status, String message, Object result){
	    this.status = status.value();
	    this.message = message;
	    this.result = result;
    }

    public ApiResponse(HttpStatus status, String message){
        this.status = status.value();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
	public String toString() {
		return "ApiResponse [statusCode=" + status + ", message=" + message +"]";
	}
}
