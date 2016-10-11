package ledger.io.converter;

import au.com.bytecode.opencsv.CSVReader;
import ledger.database.enity.Transaction;

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

    public ChaseConverter(File file) {
        this.file = file;
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
                int amount = Integer.parseInt(amountString);
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
}
