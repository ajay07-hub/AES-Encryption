// imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
// import java.io.IOException;
import java.util.Arrays;
// import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

// import java.security.NoSuchAlgorithmException; 
// import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
// import javax.crypto.IllegalBlockSizeException;
// import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

// functions for encrypting and decrypting files
public class Aesutils {
	private static final String ALG = "AES";

	// encrypt files
	public static void encrypt(String key, File inputFile, File outputFile) {
		doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
	}

	// decrypt files
	public static void decrypt(String key, File inputFile, File outputFile) {
		doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
	}

	// creates runtime exception
	public static class CustomException extends RuntimeException {
		public CustomException(String s) {
			super(s);
		}
	}

	// ------------------------------------ key secure function ------------------------------------

	static String generateKey(String str, String key) {
		int x = str.length();
		for (int i = 0;; i++) {
			if (x == i)
				i = 0;
			if (key.length() == str.length())
				break;
			key += (key.charAt(i));
		}
		return key;
	}

	public static String stringEncryption(String text, String key) {
		String cipherText = "";
		int cipher[] = new int[key.length()];
		if (key.length() > text.length()) {
			String text2 = "";
			for (int i = 0; i < key.length(); i++) {
				text2 = text2 + text.charAt(i);
			}
		}
		for (int i = 0; i < key.length(); i++) {
			// -32 // ascii of 32 is space starts here til 177 Del button 145 total keys
			// with ascii value
			cipher[i] = (((int) text.charAt(i) - 32 + (int) key.charAt(i) - 32) % 145) + 32;
		}
		for (int i = 0; i < key.length(); i++) {
			int x = cipher[i];
			cipherText += (char) x;
		}
		return cipherText;
	}

	static String generateStrongPassword(String aeskey) {
		String vingereKey = generateKey(aeskey, "VIN");
		String key = stringEncryption(aeskey, vingereKey);
		return key;
	}

	// ----------------------------------- key secure function done -----------------------------------

	// crypto method
	private static void doCrypto(int cipherMode, String aeskey, File inputFile, File outputFile) {
		try {
			// Generates secure key from user given key by encrypting it with vingere cipher
			String key = generateStrongPassword(aeskey);
			// System.out.println(key);

			// generate 16 byte key
			byte[] keyBytes = key.getBytes("UTF-8");
			keyBytes = Arrays.copyOf(keyBytes, 16);
			Key secretKey = new SecretKeySpec(keyBytes, ALG);
			// create cipher
			Cipher cipher = Cipher.getInstance(ALG);
			cipher.init(cipherMode, secretKey);
			// write to file
			FileInputStream inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);
			byte[] outputBytes = cipher.doFinal(inputBytes);
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(outputBytes);
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			throw new CustomException(e.getClass().getSimpleName());
		}
	}
}
