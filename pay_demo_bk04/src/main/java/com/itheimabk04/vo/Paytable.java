package com.itheimabk04.vo;

import java.io.Serializable;

public class Paytable implements Serializable{

	private Integer id;

	private Integer ispay;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIspay() {
		return ispay;
	}

	public void setIspay(Integer ispay) {
		this.ispay = ispay;
	}
}
