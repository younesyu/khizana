package com.khizana.khizana.dao.models;
import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "TOOLS")
public class Tool implements Serializable {
	
	private @Column @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @Column String description;
    private @Column Integer quantity;
    private @Column String category;
    private @Column Long lastOperation;

/*
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
        )
    private List<OperationDetail> operationDetails;
*/

	private static final long serialVersionUID = -6608663114204532514L;
	
    public Tool() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	
	public String getCategory() {
		return category;
	}

	public void setCategory(String categorie) {
		this.category = categorie;
	}

	public Long getLastOperation() {
		return lastOperation;
	}

	public void setLastOperation(Long lastOperation) {
		this.lastOperation = lastOperation;
	}

/*
	public List<OperationDetail> getOperationDetails() {
		return operationDetails;
	}

	public void setOperationDetails(List<OperationDetail> operationDetails) {
		this.operationDetails = operationDetails;
	}	
*/

}
