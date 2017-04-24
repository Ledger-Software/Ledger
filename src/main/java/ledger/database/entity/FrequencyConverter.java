package ledger.database.entity;

/**
 * Converts between {@link Frequency} and String
 */
public class FrequencyConverter {

    public static String convertFrequencyToString(Frequency freq) {
        switch (freq) {
            case Daily: return "Daily";
            case Weekly: return "Weekly";
            case Monthly: return "Monthly";
            case Yearly: return "Yearly";
            default: return "UNKNOWN";
        }
    }

    public static Frequency convertStringToFrequency(String freqName) {
        switch (freqName) {
            case "Daily": return Frequency.Daily;
            case "Weekly": return Frequency.Weekly;
            case "Monthly": return Frequency.Monthly;
            case "Yearly": return Frequency.Yearly;
            default: return Frequency.UNKNOWN;
        }
    }
}
