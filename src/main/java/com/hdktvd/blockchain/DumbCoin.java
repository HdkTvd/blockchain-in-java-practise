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
		Security.addProvider(new BouncyCastleProvider());
		
		wallet1 = new Wallet();
		wallet2 = new Wallet();
		
		System.out.println("wallet 1 public key - " + StringUtil.keyToString(wallet1.publicKey));
		System.out.println("wallet 1 private key - " + StringUtil.keyToString(wallet1.privateKey));		
		
		System.out.println("wallet 2 public key - " + StringUtil.keyToString(wallet2.publicKey));
		System.out.println("wallet 2 private key - " + StringUtil.keyToString(wallet2.privateKey));
		
//		Perform a transaction from wallet1 to wallet2
		Transaction t1 = new Transaction(wallet1.publicKey, wallet2.publicKey, 5, null);
		t1.createSignature(wallet1.privateKey);
		
		System.out.println("Verify signature - " + t1.verifySignature());
		
//		Processing the transactions
		
		
}
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
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
		}
		return true;
	}
}
