package ledger;

/**
 * Created by Tayler How on 9/24/2016.
 */

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.*;

public class SQLiteTest {
    static Connection dbConnection;

    @BeforeClass
    public static void SetupSQLiteConnection() {
        dbConnection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            dbConnection = DriverManager.getConnection("jdbc:sqlite:test.db");

            Statement stmt = dbConnection.createStatement();
            String createTableSQL = "CREATE TABLE COMPANY " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " AGE            INT     NOT NULL, " +
                    " ADDRESS        CHAR(50), " +
                    " SALARY         REAL)";
            stmt.executeUpdate(createTableSQL);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    @Test
    public void TestSQLiteCRUD() throws java.sql.SQLException{
        Statement stmt = this.dbConnection.createStatement();

        String insertionSQL1 = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (1, 'Paul', 32, 'California', 20000.00 );";
        stmt.executeUpdate(insertionSQL1);

        String insertionSQL2 = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (2, 'Allen', 25, 'Texas', 15000.00 );";
        stmt.executeUpdate(insertionSQL2);

        String insertionSQL3 = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (3, 'Teddy', 23, 'Norway', 20000.00 );";
        stmt.executeUpdate(insertionSQL3);

        String insertionSQL4 = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (4, 'Mark', 25, 'Rich-Mond ', 65000.00 );";
        stmt.executeUpdate(insertionSQL4);

        int resultSetSize = 0;

        ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );

        while ( rs.next() ) {
            resultSetSize++;
        }
        assertEquals(4, resultSetSize);

        resultSetSize = 0;

        String deletionSQL = "DELETE from COMPANY where ID=2";
        stmt.executeUpdate(deletionSQL);

        rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );

        while ( rs.next() ) {
            resultSetSize++;
        }
        assertEquals(3, resultSetSize);

        rs.close();
        stmt.close();
    }

    @AfterClass
    public static void TeardownSQLiteConnection() {
        try {
            Statement stmt = dbConnection.createStatement();
            String dropTableSQL = "DROP TABLE COMPANY";
            stmt.executeUpdate(dropTableSQL);
            stmt.close();

            dbConnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
