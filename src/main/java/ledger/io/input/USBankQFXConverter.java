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
 * Handles the converting of Quicken Qxf files into our internal transaction objects.
 */
public class USBankQFXConverter extends AbstractQFXConverter {

    public USBankQFXConverter(File file, Account account) {
        super(file, account, true);
    }

    protected void parseXml(List<Transaction> transactions, Document xml) throws ConverterException {
        //parse xml
        NodeList transactionTypes = xml.getElementsByTagName("TRNTYPE");
        NodeList transactionDates = xml.getElementsByTagName("DTPOSTED");
        NodeList transactionAmounts = xml.getElementsByTagName("TRNAMT");
        NodeList names = xml.getElementsByTagName("NAME");
        NodeList memos = xml.getElementsByTagName("MEMO");
        NodeList checkNumbers = xml.getElementsByTagName("CHECKNUM");

        // keep counter for check numbers
        int checkNumberCounter = 0;

        // pull out relevant data and create java objects
        try {
            for (int i = 0; i < transactionTypes.getLength(); i++) {
                Date date = new Date(GenerateEpoch.generate(transactionDates.item(i).getTextContent()));

                Type type = TypeConversion.convert(transactionTypes.item(i).getTextContent());
                Payee payee = new Payee(names.item(i).getTextContent(), "");
                if (names.item(i).getTextContent().contains("DEBIT PURCHASE -VISA ")) {
                    type = TypeConversion.convert("DEBIT_CARD");
                    payee.setName(names.item(i).getTextContent().split("DEBIT PURCHASE -VISA ")[1]);
                } else if (names.item(i).getTextContent().contains("WEB AUTHORIZED PMT ")) {
                    type = TypeConversion.convert("ACH_DEBIT");
                    payee.setName(names.item(i).getTextContent().split("WEB AUTHORIZED PMT ")[1]);
                } else if (names.item(i).getTextContent().contains("ELECTRONIC DEPOSIT ")) {
                    type = TypeConversion.convert("ACH_CREDIT");
                    payee.setName(names.item(i).getTextContent().split("ELECTRONIC DEPOSIT ")[1]);
                }

                String[] memoData = memos.item(i).getTextContent().split("Download from usbank\\.com\\.");
                if (memoData.length > 1 && memoData[1] != null && memoData[1].length() > payee.getName().length()) {
                    payee.setName(memoData[1].substring(1));
                }

                int amount = (int) ((long) (Math.floor((Double.parseDouble((transactionAmounts.item(i).getTextContent())) * 100) + 0.5d)));

                List<Tag> tags = new LinkedList<>();
                Note note = new Note(memos.item(i).getTextContent());

                Transaction transaction = new Transaction(date, type, amount, this.getAccount(), payee, false, tags, note);

                // Add check number if applicable
                if (type.getName().equals("Check")) {
                    transaction.setCheckNumber(Integer.parseInt(checkNumbers.item(checkNumberCounter).getTextContent()));
                    checkNumberCounter++;
                }

                transactions.add(transaction);
            }
        } catch (NullPointerException | DateTimeParseException | NumberFormatException e) {
            throw new ConverterException("Qfx data invalid!", e);
        }
    }
}
