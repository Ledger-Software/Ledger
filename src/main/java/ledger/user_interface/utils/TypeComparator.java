package ledger.user_interface.utils;

import ledger.database.entity.Type;

import java.util.Comparator;

/**
 * {@link Comparator} for {@link Type} that compares based on their name
 */
public class TypeComparator implements Comparator<Type> {

    @Override
    public int compare(Type t1, Type t2) {
        return t1.getName().compareTo(t2.getName());
    }
}
