package com.ucpaas.smsp.model.param;

import java.io.Serializable;

public class PageParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7442730574587098654L;

	//需要跳到哪一页
	private Integer goalPage = 1;
	
	//每一页显示多少笔
	private Integer pageSize = 10;
	
	private int startIndex = 0;

	public int getStartIndex() {
		System.out.println("----------->");
		return (this.goalPage - 1)*this.pageSize;
	}

	public Integer getGoalPage() {
		return goalPage;
	}

	public void setGoalPage(Integer goalPage) {
		this.goalPage = goalPage;
	}

	

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "PageParam [goalPage=" + goalPage + ", pageSize=" + pageSize + "]";
	}

}
