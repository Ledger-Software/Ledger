package ledger.database.storage.SQL.SQLite;

import ledger.database.IDatabase;
import ledger.exception.StorageException;

import java.sql.Connection;

public interface ISQLiteDatabase extends IDatabase {
    Connection getDatabase();

    void rollbackDatabase() throws StorageException;

    void setDatabaseAutoCommit(boolean autoCommit) throws StorageException;
}
