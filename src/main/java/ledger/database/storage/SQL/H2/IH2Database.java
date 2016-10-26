package ledger.database.storage.SQL.H2;

import ledger.database.IDatabase;
import ledger.exception.StorageException;

import java.sql.Connection;

public interface IH2Database extends IDatabase {
    Connection getDatabase();

    void rollbackDatabase() throws StorageException;

    void setDatabaseAutoCommit(boolean autoCommit) throws StorageException;
}
