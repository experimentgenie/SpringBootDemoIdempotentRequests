package com.example.springbootdemoidempotentrequests.services;

import com.example.springbootdemoidempotentrequests.dal.Transactions;
import com.example.springbootdemoidempotentrequests.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transactions> getTransactions(){
        return transactionRepository.findAll();
    }

    public Transactions getTransactionByReference(String reference) {
        return transactionRepository.findFirstByReference(reference);
    }

    public Transactions updateTransactionByReference(String reference, Transactions request) {
        Transactions transaction = getTransactionByReference(reference);
        if(transaction != null){
            BeanUtils.copyProperties(request, transaction, "id", "reference", "createdAt");
            transaction = transactionRepository.save(transaction);
        }
        return transaction;
    }

    public Transactions deleteTransactionByReference(String reference) {
        Transactions transaction = getTransactionByReference(reference);
        if(transaction != null){
            CompletableFuture.runAsync(() -> transactionRepository.delete(transaction));
        }
        return transaction;
    }

    public Transactions addNewTransaction(Transactions request) {
        Transactions transaction = new Transactions();
        BeanUtils.copyProperties(request, transaction);
        return transactionRepository.save(transaction);
    }
}
