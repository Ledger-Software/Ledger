package ledger.io.input;

import ledger.database.entity.*;
import ledger.exception.ConverterException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the converting of Quicken Qxf files of the old format into our internal transaction objects.
 */
public class FifthThirdBankQFXConverter extends AbstractUFXConverter {

    public FifthThirdBankQFXConverter(File file, Account account) {
        super(file, account, true);
    }

    protected void parseXml(List<Transaction> transactions, Document xml) throws ConverterException {
        //parse xml
        NodeList transactionTypes = xml.getElementsByTagName("TRNTYPE");
        NodeList transactionDates = xml.getElementsByTagName("DTPOSTED");
        NodeList transactionAmounts = xml.getElementsByTagName("TRNAMT");
        NodeList names = xml.getElementsByTagName("NAME");
        NodeList memos = xml.getElementsByTagName("MEMO");

        // pull out relevant data and create java objects
        try {
            for (int i = 0; i < transactionTypes.getLength(); i++) {
                Date date = new Date(GenerateEpoch.generate(transactionDates.item(i).getTextContent()));

                Type type = TypeConversion.convert(transactionTypes.item(i).getTextContent());

                int amount = (int) ((long) (Math.floor((Double.parseDouble((transactionAmounts.item(i).getTextContent())) * 100) + 0.5d)));
                Payee payee = new Payee(names.item(i).getTextContent(), "");

                List<Tag> tags = new LinkedList<>();
                Note note = new Note(memos.item(i).getTextContent());

                Transaction transaction = new Transaction(date, type, amount, this.getAccount(), payee, false, tags, note);
                transactions.add(transaction);
            }
        } catch (NullPointerException | DateTimeParseException | NumberFormatException e) {
            throw new ConverterException("Qfx data invalid!", e);
        }
    }
}
