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

        TransactionRepository transactionRepository;
        transactionRepository = new TransactionListRepository(transactions);

        String notFoundOriginName = "C12345";
        IO.println("--------------------------------------------------");
        transactionRepository.findByOriginName(notFoundOriginName).ifPresentOrElse(
                IO::println,
                () -> IO.println("Transação não encontrada para o cliente: " + notFoundOriginName)
        );

        String existentOriginName = "C1868032458";
        IO.println("--------------------------------------------------");
        transactionRepository.findByOriginName(existentOriginName).ifPresentOrElse(
                IO::println,
                () -> IO.println("Transação não encontrada para o cliente: " + existentOriginName)
        );
        IO.println("--------------------------------------------------");

        long startTime = System.nanoTime();
        transactionRepository.findByOriginName(existentOriginName)
                .ifPresentOrElse(
                        IO::println,
                        () -> IO.println("Transação não encontrada para o cliente: " + existentOriginName)
                );
        long endTime = System.nanoTime();
        IO.println("Tempo de busca - List (ms): " + (endTime - startTime) / 1_000_000.0);
        IO.println("--------------------------------------------------");

        transactionRepository = new TransactionMapRepository(transactions);
        startTime = System.nanoTime();
        transactionRepository.findByOriginName(existentOriginName)
                .ifPresentOrElse(
                        IO::println,
                        () -> IO.println("Transação não encontrada para o cliente: " + existentOriginName)
                );
        endTime = System.nanoTime();
        IO.println("Tempo de busca - Map (ms): " + (endTime - startTime) / 1_000_000.0);
        IO.println("--------------------------------------------------");

    }
}
