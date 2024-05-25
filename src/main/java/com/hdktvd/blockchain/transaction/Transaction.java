package com.hdktvd.blockchain.transaction;

import java.security.*;
import java.util.ArrayList;

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
		
		this.transactionId = calculateHash();
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
}
