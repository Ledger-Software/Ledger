package ledger.updater;

import ledger.user_interface.utils.VersionComparer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Looks in the folder that the current Jar is running inside for old versions of Ledger Software
 */
public class OldVersionFinder {

    public File[] oldVersions() {
        File jar;
        try {
            jar = new File(this.getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            return new File[0];
        }
        String jarFolder = jar.getParent();

        final String jarName = jar.getName();

        final Pattern p = Pattern.compile("Ledger-(.*)\\.jar");

        return new File(jarFolder).listFiles((dir, name) -> {
            Matcher m = p.matcher(name);
            if(m.matches()) {
                String s = m.group(1);

                return name.matches("Ledger-\\d\\.\\d\\.\\d\\.jar")
                        && !name.equals(jarName)
                        && !VersionComparer.isVersionGreater(s);
            } else {
                return false;
            }
        });
    }
}
