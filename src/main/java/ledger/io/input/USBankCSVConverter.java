package ledger.io.input;

import au.com.bytecode.opencsv.CSVReader;
import ledger.database.entity.*;
import ledger.exception.ConverterException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Takes a CSV file from US Bank and turns it into a List of {@link Transaction}
 */
public class USBankCSVConverter extends AbstractCSVConverter {

    public USBankCSVConverter(File file, Account account) {
        super(file, account);
    }

    @Override
    protected List<Transaction> readFile(CSVReader reader) throws ConverterException {
        List<Transaction> transactions = new LinkedList();

        try {

            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                String dateString = nextLine[0];
                String typeString = nextLine[1];
                String payeeString = nextLine[2];
                String memoString = nextLine[3];
                String amountString = nextLine[4];

                Date date = AbstractCSVConverter.df.parse(dateString);

                Type type = TypeConversion.convert(typeString);
                Payee payee = new Payee(payeeString, "");
                if (payeeString.contains("DEBIT PURCHASE -VISA ")) {
                    type = TypeConversion.convert("DEBIT_CARD");
                    payee.setName(payeeString.split("DEBIT PURCHASE -VISA ")[1]);
                } else if (payeeString.contains("WEB AUTHORIZED PMT ")) {
                    type = TypeConversion.convert("ACH_DEBIT");
                    payee.setName(payeeString.split("WEB AUTHORIZED PMT ")[1]);
                } else if (payeeString.contains("ELECTRONIC DEPOSIT ")) {
                    type = TypeConversion.convert("ACH_CREDIT");
                    payee.setName(payeeString.split("ELECTRONIC DEPOSIT ")[1]);
                } else if (payeeString.equals("CHECK")) {
                    type = TypeConversion.convert(payeeString);
                }

                String[] memoData = memoString.split("Download from usbank\\.com\\.");
                if (memoData.length > 0 && memoData[1].length() > payee.getName().length()) {
                    payee.setName(memoData[1].substring(1));
                }

                int amount = (int) ((long) (Math.floor((Double.parseDouble((amountString)) * 100) + 0.5d)));

                List<Tag> tags = new LinkedList<>();
                Note note = new Note(memoString);

                Transaction transaction = new Transaction(date, type, amount, getAccount(), payee, false, tags, note);

                // Add check number if applicable
                if (type.getName().equals("Check")) {
                    transaction.setCheckNumber(Integer.parseInt(typeString));
                }

                transactions.add(transaction);
            }
        } catch (IOException e) {
            throw new ConverterException("Unable to read file.", e);
        } catch (ParseException e) {
            throw new ConverterException("File is not in the valid CSV format.", e);
        }

        return transactions;
    }
}
