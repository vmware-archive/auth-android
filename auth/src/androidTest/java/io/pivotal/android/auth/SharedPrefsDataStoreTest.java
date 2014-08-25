package io.pivotal.android.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;

import java.io.IOException;

public class SharedPrefsDataStoreTest extends AndroidTestCase {

    public static final String KEY = "KEY";
    public static final Long EXPIRATION_TIME = 12345678900000L;
    public static final String ACCESS_TOKEN = "access_token12345567890";
    public static final String REFRESH_TOKEN = "refresh_token1234567890";

    public void testStoredCredentials() throws IOException {
        final StoredCredential credential = new StoredCredential();
        credential.setExpirationTimeMilliseconds(EXPIRATION_TIME);
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(REFRESH_TOKEN);

        final SharedPrefsDataStoreFactory factory = new SharedPrefsDataStoreFactory(mContext);
        final DataStore dataStore = factory.getDataStore(KEY);
        dataStore.set(KEY, credential);

        final StoredCredential stored = (StoredCredential) dataStore.get(KEY);

        assertEquals(EXPIRATION_TIME, stored.getExpirationTimeMilliseconds());
        assertEquals(ACCESS_TOKEN, stored.getAccessToken());
        assertEquals(REFRESH_TOKEN, stored.getRefreshToken());
    }

    public void testCredentialsAddedToSharedPreferences() throws IOException {
        final StoredCredential credential = new StoredCredential();
        credential.setExpirationTimeMilliseconds(EXPIRATION_TIME);
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(REFRESH_TOKEN);

        final SharedPrefsDataStoreFactory factory = new SharedPrefsDataStoreFactory(mContext);
        final DataStore dataStore = factory.getDataStore(KEY);
        dataStore.set(KEY, credential);

        final SharedPreferences preferences = mContext.getSharedPreferences("credentials", Context.MODE_PRIVATE);

        assertEquals(EXPIRATION_TIME.longValue(), preferences.getLong("expiration", 0L));
        assertEquals(ACCESS_TOKEN, preferences.getString("access_token", null));
    }
}
