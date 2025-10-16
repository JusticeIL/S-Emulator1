package configuration;

import okhttp3.OkHttpClient;

public class ClientConfiguration {
    public static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .cookieJar(new SimpleCookieManager())
            .build();
}
