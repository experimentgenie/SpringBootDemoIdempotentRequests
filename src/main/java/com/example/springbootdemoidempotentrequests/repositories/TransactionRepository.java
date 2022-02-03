package com.example.springbootdemoidempotentrequests.repositories;

import com.example.springbootdemoidempotentrequests.dal.Transactions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transactions, Long> {

    @NonNull
    List<Transactions> findAll();

    Transactions findFirstByReference(String reference);
}
