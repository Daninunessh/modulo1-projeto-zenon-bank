package br.com.zenon.fraud;

import java.util.List;

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
    }
}
