package com.ucpaas.smsp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TestModel extends BaseModel {

	private static final long serialVersionUID = 2061810455913078897L;

	private String filed1;

	private String filed2;

	public String getFiled1() {
		return filed1;
	}

	public void setFiled1(String filed1) {
		this.filed1 = filed1;
	}

	public String getFiled2() {
		return filed2;
	}

	public void setFiled2(String filed2) {
		this.filed2 = filed2;
	}

	

}
