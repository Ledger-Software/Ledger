package ledger.database.storage.SQL;

import ledger.database.entity.Frequency;
import ledger.database.entity.FrequencyConverter;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.FrequencyTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages database calls for {@link Frequency} objects.
 */
public interface ISQLFrequency extends ISQLiteDatabase {

    @Override
    default void insertFrequency(Frequency frequency) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + FrequencyTable.TABLE_NAME +
                " (" + FrequencyTable.FREQUENCY_NAME + ") VALUES (?)");
            stmt.setString(1, FrequencyConverter.convertFrequencyToString(frequency));
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while inserting frequencies from database.", e);
        }
    }

    @Override
    default List<Frequency> getAllFrequencies() throws StorageException {
        List<Frequency> frequencies = new LinkedList();
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + FrequencyTable.FREQUENCY_NAME + " FROM " + FrequencyTable.TABLE_NAME + ";");

            while (rs.next()) {
                String frequencyName = rs.getString(FrequencyTable.FREQUENCY_NAME);
                Frequency currentFrequency = FrequencyConverter.convertStringToFrequency(frequencyName);
                frequencies.add(currentFrequency);
            }
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all frequencies from database.", e);
        }
        return frequencies;
    }
}
