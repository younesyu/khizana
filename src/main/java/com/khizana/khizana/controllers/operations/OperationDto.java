package com.khizana.khizana.controllers.operations;

import java.io.Serializable;
import java.util.List;

import com.khizana.khizana.dao.models.OperationDetail;

public class OperationDto implements Serializable {

	private Boolean entry;
	private List<OperationDetail> details;
	private String withdrawnBy;
	
	private static final long serialVersionUID = 2111551646425792179L;
	
	public Boolean isEntry() {
		return entry;
	}
	public void setEntry(Boolean entry) {
		this.entry = entry;
	}
	
	public List<OperationDetail> getDetails() {
		return details;
	}
	public void setDetails(List<OperationDetail> details) {
		this.details = details;
	}
	public String getWithdrawnBy() {
		return withdrawnBy;
	}
	public void setWithdrawnBy(String withdrawnBy) {
		this.withdrawnBy = withdrawnBy;
	}
	
	

}
