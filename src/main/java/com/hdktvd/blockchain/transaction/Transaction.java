package com.hdktvd.blockchain.transaction;

import java.security.*;
import java.util.ArrayList;

import com.hdktvd.blockchain.DumbCoin;
import com.hdktvd.blockchain.StringUtility.StringUtil;

public class Transaction {
	public String transactionId;
	public PublicKey sender;
	public PublicKey receiver;
	public float value;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	public byte[] signature;
	
	private static int sequence = 0;
	
	public Transaction(PublicKey senderPublicKey, PublicKey recvPublicKey, float value, ArrayList<TransactionInput> inputs) {
		this.receiver = recvPublicKey;
		this.sender = senderPublicKey;
		this.value = value;
		this.inputs = inputs;
	}
	
	private String calculateHash() {
		sequence++;
		return StringUtil.applySha256(StringUtil.keyToString(sender)+StringUtil.keyToString(receiver)+Float.toString(value)+Integer.toString(sequence));
	}
	public void createSignature(PrivateKey privateKey) {
		String data = StringUtil.keyToString(sender) + StringUtil.keyToString(receiver) + Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}
	public boolean verifySignature() {
		String data = StringUtil.keyToString(sender) + StringUtil.keyToString(receiver) + Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
	
	public boolean processTransaction() {
//		verify signature
		if (!verifySignature()) {
			System.out.println("#Trasaction signature failed to verify.");
			return false;
		}
		
//		Collect all unspent transactions using transactionInput id and then save to it's transactionOutput id field
		for (TransactionInput ti : inputs) {
			ti.UTXO = DumbCoin.UTXOs.get(ti);
		}
		
//		Check if the total amount is less than the minimum transactions allowed
//		if (getInputsValue() < DumbCoin.minimumTransactions) {
//			
//		}
		
//		5 coins -> Samay -> Txn(input = 5; output1 = 3; output2 => 2) -> Manjrekar got 2 coins whereas leftover 3 was sent back to Samay
		float leftOver = getInputsValue() - value;
		transactionId = calculateHash();   // assign ID to the transaction after taken for process
		outputs.add(new TransactionOutput(value, transactionId, receiver));
		outputs.add(new TransactionOutput(leftOver, transactionId, sender));
		
//		add all the txn outputs to the global copy in DumbCoin class
		for (TransactionOutput to : outputs) {			
			DumbCoin.UTXOs.put(to.id, to);
		}
		
//		since we added all the txn (recvd and spent) above now remove the transactions from the UTXOs that are spent
		for (TransactionInput ti : inputs) {
			if (ti.UTXO == null) continue;  // this are unspent txn as they do not have output id
			DumbCoin.UTXOs.remove(ti.UTXO.id);
		}
		
		return true;
	}
	
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput ti : inputs) {
			if (ti.UTXO == null) continue; // skip if null, there is no output against this input (COINS NOT SPENT)
			total += ti.UTXO.value;
		}
		return total;
	}
	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput to : outputs) {
			total += to.value;
		}
		return total;
	}
}
