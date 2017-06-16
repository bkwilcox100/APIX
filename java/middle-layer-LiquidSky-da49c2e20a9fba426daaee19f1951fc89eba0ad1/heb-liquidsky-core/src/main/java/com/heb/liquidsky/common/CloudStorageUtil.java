package com.heb.liquidsky.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

/**
 * Utility methods used for working with Cloud Storage.
 * 
 * @author rholliday
 */
public final class CloudStorageUtil {

	public static final String FILE_SEPARATOR = "/";

	private CloudStorageUtil() {
		// singleton class
	}

	/**
	 * Encrypt and write a file to cloud storage
	 */
	public static Blob createFileInStorage(String bucketName, String blobPath, byte[] bytesToBeWrittenToStorage) throws IOException {
		Blob blob = getBlobFromStorage(bucketName, blobPath);
		if (blob != null) {
			throw new IOException("Blob already exists in Cloud Storage for " + bucketName + ":" + blobPath);
		}
		return writeFileToStorage(bucketName, blobPath, bytesToBeWrittenToStorage);
	}

	/**
	 * Encrypt and write a file to cloud storage
	 */
	public static Blob createEncryptedFileInStorage(String bucketName, String blobPath, byte[] plaintext, String keyringId, String keyId) throws IOException {
		byte[] encyptedBytesToBeWrittenToStorage = EncryptionUtil.encrypt(HebEnvironmentProperties.getInstance().getAppEngineId(), keyringId, keyId, plaintext);
		return createFileInStorage(bucketName, blobPath, encyptedBytesToBeWrittenToStorage);
	}

	/**
	 * Method used to delete properties in Cloud Storage.
	 */
	public static void deleteFileFromStorage(String bucketName, String blobPath) throws IOException {
		Blob blob = getBlobFromStorage(bucketName, blobPath);
		if (blob == null) {
			throw new IOException("No blob exists in Cloud Storage for " + bucketName + ":" + blobPath);
		}
		try {
			StorageOptions.getDefaultInstance().getService().delete(blob.getBlobId());
		} catch (StorageException e) {
			throw new IOException("Failure while deleting " + bucketName + ":" + blobPath, e);
		}
	}

	public static boolean exists(String bucketName, String blobPath) throws IOException {
		return (getBlobFromStorage(bucketName, blobPath) != null);
	}

	private static Blob getBlobFromStorage(String bucketName, String blobPath) throws IOException {
		Bucket bucket = getCloudStorageBucket(bucketName);
		return bucket.get(blobPath);
	}

	public static List<String> getBlobPathsInFolder(String bucketName, String folder) throws IOException {
		Bucket bucket = getCloudStorageBucket(bucketName);
		List<String> results = new ArrayList<>();
		for (Blob blob : bucket.list(Storage.BlobListOption.prefix(folder)).iterateAll()) {
			results.add(blob.getName());
		}
		return results;
	}

	private static Bucket getCloudStorageBucket(String bucketName) throws IOException {
		if (StringUtils.isBlank(bucketName)) {
			throw new IOException("bucketName is required");
		}
		Storage storage = StorageOptions.getDefaultInstance().getService();	
		Bucket bucket = storage.get(bucketName);
		if (bucket == null) {
			throw new IOException("Cloud Storage properties bucket does not exist: " + bucketName);
		}
		return bucket;
	}

	public static String readFileFromStorage(String bucketName, String blobPath) throws IOException {
		Blob blob = getBlobFromStorage(bucketName, blobPath);
		if (blob == null) {
			throw new IOException("No blob exists in Cloud Storage for " + bucketName + ":" + blobPath);
		}
		byte[] bytesReadFromStorage = blob.getContent();
		return (bytesReadFromStorage != null) ? new String(bytesReadFromStorage) : null;
	}

	public static String readEncryptedFileFromStorage(String bucketName, String blobPath, String keyringId, String keyId) throws IOException {
		Blob blob = getBlobFromStorage(bucketName, blobPath);
		if (blob == null) {
			throw new IOException("No blob exists in Cloud Storage for " + bucketName + ":" + blobPath);
		}
		byte[] bytesReadFromStorage = blob.getContent();
		if (bytesReadFromStorage == null) {
			return null;
		}
		byte[] unencryptedBytes = EncryptionUtil.decrypt(HebEnvironmentProperties.getInstance().getAppEngineId(), keyringId, keyId, bytesReadFromStorage);
		return (unencryptedBytes != null) ? new String(unencryptedBytes) : null;
	}

	/**
	 * Method used to update properties in Cloud Storage.
	 */
	public static Blob updateFileInStorage(String bucketName, String blobPath, byte[] bytesToBeWrittenToStorage) throws IOException {
		Blob blob = getBlobFromStorage(bucketName, blobPath);
		if (blob == null) {
			throw new IOException("No blob exists in Cloud Storage for " + bucketName + ":" + blobPath);
		}
		return writeFileToStorage(bucketName, blobPath, bytesToBeWrittenToStorage);
	}

	/**
	 * Method used to update properties in Cloud Storage.
	 */
	public static Blob updateEncryptedFileInStorage(String bucketName, String blobPath, byte[] plaintext, String keyringId, String keyId) throws IOException {
		byte[] encyptedBytesToBeWrittenToStorage = EncryptionUtil.encrypt(HebEnvironmentProperties.getInstance().getAppEngineId(), keyringId, keyId, plaintext);
		return updateFileInStorage(bucketName, blobPath, encyptedBytesToBeWrittenToStorage);
	}

	private static Blob writeFileToStorage(String bucketName, String blobPath, byte[] bytesToBeWrittenToStorage) throws IOException {
		List<Acl> acls = new ArrayList<>();
		acls.add(Acl.of(Acl.User.ofAllAuthenticatedUsers(), Acl.Role.READER));
		BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobPath).setAcl(acls).build();
		try {
			return StorageOptions.getDefaultInstance().getService().create(blobInfo, bytesToBeWrittenToStorage);
		} catch (StorageException e) {
			throw new IOException("Failure while writing to storage " + bucketName + ":" + blobPath, e);
		}
	}
}
