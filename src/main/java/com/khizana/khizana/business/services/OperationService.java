package com.khizana.khizana.business.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.khizana.khizana.dao.models.Operation;
import com.khizana.khizana.dao.models.OperationDetail;
import com.khizana.khizana.dao.models.Tool;
import com.khizana.khizana.dao.repositories.OperationDetailRepository;
import com.khizana.khizana.dao.repositories.OperationRepository;

@Service
public class OperationService {
    @Autowired
    OperationRepository operationRepository;
    
    @Autowired
    OperationDetailRepository operationDetailRepository;

    @Autowired
    ToolService toolService;
    
    @Transactional
    public void save(Boolean entry, List<OperationDetail> operationDetailList, String withdrawnBy) {
    	if (entry == false) {
    		List<Tool> outOfStockTools = toolService.checkStocks(operationDetailList);
    		if (!outOfStockTools.isEmpty()) {
    			throw new IllegalStateException("Stocks indisponible");
    		}    			
    	}
    	
    	// Save details
    	operationDetailList = (List<OperationDetail>) operationDetailRepository.saveAll(operationDetailList);

    	Operation operation = new Operation();
        operation.setEntry(entry);
        operation.setDateTime(LocalDateTime.now());
        operation.setDetails(operationDetailList);
        operation.setWithdrawnBy(withdrawnBy);

        Operation finalOperation = operationRepository.save(operation);

        // Sauvegarde de l'opération dans les détails
        /*operationDetailList = operationDetailList.stream().peek(operationDetail ->
                operationDetail.setOperation(finalOperation)).collect(Collectors.toList());
        operationDetailRepository.saveAll(operationDetailList);
*/
        toolService.processOperation(operation);
    	
    }
}
