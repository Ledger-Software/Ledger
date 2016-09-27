package ledger.database.manager;

import ledger.database.IDatabase;
import ledger.database.enity.Type;

public class TypeManager implements IManager<Type> {

    private IDatabase db;

    public void TypeManager(IDatabase db){
        this.db = db;
    }

    @Override
    public void insert(Type type) {
        this.db.insertType(type);
    }

    @Override
    public void edit(Type type) {
        this.db.editType(type);
    }


    @Override
    public void delete(Type type) {
        this.db.deleteType(type);
    }
}
