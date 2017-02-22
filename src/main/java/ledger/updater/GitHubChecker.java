package ledger.updater;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Key;
import ledger.user_interface.utils.VersionComparer;

import java.io.IOException;
import java.util.List;

/**
 * Checks the Github repo releases to see if there is a new version.
 * The version it checks against is the one included in the manifest file.
 * (will only work if running for a jar with a manifest version)
 */
public class GitHubChecker {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JacksonFactory JACKSON_FACTORY = new JacksonFactory();
    private Release newerRelease;

    public Release getNewerRelease() {
        return newerRelease;
    }

    public static class Release {
        @Key
        public final String tag_name;

        @Key
        public final List<Asset> assets;

        @Key
        public final String name;

        public Asset getDownloadAsset() {
            for (Asset a : assets) {
                if (a.name.endsWith(".jar")) {
                    return a;
                }
            }
            return null;
        }

        public String getDownloadURL() {
            Asset asset = getDownloadAsset();
            return asset == null ? null : asset.browser_download_url;
        }

        public String getName() {
            return this.name;
        }

        public String getVersion() {
            return this.tag_name;
        }

        public String getDownloadName() {
            Asset asset = getDownloadAsset();
            return asset == null ? null : asset.name;
        }
    }

    public static class Asset {
        @Key
        public final String browser_download_url;
        @Key
        public final String name;
        @Key
        public final long size;
    }

    public boolean isUpdateAvailable() {
        try {
            Release[] releases = getReleases();
            if (hasNewerRelease(releases)) {
                return true;
            }
        } catch (IOException e) {
            System.err.println("Unable to reach Github to check for updates.");
        }
        return false;
    }

    private boolean hasNewerRelease(Release[] releases) {
        if (releases.length < 1)
            return false;

        String currentVersion = this.getClass().getPackage().getImplementationVersion();
        if (currentVersion == null) {
            return false;
        }

        if (VersionComparer.isVersionGreater(releases[0].tag_name, currentVersion)) {
            this.newerRelease = releases[0];
            return true;
        }

        return false;
    }


    private Release[] getReleases() throws IOException {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JACKSON_FACTORY)));
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl("https://api.github.com/repos/Ledger-Software/Ledger/releases"));

        return request.execute().parseAs(Release[].class);
    }
}