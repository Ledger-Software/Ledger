package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.exception.StorageException;

import java.sql.Connection;

/**
 * Created by CJ on 10/11/2016.
 */
public interface ISQLiteDatabase extends IDatabase {
    Connection getDatabase();

    void rollbackDatabase() throws StorageException;

    void setDatabaseAutoCommit(boolean autoCommit) throws StorageException;
}
