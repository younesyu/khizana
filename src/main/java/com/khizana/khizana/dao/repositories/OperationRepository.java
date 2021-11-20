package com.khizana.khizana.dao.repositories;

import com.khizana.khizana.dao.models.Operation;
import org.springframework.data.jpa.datatables.qrepository.QDataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends QDataTablesRepository<Operation, Long>, QuerydslPredicateExecutor<Operation> {

}
