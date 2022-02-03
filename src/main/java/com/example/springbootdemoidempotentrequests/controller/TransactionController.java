package com.example.springbootdemoidempotentrequests.controller;

import com.example.springbootdemoidempotentrequests.dal.Transactions;
import com.example.springbootdemoidempotentrequests.services.TransactionService;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Callable;

@RestController("api")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final LoadingCache<String, Object> loadingCache;

    // All Safe Methods are idempotent in nature
    // GET, HEAD and OPTIONS
    @GetMapping("transactions")
    public ResponseEntity<?> getTransactions(){
        List<Transactions> transactions = transactionService.getTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("transactions/{reference}")
    public ResponseEntity<?> getTransactionByRef(@PathVariable String reference){
        Transactions transaction = transactionService.getTransactionByReference(reference);
        if(transaction == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(transaction);
    }
    // End of safe methods

    // PUT and DELETE are also idempotent in nature, but they should not be regarded as safe method
    @PutMapping("transactions/{reference}")
    public ResponseEntity<?> updateTransactionByRef(@PathVariable String reference,
                                                    @RequestBody Transactions request){
        Transactions transaction = transactionService.updateTransactionByReference(reference, request);
        if(transaction == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("transactions/{reference}")
    public ResponseEntity<?> deleteTransactionByRef(@PathVariable String reference){
        Transactions transaction = transactionService.deleteTransactionByReference(reference);
        if(transaction == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

    // All idempotent in nature 👆🏾


    // Not idempotent in nature 👇🏽
    @PostMapping("transactions")
    public ResponseEntity<?> addTransaction(@RequestBody Transactions request){
        Transactions transaction = transactionService.addNewTransaction(request);
        return ResponseEntity.ok(transaction);
    }


    // POST un-idempotency trap
    @PostMapping("transactions/timeout-trap")
    public Callable<?> addTransactionTimeoutTrap(
            @RequestHeader(required = false, value = "Idempotent-Key") String idempotentKey,
            @RequestBody Transactions request) {

        Object cachedTransaction = null;
        if(idempotentKey != null)
            cachedTransaction = loadingCache.getIfPresent(idempotentKey);

        if(cachedTransaction != null) {
            Object finalCachedTransaction = cachedTransaction;
            return () -> finalCachedTransaction;
        }

        Transactions transaction = transactionService.addNewTransaction(request);

        if(idempotentKey != null) {
            loadingCache.put(idempotentKey, transaction);
        }

        return (Callable<Object>) () -> {
            // Add a 10 seconds delay simulating a timeout
            Thread.sleep(10000); // <--- DON'T DO THIS AT HOME

            return transaction;
        };
    }
}
