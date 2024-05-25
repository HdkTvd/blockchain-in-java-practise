package com.hdktvd.blockchain.StringUtility;

import java.security.MessageDigest;

import java.util.Base64;

import java.security.*;

public class StringUtil {
	public static String applySha256(String input){		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		byte[] output = new byte[0];
		
		try {
			Signature dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] ipBytes = input.getBytes();
			dsa.update(ipBytes);
			byte[] realSign = dsa.sign();
			output = realSign;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return output;
	}
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature verifier = Signature.getInstance("ECDSA", "BC");
			verifier.initVerify(publicKey);
			byte[] dataBytes = data.getBytes();
			verifier.update(dataBytes);
			return verifier.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String keyToString(Key key) {
		return Base64.getEncoder().encodeToString((key.getEncoded()));
	}
}