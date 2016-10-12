package ledger.io.input;

import ledger.database.enity.Account;
import ledger.database.enity.Transaction;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
/**
 * Created by Jesse Shellabarger on 10/11/2016.
 */
public class QxfConverter implements IInAdapter {

    private File qxfFile;
    private Account account;

    public QxfConverter(File file, Account account) {
        this.qxfFile = file;
        this.account = account;
    }

    @Override
    public List<Transaction> convert() throws IOException {
        List<Transaction> transactions = new ArrayList();

        // read in given file
        String sgml = new Scanner(qxfFile).useDelimiter("\\Z").next();

        // chop off everything before and after transactions (before/after STMTTRN)
        int indexOfFirstTrans = sgml.indexOf("<STMTTRN>");
        sgml = sgml.substring(indexOfFirstTrans);

        int lastIndexOfTrans = sgml.indexOf("</BANKTRANLIST>");
        sgml = sgml.substring(0, lastIndexOfTrans -1 );

        System.out.println("After removing metadata: " + sgml);

        // add tags to the all rows that are not STMTTRN
        String[] splitPieces = sgml.split("<");
        LinkedList<String> modifiedPieces = new LinkedList();
        for (String piece : splitPieces) {
            boolean matches = Pattern.matches("(?!STMTTRN[>].*|[/]STMTTRN[>].*).*[>].*", piece);
            if (matches) {
                modifiedPieces.add(piece.replace(">", "/>"));
            } else {
                modifiedPieces.add(piece);
            }
        }

        StringBuilder correctedXml = new StringBuilder();
        for (String piece : modifiedPieces) {
            correctedXml.append("<");
            correctedXml.append(piece);
        }

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
        InputSource is = new InputSource(new StringReader(sgml));
        Document xml = null;
        try {
            xml = builder.parse(is);
        } catch (SAXException e) {
            //TODO: Handle This
            e.printStackTrace();
        }

        //parse xml

        // pull out relevant data and create java objects


        // return list
        return transactions;
    }
}
