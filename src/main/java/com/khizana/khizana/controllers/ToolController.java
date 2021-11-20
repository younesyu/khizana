package com.khizana.khizana.controllers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.khizana.khizana.business.parsers.ExcelParser;
import com.khizana.khizana.business.services.ToolService;
import com.khizana.khizana.dao.models.QTool;
import com.khizana.khizana.dao.models.Tool;
import com.khizana.khizana.dao.repositories.OperationRepository;
import com.khizana.khizana.dao.repositories.ToolRepository;
import com.querydsl.core.BooleanBuilder;

import javax.validation.Valid;


@RestController
public class ToolController {
	
	@Autowired
	ToolService toolService;
	
	@Autowired
	OperationRepository operationRepository;

    @Autowired
    ToolRepository toolRepository;
    
    @Autowired
    ExcelParser excelParser;
    
    @GetMapping(value = "/tools/parse")
    public List<Tool> parseExcel() {
        if (toolRepository.count() == 0) {
            excelParser.parseFile();
        }
    	
    	/*Operation operation = new Operation();
    	operation.setDateTime(LocalDateTime.now());
    	operation.setWithdrawnBy("Azzeddine");
    	operation.setEntry(false);
    	operation.setDetails(Arrays.asList(new OperationDetail(((List<Tool>) toolRepository.findAll()).get(0), 5), new OperationDetail(((List<Tool>) toolRepository.findAll()).get(1), 15)));
    	operationRepository.save(operation);*/
    	return (List<Tool>) toolRepository.findAll();
    }

    @GetMapping(value = "/tools/findAll")
    public Page<Tool> findAll(@RequestParam Integer pageNumber,
		                       @RequestParam Integer size,
		                       @RequestParam String sortField,
		                       @RequestParam String sortDirection,
							   @RequestParam(required=false, defaultValue = "") String filterItem,
		                       @RequestParam(required=false, defaultValue = "") String filterDesignation,
		                       @RequestParam(required=false, defaultValue = "") String filterCategory,
		                       @RequestParam(required=false) boolean filterInStock,
		                       @RequestParam(required=false) boolean filterRupture) {
        Sort.Direction direction = ("asc".equals(sortDirection))? Sort.Direction.ASC : Sort.Direction.DESC;
        
        BooleanBuilder filters = new BooleanBuilder();
        QTool tool = QTool.tool;
        
        if (!"".equals(filterItem)) {
        	filters.and(tool.id.like(filterItem + "%"));
        }
        if (!"".equals(filterDesignation)) {
        	filters.and(tool.description.containsIgnoreCase(filterDesignation));
        }
        if (!"".equals(filterCategory)) {
        	filters.and(tool.category.equalsIgnoreCase(filterCategory));
        }

        if (filterInStock && !filterRupture) {
        	filters.and(tool.quantity.gt(0));
        }
        
    	if (filterRupture && !filterInStock) {
    		filters.and(tool.quantity.eq(0));
        }
        
        return toolRepository.findAll(filters, PageRequest.of(pageNumber, size, direction, sortField));
    }
    
    @GetMapping(value = "/tools/find")
    public Optional<Tool> findById(@RequestParam Long id) {
    	return toolRepository.findById(id);
    }

    @PostMapping(value = "/tools/peek")
    public Page<Tool> peek(@RequestBody Map<String, Object> body) {
        if (toolRepository.findAll().iterator().hasNext()) {
            parseExcel();
        }
        Sort.Direction direction = ((List<Map<String, Object>>) body.get("order"))
                .get(0).get("dir").equals("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Integer columnNumber = (Integer) ((List<Map<String, Object>>) body.get("order"))
                .get(0).get("column");
        String columnName = (String) ((List<Map<String, Object>>) body.get("columns")).get(columnNumber).get("data");
        PageRequest pageRequest = PageRequest.of((Integer) body.get("start"), (Integer) body.get("length"), direction, columnName);
        return toolRepository.findAll(pageRequest);
    }

    @PostMapping(value = "/tools/find")
    public List<Tool> find(@RequestBody Map<String, Object> body) {
        if (toolRepository.findAll().iterator().hasNext()) {
            parseExcel();
        }
        BooleanBuilder predicate = new BooleanBuilder();
        QTool qTool = QTool.tool;
        List<Long> ids = ((List<String>) body.get("data")).stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        predicate.and(qTool.id.in(ids));
        return (List<Tool>) toolRepository.findAll(predicate);
    }


    @RequestMapping(value = "/tools/table", method = RequestMethod.POST)
    public DataTablesOutput<Tool> getTools(@Valid @RequestBody DataTablesInput input,
                                           @RequestParam(required = false) String category,
                                           @RequestParam(required = false) List<Boolean> etatDansStock) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (category != null && !category.isBlank()) {
            predicate.and(QTool.tool.category.equalsIgnoreCase(category));
        }
        if (etatDansStock != null) {
            etatDansStock = etatDansStock.stream().filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (etatDansStock.size() == 1) { // contains either true or false
                if (etatDansStock.contains(Boolean.FALSE)) {
                    predicate.and(QTool.tool.quantity.eq(0));
                } else {
                    predicate.and(QTool.tool.quantity.gt(0));
                }
            }
        }

        return toolRepository.findAll(input, predicate);
    }

    @GetMapping(value = "/alltools")
    public List<Tool> findAll() {
    	return (List<Tool>) toolRepository.findAll();
    }

    @GetMapping(value = "/tool")
    public @ResponseBody Optional<Tool> find(@RequestParam Long id) {
    	return toolRepository.findById(id);
    }

    @PutMapping(value = "/tools/update")
    public void update(@RequestParam Integer id) {
    }

    @PostMapping(value = "/tools/save")
    public void save() {
    }

    @GetMapping(value = "/tools/categories")
    public String[] getCategories() {
    	return toolRepository.findDistinctCategories();
    }

    @GetMapping(value = "/tools/autocomplete")
    public @ResponseBody List<Tool> autocomplete(@RequestParam String query) {
        return toolService.autocomplete(query);
    }

    @GetMapping(value = "/tools/rupture")
    public @ResponseBody
    long nbToolsRupture() {
        parseExcel();
        return toolService.getNbToolsRupture();
    }
}
