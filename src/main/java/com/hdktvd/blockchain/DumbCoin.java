package com.hdktvd.blockchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.gson.GsonBuilder;
import com.hdktvd.blockchain.StringUtility.StringUtil;
import com.hdktvd.blockchain.model.Block;
import com.hdktvd.blockchain.transaction.*;
import com.hdktvd.blockchain.wallet.Wallet;

public class DumbCoin {
	public static ArrayList<Block> blockchain = new ArrayList<Block>(); 
	public static int difficulty = 5;
	
	public static Wallet wallet1;
	public static Wallet wallet2;
	
	public static float minimumTransaction = 0.1f;
	
	public static Transaction genesisTransaction;
	
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	public static void main(String[] args) {	
//		Mining coins
//		blockchain.add(new Block("Hi im the first block", "0"));
//		System.out.println("Trying to Mine block 1... ");
//		blockchain.get(0).mineBlock(difficulty);
//		
//		blockchain.add(new Block("Yo im the second block",blockchain.get(blockchain.size()-1).hash));
//		System.out.println("Trying to Mine block 2... ");
//		blockchain.get(1).mineBlock(difficulty);
//		
//		blockchain.add(new Block("Hey im the third block",blockchain.get(blockchain.size()-1).hash));
//		System.out.println("Trying to Mine block 3... ");
//		blockchain.get(2).mineBlock(difficulty);	
//		
//		System.out.println("\nBlockchain is Valid: " + isChainValid());
//		
//		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//		System.out.println("\nThe block chain: ");
//		System.out.println(blockchainJson);
//		
//		Wallets and transactions
//		Transaction t1 = new Transaction(wallet1.publicKey, wallet2.publicKey, 5, null);
//		t1.createSignature(wallet1.privateKey);
		
//		System.out.println("Verify signature - " + t1.verifySignature());

		Security.addProvider(new BouncyCastleProvider());
		
		wallet1 = new Wallet();
		wallet2 = new Wallet();
		Wallet coinbase = new Wallet();
		
		System.out.println("wallet 1 public key - " + StringUtil.keyToString(wallet1.publicKey));
		System.out.println("wallet 1 private key - " + StringUtil.keyToString(wallet1.privateKey));		
		
		System.out.println("wallet 2 public key - " + StringUtil.keyToString(wallet2.publicKey));
		System.out.println("wallet 2 private key - " + StringUtil.keyToString(wallet2.privateKey));
		
//		Perform a transaction from wallet1 to wallet2
		
//		Create genesis transaction
		genesisTransaction = new Transaction(coinbase.publicKey, wallet1.publicKey, 100, null);
		genesisTransaction.createSignature(coinbase.privateKey);
		genesisTransaction.transactionId = "0";
		genesisTransaction.outputs.add(new TransactionOutput(100, genesisTransaction.transactionId, wallet1.publicKey));
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		System.out.println("Creating and mining genesis block...");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		
//		testing
		Block block1 = new Block(genesis.hash);
		System.out.println("Wallet 1 balance - " + wallet1.getBalance() + ". Wallet 1 sending 40 dumb coins to wallet 2...");
		block1.addTransaction(wallet1.sendFunds(wallet2.publicKey, 40f));
		addBlock(block1);
		System.out.println("Wallet 1 balance - " + wallet1.getBalance());
		System.out.println("Wallet 2 balance - " + wallet2.getBalance());
		
		Block block2 = new Block(block1.hash);
		System.out.println("Wallet 1 balance - " + wallet1.getBalance() + ". Wallet 1 sending 1000 dumb coins to wallet 2...");
		block2.addTransaction(wallet1.sendFunds(wallet2.publicKey, 1000f));
		addBlock(block2);
		System.out.println("Wallet 1 balance - " + wallet1.getBalance());
		System.out.println("Wallet 2 balance - " + wallet2.getBalance());
		
		Block block3 = new Block(block2.hash);
		System.out.println("Wallet 2 balance - " + wallet2.getBalance() + ". Wallet 2 sending 35 dumb coins to wallet 1...");
		block3.addTransaction(wallet2.sendFunds(wallet1.publicKey, 35f));
//		addBlock(block3);
		System.out.println("Wallet 1 balance - " + wallet1.getBalance());
		System.out.println("Wallet 2 balance - " + wallet2.getBalance());
		
		isChainValid();
		
//		TODO: save transactions history in wallets
}
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
			
			TransactionOutput tempOutput;
			for (Transaction currentTransaction : currentBlock.transactions) {
				if (!currentTransaction.verifySignature()) {
					System.out.printf("Transaction [%s] verification failed. Signature invalid.%n", currentTransaction.transactionId);
					return false;
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.printf("Transaction [%s] input and output values don't match.%n", currentTransaction.transactionId);
					return false;
				}
				
				for (TransactionInput ti : currentTransaction.inputs) {
					tempOutput = tempUTXOs.get(ti.transactionOutputId);
					
					if (tempOutput == null) {
						System.out.println("Transaction output missing from UTXOs.");
						return false;
					}
					if (tempOutput.value != ti.UTXO.value) {
						System.out.println("Transaction output value does not match with current transaction in the block.");
						return false;
					}
					
					tempUTXOs.remove(ti.transactionOutputId);
				}
				
				for (TransactionOutput to : currentTransaction.outputs) {
					tempUTXOs.put(to.id, to);
				}
				
				if (currentTransaction.outputs.get(0).receiver != currentTransaction.receiver) {
					System.out.println("Transaction receiver mismatch.");
					return false;
				}
				if (currentTransaction.outputs.get(1).receiver != currentTransaction.sender) {
					System.out.println("Transaction receiver mismatch.");
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
}
