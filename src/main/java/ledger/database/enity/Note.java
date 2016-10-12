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
     * @param noteText The text of the new Note
     */
    public Note(String noteText) {
        this(-1, noteText);
    }

    /**
     * Constructs a Note.
     *
     * @param transactionID The Id of the transaction associated with the Note
     * @param noteText      The text of the new note
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
     * @param transactionId The Id of the transaction associated with the Note
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
     * @param noteText The text of the new note
     */
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
