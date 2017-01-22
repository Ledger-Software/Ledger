package ledger.user_interface.utils;

import ledger.database.entity.Type;

import java.util.Comparator;

/**
 * Created by Tayler How on 1/19/2017.
 */
public class TypeComparator implements Comparator<Type> {

    @Override
    public int compare(Type t1, Type t2) {
        return t1.getName().compareTo(t2.getName());
    }
}
