package configuration;

import okhttp3.OkHttpClient;

public class ClientConfiguration {
    public static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .cookieJar(new SimpleCookieManager())
            .build();
    public static final int REFRESH_RATE = 1000; // in milliseconds
}
