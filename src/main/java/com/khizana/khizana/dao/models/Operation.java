package com.khizana.khizana.dao.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "OPERATIONS")
public class Operation implements Serializable {

	private @Column @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @Column LocalDateTime dateTime;
    private @Column boolean entry;
    private @Column String withdrawnBy;
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
	)
    private List<OperationDetail> details;
	
	private static final long serialVersionUID = -2561423923072887724L;
    
    public Operation() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public boolean isEntry() {
		return entry;
	}

	public void setEntry(boolean entry) {
		this.entry = entry;
	}

	public String getWithdrawnBy() {
		return withdrawnBy;
	}

	public void setWithdrawnBy(String withdrawnBy) {
		this.withdrawnBy = withdrawnBy;
	}

	public List<OperationDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OperationDetail> details) {
		this.details = details;
	}
	
	
}
