package com.khizana.khizana.controllers.operations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khizana.khizana.controllers.ToolController;
import com.khizana.khizana.dao.models.*;
import com.khizana.khizana.dao.repositories.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.khizana.khizana.business.services.OperationService;
import com.khizana.khizana.business.services.ToolService;
import com.khizana.khizana.dao.repositories.OperationRepository;
import com.querydsl.core.BooleanBuilder;

import javax.validation.Valid;

@RestController
public class OperationController {

	@Autowired
	OperationRepository operationRepository;

	@Autowired
	ToolService toolService;

	@Autowired
	OperationService operationService;

	@Autowired
	ToolController toolController;

	@GetMapping(value = "/operation")
	public Page<Operation> findAll(@RequestParam Integer pageNumber, @RequestParam Integer size,
			@RequestParam String sortField, @RequestParam String sortDirection,
			@RequestParam(required = false ) Long filterId, 
			@RequestParam(required = false) String filterPerson,
			@RequestParam(defaultValue = "false") Boolean filterEntree, 
			@RequestParam(defaultValue = "false") Boolean filterSortie,
			@RequestParam(required = false) List<Long> filterContainsTool) {
 		
        BooleanBuilder filters = new BooleanBuilder();
        QOperation qOperation = QOperation.operation;
        QOperationDetail qOperationDetail = QOperationDetail.operationDetail;
        QTool qTool = QTool.tool;
        
        Sort.Direction direction = ("asc".equals(sortDirection)) ? Sort.Direction.ASC : Sort.Direction.DESC;
 		
		if (filterId != null) {
        	filters.and(qOperation.id.like(filterId + "%"));
        }
		
		List<Boolean> filterInputOutput = new ArrayList<>();
		if (filterEntree) filterInputOutput.add(true);
		if (filterSortie) filterInputOutput.add(false);
		if (!filterInputOutput.isEmpty()) filters.and(qOperation.entry.in(filterInputOutput));
		
		if (filterPerson != null && !"".equals(filterPerson)) {
			filters.and(qOperation.withdrawnBy.startsWithIgnoreCase(filterPerson));
		}
		
		if (filterContainsTool != null && !filterContainsTool.isEmpty()) {
			filters.and(qTool.id.eq(1L));
			filters.and(qOperationDetail.tool.eq(qTool));
			filters.and(qOperationDetail.in(qOperation.details));
		}
 		
		return operationRepository.findAll(filters, PageRequest.of(pageNumber, size, direction, sortField));
	}

	@GetMapping(value = "/operations")
	public List<Operation> operations() {
		return (List<Operation>) operationRepository.findAll();
	}

	@PostMapping(value = "/operation/save")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody void save(@RequestBody Map<String, Object> requestBody) throws Exception {
		Boolean isEntry = (Boolean) requestBody.get("is-entry");
		String withdrawnBy = null;
		if (isEntry == false) {
			withdrawnBy = (String) requestBody.get("withdrawn-by");
		}
		List<OperationDetail> operationDetails =
				((List<Map<String, Object>>) requestBody.get("validated-tools"))
				.stream().map(toolJson -> {
					OperationDetail operationDetail = new OperationDetail();
					if (isEntry) {
						operationDetail.setQuantity((Integer) toolJson.get("incoming"));
					} else {
						operationDetail.setQuantity((Integer) toolJson.get("requested"));
					}
					ObjectMapper objectMapper = new ObjectMapper();
					if (isEntry) {
						toolJson.remove("incoming");
					} else {
						toolJson.remove("requested");
					}
					Tool t = objectMapper.convertValue(toolJson, Tool.class);
					operationDetail.setTool(t);
					return operationDetail;
				}).collect(Collectors.toList());
		operationService.save(isEntry, operationDetails, withdrawnBy);
	}

	@GetMapping(value = "/operation/{id}")
	public @ResponseBody Optional<Operation> findOne(@PathVariable Long id) {
		return operationRepository.findById(id);
	}

	@PostMapping(value = "/operations/table")
	public DataTablesOutput<Operation> getOperations(
			@Valid @RequestBody DataTablesInput input,
			 @RequestParam(required = false) List<String> horodatage,
			 @RequestParam(required = false) List<Boolean> isEntry) {

		BooleanBuilder predicate = new BooleanBuilder();
		if (!CollectionUtils.isEmpty(horodatage)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			List<LocalDate> localDateList = horodatage.stream()
					.map(h -> LocalDate.parse(h, formatter))
					.collect(Collectors.toList());
			predicate.and(
				QOperation.operation.dateTime
						.after(localDateList.get(0).atStartOfDay())
				 .and(QOperation.operation.dateTime
						 .before(localDateList.get(1).plusDays(1)
											  .atStartOfDay()))
			);
		}
		if (isEntry != null) {
			isEntry = isEntry.stream().filter(Objects::nonNull)
					.collect(Collectors.toList());
			if (isEntry.size() == 1) { // contains either true or false
				predicate.and(QOperation.operation.entry.eq(isEntry.get(0)));
			}
		}

		return operationRepository.findAll(input, predicate);
	}
}
