package ledger.io.input;

import ledger.database.enity.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
public class QfxConverter implements IInAdapter {

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

        System.out.println("After removing metadata: " + sgml);

        // add tags to the all rows that are not STMTTRN
        String[] splitPieces = sgml.split("<");
        LinkedList<String> modifiedPieces = new LinkedList();
        for (String piece : splitPieces) {
            System.out.println(piece);
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

        System.out.println("After stitching: " + correctedXml);


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
            // yyyy-MM-dd'T'HH:mm:ss.SSSZ`  2001-07-04T12:08:56.235-0700
            Date date = new Date(extractDate(transactionDates.item(i).getNodeValue()));
            //TODO: Discuss what to do about type
            Type type = new Type("Error","No type exists");
            int amount = (int) (Double.parseDouble((transactionAmounts.item(i).getNodeValue())) * 100);
            Payee payee = new Payee(names.item(i).getNodeValue(), "");
            //TODO: Discuss what to do about tags
            List<Tag> tags = new LinkedList<>();
            Note note = new Note(memos.item(i).getNodeValue());

            //TODO: Discuss what to do about pending

            Transaction transaction = new Transaction(date, type, amount, this.account, payee, true, tags, note);
            transactions.add(transaction);
        }

        // return list
        return transactions;
    }

    private String extractDate(String xmlDate) {
        StringBuilder extractedDate = new StringBuilder();

        int year = Integer.parseInt(xmlDate.substring(0, 4));
        int month = Integer.parseInt(xmlDate.substring(4, 6));
        int day = Integer.parseInt(xmlDate.substring(6, 8));

        int hours = Integer.parseInt(xmlDate.substring(8, 10));
        int indexOfOpenBracket = xmlDate.indexOf("[");
        int indexOfColon = xmlDate.indexOf(":");
        int timezoneOffset = Integer.parseInt(xmlDate.substring(indexOfOpenBracket + 1, indexOfColon));
        int utcHours = hours - timezoneOffset;
        if (utcHours < 0) {
            utcHours += 24;
            day -= 1;
        } else if (utcHours > 24) {
            utcHours -= 24;
            day += 1;
        }

        int minutes = Integer.parseInt(xmlDate.substring(10, 12));
        int seconds = Integer.parseInt(xmlDate.substring(12, 14));

        extractedDate.append(year);
        extractedDate.append("-");
        extractedDate.append(month);
        extractedDate.append("-");
        extractedDate.append(day);
        extractedDate.append("T");
        extractedDate.append(utcHours);
        extractedDate.append(":");
        extractedDate.append(minutes);
        extractedDate.append(":");
        extractedDate.append(seconds);
        extractedDate.append(".000-0000");

        return extractedDate.toString();
    }
}
