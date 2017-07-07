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
public class BaxterCreditUnionQFXConverter extends AbstractQFXConverter {

    public BaxterCreditUnionQFXConverter(File file, Account account) {
        super(file, account, false);
    }

    protected void parseXml(List<Transaction> transactions, Document xml) throws ConverterException {
        //parse xml
        NodeList transactionTypes = xml.getElementsByTagName("TRNTYPE");
        NodeList transactionDates = xml.getElementsByTagName("DTPOSTED");
        NodeList transactionAmounts = xml.getElementsByTagName("TRNAMT");
        NodeList names = xml.getElementsByTagName("NAME");

        // pull out relevant data and create java objects
        try {
            for (int i = 0; i < transactionTypes.getLength(); i++) {
                Date date = new Date(GenerateEpoch.generate(transactionDates.item(i).getTextContent()));

                Type type = TypeConversion.convert(transactionTypes.item(i).getTextContent());

                Payee payee = new Payee(names.item(i).getTextContent(), "");
				
				// special cases
				if (names.item(i).getTextContent().startsWith("TARGET DEBIT CRD - Location - ")) {
					payee.setName(names.item(i).getTextContent().split("TARGET DEBIT CRD - Location - ")[1]);
				} else if (names.item(i).getTextContent().matches("#\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\b")) {
					payee.setName(names.item(i).getTextContent().split("#\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\b[[:blank:]]-[[:blank:]]")[1]);
				}

                int amount = (int) ((long) (Math.floor((Double.parseDouble((transactionAmounts.item(i).getTextContent())) * 100) + 0.5d)));

                List<Tag> tags = new LinkedList<>();
                Note note = new Note("Imported from Baxter Credit Union QFX");

                Transaction transaction = new Transaction(date, type, amount, this.getAccount(), payee, false, tags, note);
                transactions.add(transaction);
            }
        } catch (NullPointerException | DateTimeParseException | NumberFormatException e) {
            throw new ConverterException("Qfx data invalid!", e);
        }
    }
}