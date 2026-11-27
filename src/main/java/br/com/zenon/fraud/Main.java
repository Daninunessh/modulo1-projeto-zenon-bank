package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {

    void main() {

        TransactionIngestor transactionIngestor = new TransactionIngestor();
        List<Transaction> transactions = transactionIngestor.read("data/paysim.csv");

        transactions.stream()
                .limit(10)
                .forEach(IO::println);

        IO.println("--------------------------------------------------");

        List<Transaction> errorTransactions = transactionIngestor.read("data/paysim_with_bad_data.csv");

        errorTransactions.forEach(IO::println);

        FraudAnalyzer fraudAnalyzer = new FraudAnalyzer(transactions);

        long fraudTransactions = fraudAnalyzer.isFraud();

        List<BigDecimal> topFraudulentAmounts = fraudAnalyzer.biggestAmount();

        List<String> suspectClients = fraudAnalyzer.suspectClients();

        BigDecimal totalLoss = fraudAnalyzer.totalAmount();

        Map<TransactionType, Long> transactionsFraud = fraudAnalyzer.fraudByTransactionType();

        IO.println("--------------------------------------------------");
        IO.println("1. Total de Fraudes: " + fraudTransactions);
        IO.println("--------------------------------------------------");
        IO.println("2. Top 3 Fraudes de Maior Valor:");
        topFraudulentAmounts.forEach(amount -> IO.println("- %.2f".formatted(amount)));
        IO.println("--------------------------------------------------");
        IO.println("3. Clientes Suspeitos:");
        suspectClients.forEach(IO::println);
        IO.println("--------------------------------------------------");
        IO.println("4. Prejuízo Total: " + totalLoss);
        IO.println("--------------------------------------------------");
        IO.println("5. Fraudes por Tipo:");
        transactionsFraud.forEach((type, count) -> IO.println("- " + type + ": " + count));
    }
}
