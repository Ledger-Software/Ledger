package ledger.database.enity;

/**
 * Entity that holds a single Note
 */
public class Note implements IEntity {
    private int transactionId;
    private String noteText;

    /**
     * Constructs a Note.
     * @param transaction_Id
     * @param noteText
     */
    public Note(int transaction_Id, String noteText) {
        this.transactionId = transaction_Id;
        this.noteText = noteText;
    }

    /**
     * Returns the Transaction id.
     * @return transactionId
     */
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    /**
     * Returns the Note Text.
     * @return noteText
     */
    public String getNoteText() {
        return noteText;
    }

    /**
     * Sets the text of the note.
     * @param noteText
     */
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
