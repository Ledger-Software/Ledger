package ledger.io.util;

import ledger.controller.DbController;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Takes a List of Transactions and for each adds the Tags for their payees to it.
 */
public class Tagger {

    private final List<Transaction> transactions;

    public Tagger(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * For each Transaction it sets it appends its tag list with that of its payee.
     */
    public void tagTransactions() {
        for(Transaction t: transactions) {
            List<Tag> originalList = t.getTags();
            Set<Tag> fullList = new HashSet<Tag>();
            fullList.addAll(originalList);

            List<Tag> payeeTags = DbController.INSTANCE.getTagsForPayee(t.getPayee()).waitForResult();
            if(payeeTags != null)
                fullList.addAll(payeeTags);

            List<Tag> newList = new ArrayList<Tag>();
            newList.addAll(fullList);
            t.setTags(newList);
        }
    }
    
}
