package ledger.controller;

import ledger.controller.register.Task;
import ledger.database.IDatabase;
import ledger.database.enity.*;
import ledger.exception.StorageException;

import java.util.List;


/**
 * Created by gert on 10/11/16.
 */
public class DbController {

    public static DbController INSTANCE;
    private IDatabase db;
    private List<CallableMethod> DbExceptionList;
    private List<CallableMethod> DbCompleteList;

    public DbController() {
        INSTANCE = this;
    }


    public Task<Transaction, Object> insertTransaction(final Transaction transaction) throws StorageException {
        return new Task<Transaction, Object>(db::insertTransaction, transaction);

    }

    public Task<Transaction, Object> deleteTransaction(final Transaction transaction) throws StorageException {
        return new Task<Transaction, Object>(db::deleteTransaction, transaction);transaction

    }

    public Task<Transaction, Object> editTransaction(final Transaction transaction) throws StorageException {
        return new Task<Transaction, Object>(db::editTransaction, transaction);

    }

    public Task<Object, List<Transaction>> getAllTransactions() throws StorageException {
        return new Task<Object, List<Transaction>(db::getAllTransactions, null);

    }

    public Task<Account, Object> insertAccount(final Account account) throws StorageException {
        return new Task<Account, Object>(db::insertAccount, account);

    }

    public Task<Account, Object> deleteAccount(final Account account) throws StorageException {
        return new Task<Account, Object>(db::deleteAccount, account);

    }

    public Task<Account, Object> editAccount(final Account account) throws StorageException {
        return new Task<Account, Object>(db::editAccount, account);

    }
    public Task<Object, List<Account>> getAllAccounts() throws StorageException {
        return new Task<Object, List<Account>(db::getAllAccounts, null);

    }
    public Task<Payee, Object> insertPayee(final Payee payee) throws StorageException {
        return new Task<Payee, Object>(db::insertPayee, payee);

    }

    public Task<Payee, Object> deletePayee(final Payee payee) throws StorageException {
        return new Task<Payee, Object>(db::deletePayee, payee);

    }

    public Task<Payee, Object> editPayee(final Payee payee) throws StorageException {
        return new Task<Payee, Object>(db::editPayee, payee);

    }

    public Task<Object, List<Payee>> getAllPayees() throws StorageException {
        return new Task<Object, List<Payee>(db::getAllPayees, null);

    }
    public Task<Note, Object> insertNote(final Note note) throws StorageException {
        return new Task<Note, Object>(db::insertNote, note);

    }

    public Task<Note, Object> deleteNote(final Note note) throws StorageException {
        return new Task<Note, Object>(db::deleteNote, note);

    }

    public Task<Note, Object> editNote(final Note note) throws StorageException {
        return new Task<Note, Object>(db::editNote, note);

    }

    public Task<Object, List<Note>> getAllNotes() throws StorageException {
        return new Task<Object, List<Note>(db::getAllNotes, null);

    }
    public Task<Tag, Object> insertTag(final Tag tag) throws StorageException {
        return new Task<Tag, Object>(db::insertTag, tag);

    }

    public Task<Tag, Object> deleteTag(final Tag tag) throws StorageException {
        return new Task<Tag, Object>(db::deleteTag, tag);

    }

    public Task<Tag, Object> editTag(final Tag tag) throws StorageException {
        return new Task<Tag, Object>(db::editTag, tag);

    }

    public Task<Object, List<Tag>> getAllTags() throws StorageException {
        return new Task<Object, List<Tag>(db::getAllTags, null);

    }
    public Task<Type, Object> insertType(final Type type) throws StorageException {
        return new Task<Type, Object>(db::insertType, type);

    }

    public Task<Type, Object> deleteType(final Type type) throws StorageException {
        return new Task<Type, Object>(db::deleteType, type);

    }

    public Task<Type, Object> editType(final Type type) throws StorageException {
        return new Task<Type, Object>(db::editType, type);

    }

    public Task<Object, List<Type>> getAllTypes() throws StorageException {
        return new Task<Object, List<Type>(db::getAllTypes, null);

    }

}
