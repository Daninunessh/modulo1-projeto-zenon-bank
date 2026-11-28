
package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionIngestor {

    public static final int MAX_SIZE = 100_000;

    public List<Transaction> read(String fileName) {
        Path path = Path.of(fileName);

        try {
            List<String> lines = Files.readAllLines(path);

            return lines.stream()
                    .skip(1)
                    .limit(MAX_SIZE)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler arquivo: " + fileName, e);
        }
    }

    private Optional<Transaction> parseTransaction(String line) {

        try {
            String[] chunks = line.split(",");

            int step = Integer.parseInt(chunks[0]);

            TransactionType transactionType = TransactionType.valueOf(chunks[1]);

            if(chunks[2] == null || chunks[2].isEmpty())
                throw new IllegalArgumentException("O valor de amount não pode ser nulo ou vazio");
            BigDecimal amount = new BigDecimal(chunks[2]);

            if(chunks[4] == null || chunks[4].isEmpty() || chunks[5] == null || chunks[5].isEmpty())
                throw new IllegalArgumentException("O valor de origin oldBalance ou newBalance não pode ser nulo ou vazio");
            TransactionCustomer origin = new TransactionCustomer(chunks[3], new BigDecimal(chunks[4]), new BigDecimal(chunks[5]));

            if(chunks[7] == null || chunks[7].isEmpty() || chunks[8] == null || chunks[8].isEmpty())
                throw new IllegalArgumentException("O valor de recipient oldBalance ou newBalance não pode ser nulo ou vazio");
            TransactionCustomer recipient = new TransactionCustomer(chunks[6], new BigDecimal(chunks[7]), new BigDecimal(chunks[8]));

            boolean isFraud = chunks[9].equals("1");
            boolean isFlaggedFraud = chunks[10].equals("1");

            return Optional.of(new Transaction(step, transactionType, amount, origin, recipient, isFraud, isFlaggedFraud));
        } catch (Exception e) {
            System.err.println("Erro ao fazer parse: " + line + " | " + e);
            return Optional.empty();
        }
    }
}
