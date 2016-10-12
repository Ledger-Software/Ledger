package ledger.io.input;

import ledger.database.enity.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Jesse Shellabarger on 10/11/2016.
 */
public class QfxConverter implements IInAdapter<Transaction> {

    private File qfxFile;
    private Account account;

    public QfxConverter(File file, Account account) {
        this.qfxFile = file;
        this.account = account;
    }

    @Override
    public List<Transaction> convert() throws IOException {
        List<Transaction> transactions = new ArrayList();

        // read in given file
        String sgml = new Scanner(qfxFile).useDelimiter("\\Z").next();

        // chop off everything before and after transactions (before/after STMTTRN)
        int indexOfFirstTrans = sgml.indexOf("<STMTTRN>");
        sgml = sgml.substring(indexOfFirstTrans);

        int lastIndexOfTrans = sgml.indexOf("</BANKTRANLIST>");
        sgml = sgml.substring(0, lastIndexOfTrans);

        // add tags to the all rows that are not STMTTRN
        String[] splitPieces = sgml.split("<");
        LinkedList<String> modifiedPieces = new LinkedList();
        for (String piece : splitPieces) {
            boolean matches = Pattern.matches("(?!STMTTRN[>].*|[/]STMTTRN[>].*).*[>].*", piece);
            if (matches) {
                int lastClosingTag = piece.indexOf(">");
                String xmlTag = piece.substring(0, lastClosingTag);
                String correctXml = "<" + piece + "</" + xmlTag + ">";
                modifiedPieces.add(correctXml + "\n");
            } else if (!piece.equals("")) {
                modifiedPieces.add("<" + piece + "\n");
            }
        }

        StringBuilder correctedXml = new StringBuilder();
        correctedXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<QFX>");
        for (String piece : modifiedPieces) {
            correctedXml.append(piece);
        }
        correctedXml.append("\n" + "</QFX>");

        // XML Parse
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            //TODO: Handle this
            e.printStackTrace();
        }
        InputSource is = new InputSource(new StringReader(correctedXml.toString()));
        Document xml = null;
        try {
            xml = builder.parse(is);
        } catch (SAXException e) {
            //TODO: Handle This
            e.printStackTrace();
        }

        //parse xml
        NodeList transactionTypes = xml.getElementsByTagName("TRNTYPE");
        NodeList transactionDates = xml.getElementsByTagName("DTPOSTED");
        NodeList transactionAmounts = xml.getElementsByTagName("TRNAMT");
        NodeList names = xml.getElementsByTagName("NAME");
        NodeList memos = xml.getElementsByTagName("MEMO");

        // pull out relevant data and create java objects
        for (int i = 0; i < transactionTypes.getLength(); i++) {
            Date date = new Date(GenerateEpoch.generate(transactionDates.item(i).getTextContent()));
            //TODO: Discuss what to do about type
            Type type = new Type("Error", "No type exists");
            int amount = (int) ((long) (Math.floor((Double.parseDouble((transactionAmounts.item(i).getTextContent())) * 100) + 0.5d)));
            Payee payee = new Payee(names.item(i).getTextContent(), "");
            //TODO: Discuss what to do about tags
            List<Tag> tags = new LinkedList<>();
            Note note = new Note(memos.item(i).getTextContent());

            //TODO: Discuss what to do about pending

            Transaction transaction = new Transaction(date, type, amount, this.account, payee, true, tags, note);
            transactions.add(transaction);
        }

        // return list
        return transactions;
    }
}
