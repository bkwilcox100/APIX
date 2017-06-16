package com.heb.liquidsky.common;

import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.api.services.cloudkms.v1.CloudKMSScopes;
import com.google.api.services.cloudkms.v1.model.DecryptRequest;
import com.google.api.services.cloudkms.v1.model.DecryptResponse;
import com.google.api.services.cloudkms.v1.model.EncryptRequest;
import com.google.api.services.cloudkms.v1.model.EncryptResponse;

public final class EncryptionUtil {

	private EncryptionUtil() {
		// singleton class
	}

	/**
	 * 
	 * @param projectId
	 * @param ringId
	 * @param keyId
	 * @param plaintext
	 * @return an encrypted byte[]
	 * @throws IOException
	 */
	public static byte[] encrypt(String projectId, String ringId, String keyId, byte[] plaintext) 
			throws IOException {
		
		return encrypt(projectId, ringId, keyId, null, plaintext);
	}
	
	/**
	 * 
	 * @param projectId
	 * @param ringId
	 * @param keyId
	 * @param version
	 * @param plaintext
	 * @return an encrypted byte[]
	 * @throws IOException
	 */
	public static byte[] encrypt(String projectId, String ringId, String keyId, String version, byte[] plaintext) 
			throws IOException {
	
		String location = "global";
		
		String keyName = String.format(
				"projects/%s/locations/%s/keyRings/%s/cryptoKeys/%s", 
				projectId, location, ringId, keyId);
		
		if(version != null) {
			keyName += "/cryptoKeyVersions/" + version;
		}
		
		CloudKMS kms = createAuthorizedClient();
		
		EncryptRequest request = new EncryptRequest().encodePlaintext(plaintext);
		EncryptResponse response = kms.projects().locations().keyRings().cryptoKeys()
				.encrypt(keyName, request)
				.execute();
		
		return response.decodeCiphertext();
	}
	
	/**
	 * 
	 * @param projectId
	 * @param ringId
	 * @param keyId
	 * @param encrypted
	 * @return a plain text byte[]
	 * @throws IOException
	 */
	public static byte[] decrypt(String projectId, String ringId, String keyId, byte[] encrypted) 
			throws IOException {
		
		String location = "global";
		
		String keyName = String.format(
				"projects/%s/locations/%s/keyRings/%s/cryptoKeys/%s", 
				projectId, location, ringId, keyId);
		
		CloudKMS kms = createAuthorizedClient();
		
		DecryptRequest request = new DecryptRequest().encodeCiphertext(encrypted);
		DecryptResponse response = kms.projects().locations().keyRings().cryptoKeys()
				.decrypt(keyName, request)
				.execute();
		
		return response.decodePlaintext();
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private static CloudKMS createAuthorizedClient() 
			throws IOException {
		
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		
		// TODO update to use service account for automated builds/deploys etc
		GoogleCredential credential = GoogleCredential.getApplicationDefault(transport, jsonFactory);
		
		if(credential.createScopedRequired()) {
			credential = credential.createScoped(CloudKMSScopes.all());
		}
		
		// TODO replace hard coded application name 
		return new CloudKMS.Builder(transport, jsonFactory, credential)
				.setApplicationName("HEB Middle Layer")
				.build();
	}
}
