package io.pivotal.android.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class SharedPrefsDataStoreFactory implements DataStoreFactory {

    private Context context;
    private FileDataStoreFactory dataStoreFactory;

    public SharedPrefsDataStoreFactory(final Context context) {
        this.context = context.getApplicationContext();

        try {
            final File dataStoreDir = context.getDir("oauth2", Context.MODE_PRIVATE);
            dataStoreFactory = new FileDataStoreFactory(dataStoreDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataStore<StoredCredential> getDataStore(String id) throws IOException {
        return new SharedPrefsDataStore(context, dataStoreFactory.getDataStore(id));
    }

    private static final class SharedPrefsDataStore implements DataStore<StoredCredential> {

        public static final String CREDENTIALS = "credentials";
        public static final String EXPIRATION = "expiration";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String TOKEN_KEY = "token_key";

        private final SharedPreferences preferences;
        private final DataStore dataStore;

        public SharedPrefsDataStore(Context context, DataStore dataStore) {
            this.preferences = context.getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE);
            this.dataStore = dataStore;
        }

        @Override
        public DataStoreFactory getDataStoreFactory() {
            return dataStore.getDataStoreFactory();
        }

        @Override
        public String getId() {
            return dataStore.getId();
        }

        @Override
        public int size() throws IOException {
            return dataStore.size();
        }

        @Override
        public boolean isEmpty() throws IOException {
            return dataStore.isEmpty();
        }

        @Override
        public boolean containsKey(String key) throws IOException {
            return dataStore.containsKey(key);
        }

        @Override
        public boolean containsValue(StoredCredential value) throws IOException {
            return dataStore.containsValue(value);
        }

        @Override
        public Set<String> keySet() throws IOException {
            return dataStore.keySet();
        }

        @Override
        public Collection<StoredCredential> values() throws IOException {
            return dataStore.values();
        }

        @Override
        public StoredCredential get(String key) throws IOException {
            return (StoredCredential) dataStore.get(key);
        }

        @Override
        public DataStore<StoredCredential> set(String key, StoredCredential value) throws IOException {
            storeInSharedPrefs(key, value);
            dataStore.set(key, value);
            return this;
        }

        @Override
        public DataStore<StoredCredential> clear() throws IOException {
            clearSharedPrefs();
            dataStore.clear();
            return this;
        }

        @Override
        public DataStore<StoredCredential> delete(String key) throws IOException {
            clearSharedPrefs(key);
            dataStore.delete(key);
            return this;
        }

        private void storeInSharedPrefs(String key, StoredCredential value) {
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(EXPIRATION, value.getExpirationTimeMilliseconds());
            editor.putString(ACCESS_TOKEN, value.getAccessToken());
            editor.putString(TOKEN_KEY, key);
            editor.commit();
        }

        private void clearSharedPrefs(String key) {
            if (preferences.getString(TOKEN_KEY, "").equals(key)) {
                clearSharedPrefs();
            }
        }

        private void clearSharedPrefs() {
            preferences.edit().clear().commit();
        }
    }
}
