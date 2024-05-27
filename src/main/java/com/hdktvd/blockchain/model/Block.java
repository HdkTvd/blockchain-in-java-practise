package com.hdktvd.blockchain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hdktvd.blockchain.StringUtility.StringUtil;
import com.hdktvd.blockchain.transaction.Transaction;

public class Block {
	
	public String hash;
	public String previousHash; 
	private String data;
	private long timeStamp;
	private int nonce;
	
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	
	public Block(String previousHash ) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		
		this.hash = calculateHash();
	}
	
	//Calculate new hash based on blocks contents
	public String calculateHash() {
		String calculatedhash = StringUtil.applySha256( 
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				merkleRoot 
				);
		return calculatedhash;
	}
	
	public void mineBlock(int difficulty) {
		List<String> transactionIds = new ArrayList<>();
		for (Transaction t : transactions) {
			transactionIds.add(t.transactionId);
		}
		merkleRoot = StringUtil.getMerkleRoot(transactionIds);
		
		String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0" 
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}
	
	public boolean addTransaction(Transaction transaction) {
		if (transaction == null) return false;
		if (previousHash != "0") { // genesis block, no transaction processing
			if (!transaction.processTransaction()) {
				System.out.println("Unable to process transaction.");
				return false;
			}
		}
		
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}
	
}