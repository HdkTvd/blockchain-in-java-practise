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
	
	public Transaction(PublicKey recvPublicKey, PublicKey senderPublicKey, float value, ArrayList<TransactionInput> inputs) {
		this.transactionId = calculateHash();
		this.receiver = recvPublicKey;
		this.sender = senderPublicKey;
		this.value = value;
		this.inputs = inputs;
	}
	
	private String calculateHash() {
		sequence++;
		return StringUtil.applySha256(sender.toString()+receiver.toString()+Float.toString(value)+Integer.toString(sequence));
	}
	public void createSignature(PrivateKey privateKey) {
		String data = sender.toString() + receiver.toString() + Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}
	public boolean verifySignature() {
		String data = sender.toString() + receiver.toString() + Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
}
