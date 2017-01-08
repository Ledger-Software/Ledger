package ledger.updater;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.List;

public class GitHubChecker {
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JacksonFactory JACKSON_FACTORY = new JacksonFactory();
    private Release newerRelease;

    public Release getNewerRelease() {
        return newerRelease;
    }

    public static class Release {
        @Key
        public String tag_name;

        @Key
        public List<Asset> assets;

        @Key
        public String name;

        public String getDownloadURL() {
            for(Asset a: assets) {
                if(a.name.endsWith(".jar")) {
                    return a.browser_download_url;
                }
            }
            return null;
        }

        public String getName() {
            return this.name;
        }

        public String getVersion() {
            return this.tag_name;
        }

        public String getDownloadName() {
            for(Asset a: assets) {
                if(a.name.endsWith(".jar")) {
                    return a.name;
                }
            }
            return null;
        }
    }

    public static class Asset {
        @Key
        public String browser_download_url;
        @Key
        public String name;
    }

    public boolean isUpdateAvaliable() {
        try {
            Release[] releases = getReleases();
            if(hasNewerRelease(releases)) {
                return true;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean hasNewerRelease(Release[] releases) {
        if(releases.length < 1)
            return false;

        String currentVersion = this.getClass().getPackage().getImplementationVersion();
        if(currentVersion == null) {
            return false;
        }

        if(isVersionGreater(releases[0].tag_name, currentVersion)) {
            this.newerRelease = releases[0];
            return true;
        }

        return false;
    }

    private boolean isVersionGreater(String tag_name, String currentVersion) {
        String[] tagSplit = tag_name.split("\\.");
        String[] currentSplit = currentVersion.split("\\.");
        try {
            for(int i =0; i < tagSplit.length; i++) {
                int tagV = Integer.parseInt(tagSplit[i]);
                int currentV = Integer.parseInt(currentSplit[i]);

                if(tagV > currentV)
                    return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
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