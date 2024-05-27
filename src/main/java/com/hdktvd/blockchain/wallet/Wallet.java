package com.hdktvd.blockchain.wallet;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hdktvd.blockchain.DumbCoin;
import com.hdktvd.blockchain.transaction.Transaction;
import com.hdktvd.blockchain.transaction.TransactionInput;
import com.hdktvd.blockchain.transaction.TransactionOutput;

public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
		
	public Wallet() {
		generateKey();
	}
	
	public void generateKey() {
		try {			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom randomNumGen = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			
			keyGen.initialize(ecSpec, randomNumGen);
			KeyPair keyPair = keyGen.generateKeyPair();
			
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public float getBalance() {
		float total = 0;
//		populate the wallet with global UTXO map by matching publickey
		for (Map.Entry<String, TransactionOutput> item : DumbCoin.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			if (UTXO.isMine(publicKey)) {
				UTXOs.put(UTXO.id, UTXO);
				total += UTXO.value;
			}
		}
		return total;
	}
	
	public Transaction sendFunds(PublicKey receiver, float value) {
		if (getBalance() < value) {
			System.out.println("Unsufficient balance. Transaction discarded.");
			return null;
		}
		
//		generate all the input txns from unspent output txns until the required amount is reached.
		float total = 0;
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			inputs.add(new TransactionInput(UTXO.id));
			total += UTXO.value;
			if (total >= value) {
				break;
			}
		}
		
		Transaction transaction = new Transaction(publicKey, receiver, value, inputs);
//		sign the transaction with sender's private key such that it can be validated on receive
		transaction.createSignature(privateKey);
		
//		remove inputs txns from UTXO list as those will be spent later
		for ( TransactionInput ti : inputs ) {
			DumbCoin.UTXOs.remove(ti.transactionOutputId);
		}
		
		return transaction;
	}
}
