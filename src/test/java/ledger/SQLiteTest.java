package ledger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class SQLiteTest {
    static Connection dbConnection;

    @BeforeClass
    public static void SetupSQLiteConnection() throws java.sql.SQLException, java.lang.ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        dbConnection = DriverManager.getConnection("jdbc:sqlite:src/test/resources/test.db");

        Statement stmt = dbConnection.createStatement();
        String createTableSQL = "CREATE TABLE COMPANY " +
                "(ID INT PRIMARY KEY     NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " AGE            INT     NOT NULL, " +
                " ADDRESS        CHAR(50), " +
                " SALARY         REAL)";
        stmt.executeUpdate(createTableSQL);
        stmt.close();
    }

    @Test
    public void TestSQLiteCRUD() throws java.sql.SQLException {
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

        ResultSet rs = stmt.executeQuery("SELECT * FROM COMPANY;");

        while (rs.next()) {
            resultSetSize++;
        }
        assertEquals(4, resultSetSize);

        resultSetSize = 0;

        String deletionSQL = "DELETE from COMPANY where ID=2";
        stmt.executeUpdate(deletionSQL);

        rs = stmt.executeQuery("SELECT * FROM COMPANY;");

        while (rs.next()) {
            resultSetSize++;
        }
        assertEquals(3, resultSetSize);

        rs.close();
        stmt.close();
    }

    @AfterClass
    public static void TeardownSQLiteConnection() throws java.sql.SQLException, java.io.IOException {
        Statement stmt = dbConnection.createStatement();
        String dropTableSQL = "DROP TABLE COMPANY";
        stmt.executeUpdate(dropTableSQL);
        stmt.close();

        dbConnection.close();

        Path dbPath = Paths.get("src/test/resources/test.db");
        Files.delete(dbPath);
    }
}
