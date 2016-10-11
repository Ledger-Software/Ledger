package ledger.database.storage;

/**
 * Created by CJ on 10/2/2016.
 */
public class SQLiteTableConstants {
    static final String tableTransaction = "CREATE TABLE IF NOT EXISTS TRANSACT " +
            "(TRANS_ID INTEGER PRIMARY KEY  AUTOINCREMENT, " +
            "TRANS_DATETIME REAL        NOT NULL, " +
            "TRANS_AMOUNT INT           NOT NULL," +
            "TRANS_PENDING BOOLEAN      NOT NULL, " +
            "TRANS_ACCOUNT_ID INT       NOT NULL, " +
            "TRANS_PAYEE_ID INT         NOT NULL, " +
            "TRANS_TYPE_ID INT          NOT NULL, " +
            "FOREIGN KEY(TRANS_ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID), " +
            "FOREIGN KEY(TRANS_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)," +
            "FOREIGN KEY(TRANS_TYPE_ID) REFERENCES TYPE(TYPE_ID)" +
            ")";

    static final String tableNote = "CREATE TABLE IF NOT EXISTS NOTE" +
            "(NOTE_TRANS_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "NOTE_TEXT TEXT             NOT NULL, " +
            "FOREIGN KEY(NOTE_TRANS_ID) REFERENCES TRANSACT(TRANS_ID)" +
            ")";

    static final String tableTagToTrans = "CREATE TABLE IF NOT EXISTS TAG_TO_TRANS " +
            "(TTTS_TAG_ID INT           NOT NULL, " +
            "TTTS_TRANS_ID INT          NOT NULL, " +
            "FOREIGN KEY(TTTS_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTTS_TRANS_ID) REFERENCES TRANSACT(TRANS_ID)" +
            ")";

    static final String tableTag = "CREATE TABLE IF NOT EXISTS TAG " +
            "(TAG_ID INTEGER PRIMARY KEY    AUTOINCREMENT, " +
            "TAG_NAME TEXT              NOT NULL, " +
            "TAG_DESC TEXT              NOT NULL" +
            ")";

    static final String tableType = "CREATE TABLE IF NOT EXISTS TYPE " +
            "(TYPE_ID INTEGER PRIMARY KEY    AUTOINCREMENT, " +
            "TYPE_NAME TEXT              NOT NULL, " +
            "TYPE_DESC TEXT              NOT NULL" +
            ")";

    static final String tableAccount = "CREATE TABLE IF NOT EXISTS ACCOUNT " +
            "(ACCOUNT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ACCOUNT_NAME TEXT           NOT NULL, " +
            "ACCOUNT_DESC TEXT           NOT NULL" +
            ")";

    static final String tableAccountBalance = "CREATE TABLE IF NOT EXISTS ACCOUNT_BALANCE " +
            "(ABAL_ACCOUNT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ABAL_DATETIME REAL         NOT NULL, " +
            "ABAL_AMOUNT INT            NOT NULL " +
            ")";

    static final String tablePayee = "CREATE TABLE IF NOT EXISTS PAYEE " +
            "(PAYEE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "PAYEE_NAME TEXT           NOT NULL, " +
            "PAYEE_DESC TEXT           NOT NULL" +
            ")";

    static final String tableTagToPayee = "CREATE TABLE IF NOT EXISTS TAG_TO_PAYEE " +
            "(TTPE_TAG_ID INT           NOT NULL, " +
            "TTPE_PAYEE_ID INT          NOT NULL, " +
            "FOREIGN KEY(TTPE_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTPE_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)" +
            ")";
}
