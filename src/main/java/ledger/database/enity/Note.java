package ledger.database.enity;

/**
 * Entity that holds a single Note
 */
public class Note implements IEntity {
    private int transactionId;
    private String noteText;

    /**
     * Constructs a Note.
     *
     * @param noteText
     */
    public Note(String noteText) {
        this(-1, noteText);
    }

    /**
     * Constructs a Note.
     *
     * @param transaction_Id
     * @param noteText
     */
    public Note(int transactionID, String noteText) {
        this.transactionId = transactionID;
        this.noteText = noteText;
    }

    /**
     * Returns the Transaction id.
     *
     * @return transactionId
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the Transaction id.
     *
     * @param transactionId
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Returns the Note Text.
     *
     * @return noteText
     */
    public String getNoteText() {
        return noteText;
    }

    /**
     * Sets the text of the note.
     *
     * @param noteText
     */
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
