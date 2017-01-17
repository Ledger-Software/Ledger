package ledger.user_interface.utils;

/**
 * Compares
 */
public class VersionComparer {

    /**
     * Returns true if a is greater then b
     * @param a
     * @param b
     * @return
     */
    public static boolean isVersionGreater(String a, String b) {
        String[] tagSplit = a.split("\\.");
        String[] currentSplit = b.split("\\.");
        try {
            for (int i = 0; i < tagSplit.length; i++) {
                int tagV = Integer.parseInt(tagSplit[i]);
                int currentV = Integer.parseInt(currentSplit[i]);

                if (tagV > currentV)
                    return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns true if tag_Name is newer then the current version of the running Jar.
     * @param tag_name
     * @return
     */
    public static boolean isVersionGreater(String tag_name) {
        String currentVersion = VersionComparer.class.getPackage().getImplementationVersion();
        if (currentVersion == null) {
            return false;
        }

        return isVersionGreater(tag_name, currentVersion);
    }
}
