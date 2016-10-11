package ledger.io.converter;

import au.com.bytecode.opencsv.CSVReader;
import ledger.database.enity.*;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by CJ on 10/10/2016.
 */
public class ChaseConverter implements IConverter {

    private File file;
    private Account account;

    public ChaseConverter(File file, Account account) {
        this.file = file;
        this.account = account;
    }

    @Override
    public List<Transaction> convert() throws FileNotFoundException {
        CSVReader reader = new CSVReader(new FileReader(file), ',', '"', 1);
        List<Transaction> transactions = new LinkedList<>();

        try {
            String [] nextLine;

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

            while ((nextLine = reader.readNext()) != null) {
                String details = nextLine[0];
                String dateString = nextLine[1];
                String description = nextLine[2];
                String amountString = nextLine[3];
                String typeString = nextLine[4];
                String balanceString = nextLine[5];
                String checkNumberString = nextLine[6];

                //Date date, Type type, int amount, Account account, Payee payee, boolean pending, List<Tag> tagList, Note note

                Date date = df.parse(dateString);
                boolean pending = isPending(amountString);
                int amount = 0;
                if(!pending)
                    amount = Integer.parseInt(amountString);

                Type type = TypeConversion.convert(typeString);

                // TODO: Find Payee
                Payee payee = null;
                List<Tag> tags = null;
                Note note = null;

                Transaction transaction = new Transaction(date, type, amount, this.account, payee, pending, tags, note);

                transactions.add(transaction);
            }

        } catch (IOException e) {
            // TODO: Decide what to do
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO: Throw Custom Exception
            e.printStackTrace();
        }
        return transactions;
    }

    private boolean isPending(String amount) {
        return amount == null || amount.isEmpty();
    }
}
