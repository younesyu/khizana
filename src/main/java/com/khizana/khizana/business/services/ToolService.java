package com.khizana.khizana.business.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.khizana.khizana.dao.models.Operation;
import com.khizana.khizana.dao.models.OperationDetail;
import com.khizana.khizana.dao.models.QTool;
import com.khizana.khizana.dao.models.Tool;
import com.khizana.khizana.dao.repositories.ToolRepository;
import com.querydsl.core.BooleanBuilder;

@Service
public class ToolService {

    @Autowired
    ToolRepository toolRepository;
    
    @Autowired
    EntityManager em;

    public void processOperation(Operation operation) {
    	List<Tool> updatedToolList = operation.getDetails().stream().map(detail -> {
    		Tool tool = detail.getTool();
    		Tool toolInDb = toolRepository.findById(tool.getId()).get();
    		Integer quantity = detail.getQuantity();
    		Integer sign = (operation.isEntry())? 1 : -1;
    		
    		toolInDb.setLastOperation(operation.getId());
    		toolInDb.setQuantity(tool.getQuantity() + (sign * quantity));
    		return toolInDb;
    	}).collect(Collectors.toList());
    	
    	toolRepository.saveAll(updatedToolList);
    }

	public List<Tool> checkStocks(List<OperationDetail> operationDetailList) {
		List<Tool> outOfStockTools = new ArrayList<>();
		
		for (OperationDetail operationDetail : operationDetailList) {
			Tool tool = operationDetail.getTool();
			Optional<Tool> toolInDb = toolRepository.findById(tool.getId());
			
			if (toolInDb.isPresent()) {
				if (operationDetail.getQuantity() > toolInDb.get().getQuantity()) {
					outOfStockTools.add(tool);
				}
			} else {
				throw new NoSuchElementException(String.format("Could not find tool with id %d", tool.getId()));
			}
		}
		
		return outOfStockTools;
	}
	
	public List<Tool> autocomplete(String query) {
		QTool qTool = QTool.tool;
		BooleanBuilder predicate = new BooleanBuilder();
		
		predicate.orAllOf(qTool.category.likeIgnoreCase('%' + query + '%'),
						  qTool.description.likeIgnoreCase('%' + query + '%'),
						  qTool.id.like(query + '%'));
		
		return toolRepository.findAll(predicate, PageRequest.of(0, 10)).getContent(); 
	}

	public long getNbToolsRupture() {
    	return toolRepository.count(QTool.tool.quantity.eq(0));
	}
}
