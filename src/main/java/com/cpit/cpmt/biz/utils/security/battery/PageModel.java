package com.cpit.cpmt.biz.utils.security.battery;

import java.util.List;

import com.cpit.cpmt.dto.exchange.basic.BmsInfo;

public class PageModel<T> {
	    private int pageIndex;//当前页
	    private long row;//记录数
	    private int pageSize;//每页显示条数
	    private List<T> result;//结果集
	    private List<BmsInfo> bmsInfoList;//结果集 自定义的
	    
	    public int getPageIndex() {
			return pageIndex;
		}
		public void setPageIndex(int pageIndex) {
			this.pageIndex = pageIndex;
		}
		public long getRow() {
			return row;
		}
		public void setRow(long row) {
			this.row = row;
		}
		public int getPageSize() {
			return pageSize;
		}
		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}
		public List<T> getResult() {
			return result;
		}
		public void setResult(List<T> result) {
			this.result = result;
		}
		
		public List<BmsInfo> getBmsInfoList() {
			return bmsInfoList;
		}
		public void setBmsInfoList(List<BmsInfo> bmsInfoList) {
			this.bmsInfoList = bmsInfoList;
		}
		/**
	     * 获取总页数
	     *
	     * @return
	     */
	    public int getPages() {
	        return ((int) row + pageSize - 1) / pageSize;
	    }
	    /**
	     * 取得首页
	     * @return
	     */
	    public int getTopPageNo(){
	        return 1;
	    }
	    /**
	     * 上一页
	     * @return
	     */
	    public int getPreviousPageNo(){
	        if(pageIndex<=1){
	            return 1;
	        }
	        return pageIndex-1;
	    }
	    /**
	     * 下一页
	     * @return
	     */
	    public int getNextPageNo(){
	        if(pageIndex>=getBottomPageNo()){
	            return getBottomPageNo();
	        }
	        return pageIndex+1;
	    }
	    /**
	     * 取得尾页
	     * @return
	     */
	    public int getBottomPageNo(){
	        return getPages();
	    }
}
