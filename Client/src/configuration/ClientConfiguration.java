package configuration;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class ClientConfiguration {
    public static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .cookieJar(new SimpleCookieManager())
            .readTimeout(2, TimeUnit.MINUTES) // Time waiting for response data
            .writeTimeout(2, TimeUnit.MINUTES) // Time to send request data
            .callTimeout(2, TimeUnit.MINUTES) // Extending call timeout to 2 minutes
            .build();
    public static final int REFRESH_RATE = 1000; // in milliseconds
}