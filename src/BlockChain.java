// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

import java.util.*;

public class BlockChain
 {
    public static final int CUT_OFF_AGE = 10;

	private TxHandler txHandler;
	
	private UTXOPool utXoPool;
	
	private TransactionPool txPool;
	
	private int height;
	
	private Map<byte[], Block> blockChain;
	
	private HashMap<byte[], Integer> blockHeight;
    
	
    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
        // IMPLEMENT THIS
		
		utXoPool = new UTXOPool();
		
		txHandler = new TxHandler(utXoPool);

		txPool = new TransactionPool();
		
		height = 1;
		
		//blockHeight.put(genesisBlock.getHash(), height);
		
		blockChain = new HashMap<>();
		
		this.addBlock(genesisBlock);
		
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        // IMPLEMENT THIS
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
        // IMPLEMENT THIS
		return utXoPool;
    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        // IMPLEMENT THIS
		return txPool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
        // IMPLEMENT THIS
		
		ArrayList<Transaction> txs;
		
		int tempHeight = 0;
		
		//genesis block?
		if ((block.getPrevBlockHash() == null) && (height == 1))
			return false;
		
		//valid transactions?
		txs = block.getTransactions();
	
		Iterator <Transaction> setIterator = txs.iterator();
		
		while (setIterator.hasNext())
		{
			Transaction tx = setIterator.next();
			
			if (txHandler.isValidTx(tx))
			{
				this.addTransaction(tx);
			}
				else
				{return false;}
					
			
		}
		
		if(!blockHeight.containsKey(block.getPrevBlockHash()))
			return false;
		
		tempHeight = blockHeight.get(block.getPrevBlockHash());
		
		if ( (tempHeight + 1) <= (height - CUT_OFF_AGE))
			return false;
		
		blockHeight.put(block.getHash(), tempHeight +1);
		
		if ((tempHeight + 1) == height)
			height = height + 1;
		
		blockChain.put(block.getHash(), block);
		
		return true;
    }

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
        // IMPLEMENT THIS
		txPool.addTransaction(tx);
		
    }
}


