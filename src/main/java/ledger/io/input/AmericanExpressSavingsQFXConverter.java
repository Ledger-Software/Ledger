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
public class AmericanExpressSavingsQFXConverter extends AbstractUFXConverter {

    public AmericanExpressSavingsQFXConverter(File file, Account account) {
        super(file, account, false);
    }

    protected void parseXml(List<Transaction> transactions, Document xml) throws ConverterException {
        //parse xml
        NodeList transactionTypes = xml.getElementsByTagName("TRNTYPE");
        NodeList transactionDates = xml.getElementsByTagName("DTPOSTED");
        NodeList transactionAmounts = xml.getElementsByTagName("TRNAMT");

        // pull out relevant data and create java objects
        try {
            for (int i = 0; i < transactionTypes.getLength(); i++) {
                Date date = new Date(GenerateEpoch.generate(transactionDates.item(i).getTextContent()));

                Type type = TypeConversion.convert(transactionTypes.item(i).getTextContent());

                // American Savins Bank QFX files don't include any payee information :(
                Payee payee = new Payee("***Not Available***", "Payee information was not included in the provided data");

                int amount = (int) ((long) (Math.floor((Double.parseDouble((transactionAmounts.item(i).getTextContent())) * 100) + 0.5d)));

                List<Tag> tags = new LinkedList<Tag>();
                Note note = new Note("Imported from American Express Savings QFX (does not include Payee info)");

                Transaction transaction = new Transaction(date, type, amount, this.getAccount(), payee, false, tags, note);
                transactions.add(transaction);
            }
        } catch (NullPointerException e) {
            throw new ConverterException("Qfx data invalid!", e);
        } catch (NumberFormatException e2) {
            throw new ConverterException("Qfx data invalid!", e2);
        } catch (DateTimeParseException e3) {
            throw new ConverterException("Qfx data invalid!", e3);
        }
    }
}
