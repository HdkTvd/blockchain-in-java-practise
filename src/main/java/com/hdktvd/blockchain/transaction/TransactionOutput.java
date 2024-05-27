package com.hdktvd.blockchain.transaction;

import java.security.PublicKey;

import com.hdktvd.blockchain.StringUtility.StringUtil;

public class TransactionOutput {
	public String id;
	public PublicKey receiver;
	public String parentTransactionId;
	public float value;
	
	public TransactionOutput(float value, String parentTxnId, PublicKey receiver) {
		this.value = value;
		this.parentTransactionId = parentTxnId;
		this.receiver = receiver;
		
		this.id = StringUtil.applySha256(StringUtil.keyToString(receiver)+Float.toString(value)+parentTxnId);
	}
	
	public boolean isMine(PublicKey pubKey) {
		return (pubKey == receiver);
	}
}
