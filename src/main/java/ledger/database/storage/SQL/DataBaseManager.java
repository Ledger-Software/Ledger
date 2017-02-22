package ledger.database.storage.SQL;

import ledger.exception.StorageException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Abstract class for handling Databases
 */
public abstract class DataBaseManager {

    public abstract Connection getDatabase();

    public void executeList(List<String> statements) throws StorageException {
        try {
            for (String statement : statements) {
                Statement stmt = getDatabase().createStatement();
                stmt.execute(statement);
            }
        } catch (SQLException e) {
            throw new StorageException("Unable to Create Table", e);
        }

    }
}
