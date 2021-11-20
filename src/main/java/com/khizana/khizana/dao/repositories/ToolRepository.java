package com.khizana.khizana.dao.repositories;


import org.springframework.data.jpa.datatables.qrepository.QDataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.khizana.khizana.dao.models.Tool;

@Repository
public interface ToolRepository extends QDataTablesRepository<Tool, Long>, QuerydslPredicateExecutor<Tool> {
	
	@Query("SELECT DISTINCT t.category FROM Tool t")
	public String[] findDistinctCategories();
}
