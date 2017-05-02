package ledger.user_interface.utils;

import ledger.database.entity.Frequency;

import java.util.Comparator;

public class FrequencyComparator implements Comparator<Frequency> {

    @Override
    public int compare(Frequency o1, Frequency o2) {
        return o1.name().compareTo(o2.name());
    }
}
