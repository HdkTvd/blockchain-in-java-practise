package com.hdktvd.blockchain.StringUtility;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
	
	public static String getMerkleRoot(List<String> transactionsId) {
		if (transactionsId.size() == 1) {
            return transactionsId.get(0);
        }

		List<String> newLevel = new ArrayList<>();
        for (int i = 0; i < transactionsId.size(); i += 2) {
            if (i + 1 < transactionsId.size()) {
                newLevel.add(applySha256(transactionsId.get(i) + transactionsId.get(i + 1)));
            } else {
                newLevel.add(applySha256(transactionsId.get(i) + transactionsId.get(i))); // If odd number of elements, hash the last one with itself
            }
        }

        return getMerkleRoot(newLevel); // Recurse to the next level
	}
}