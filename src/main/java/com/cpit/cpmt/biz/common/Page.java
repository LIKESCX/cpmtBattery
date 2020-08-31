package com.cpit.cpmt.biz.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.RowBounds;

/**
 * Description: 分页 liangzhiyuan 2016-01-20
 */
public class Page<E> extends ArrayList<E> {

	public static final int PAGE_SIZE = 10;
	public static final int NO_SQL_COUNT = -1;
	public static final int SQL_COUNT = 0;
	private int pageNum;
	private int pageSize;
	private int startRow;
	private int endRow;
	private long total;
	private int pages;
	private String orderBy;
	private List<E> items;

	public List<E> getItems() {
		return items;
	}

	public void setItems(List<E> items) {
		this.items = items;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Page() {
	}

	public Page(int pageNum, int pageSize) {
		this(pageNum, pageSize, SQL_COUNT);
	}

	public Page(int pageNum, int pageSize, String orderBy) {
		this(pageNum, pageSize, SQL_COUNT, orderBy);
	}

	public Page(int pageNum, int pageSize, int total) {
		super(pageSize);
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.total = total;
		this.startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
		this.endRow = pageNum * pageSize;
	}

	public Page(int pageNum, int pageSize, int total, String orderBy) {
		super(pageSize);
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.total = total;
		this.startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
		this.endRow = pageNum * pageSize;
		this.orderBy = orderBy;
	}

	public Page(RowBounds rowBounds) {
		super(rowBounds.getLimit());
		this.pageSize = rowBounds.getLimit();
		this.startRow = rowBounds.getOffset();
		this.total = NO_SQL_COUNT;
		this.endRow = this.startRow + this.pageSize;
	}

	public List<E> getResult() {
		return this;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "Page{" + "pageNum=" + pageNum + ", pageSize=" + pageSize + ", startRow=" + startRow + ", endRow="
				+ endRow + ", total=" + total + ", pages=" + pages + '}';
	}
}