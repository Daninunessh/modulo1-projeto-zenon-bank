package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FraudAnalyzer {

    private final List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    public long isFraud() {
        return getFraudStream()
                .count();
    }

    public List<BigDecimal> biggestAmount() {
        return highValueFraudStream()
                .map(Transaction::amount)
                .limit(3)
                .toList();
    }

    public List<String> suspectClients() {
        return highValueFraudStream()
                .map(t -> t.origin().name())
                .distinct()
                .limit(5)
                .toList();
    }

    public BigDecimal totalAmount() {
        return getFraudStream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType, Long> fraudByTransactionType() {
        return getFraudStream()
                .collect(Collectors.groupingBy(Transaction::transactionType, Collectors.counting()));
    }

    private Stream<Transaction> getFraudStream() {
        return transactions.stream()
                .filter(Transaction::isFraud);
    }

    private Stream<Transaction> highValueFraudStream() {
        return getFraudStream()
                .sorted(Comparator.comparing(Transaction::amount).reversed());
    }
}