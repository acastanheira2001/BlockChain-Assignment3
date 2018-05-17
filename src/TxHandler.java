
import java.util.*;


public class TxHandler
{

	/*11/5 inseri o private e nao submeti o
	codigo. se der erro basta retirar.*/
	private UTXOPool uPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool)
	{
        // IMPLEMENT THIS
		this.uPool = new UTXOPool(utxoPool);
		//this.uPool2 = new UTXOPool();
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx)
	{
        // IMPLEMENT THIS

		int i = 0;
		double sumInputs = 0.0;
		double sumOutputs = 0.0;
		int inputIndex = 0;
		HashMap<UTXO, Boolean> usedUTXO = new HashMap<UTXO, Boolean>();

		for (i = 0; i < tx.numInputs(); i++)
		{
			Transaction.Input input = tx.getInput(i);
            if (input == null) { return false; }
			
			UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
			
			Transaction.Output previousTxOutput = this.uPool.getTxOutput(utxo);
			
			if ( previousTxOutput != null)
				sumInputs = sumInputs + previousTxOutput.value;

			if(usedUTXO.containsKey(utxo))
				return false;
			
			usedUTXO.put(utxo, true);
			//(3)
			/*if (!uPool2.contains(utxo))
				uPool2.addUTXO(utxo, previousTxOutput);
			else
				return false; */

			//(1)
			if (!uPool.contains(utxo))
				return false;

			//(2)
			if (!Crypto.verifySignature(previousTxOutput.address, tx.getRawDataToSign(i), input.signature))
				return false;


		}
		//(5)
		for( i =0; i < tx.numOutputs();i++)
		{
			
			if 	(tx.getOutput(i) == null)
				return false;
			
			if  (tx.getOutput(i).value < 0)
				return false;
				
				sumOutputs = sumOutputs + tx.getOutput(i).value;
		}
		
		if (sumOutputs > sumInputs)
			return false;

		return true;
	}



    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs)
	{
        // IMPLEMENT THIS

		//int i = 0;
		//int j = 0;
		//Transaction[] validTxs = null;
		
		if(possibleTxs == null)
			return new Transaction[0];
			
			
		ArrayList<Transaction> validTxs = new ArrayList<>();

        for (Transaction tx : possibleTxs) {
            if (!isValidTx(tx))
				continue;
			else
				validTxs.add(tx);
			
			for (Transaction.Input input : tx.getInputs()) {
                UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
                this.uPool.removeUTXO(utxo);
            }
            byte[] txHash = tx.getHash();
            int index = 0;
            for (Transaction.Output output : tx.getOutputs()) {
                UTXO utxo = new UTXO(txHash, index);
                index += 1;
                this.uPool.addUTXO(utxo, output);
            }
        }

        return validTxs.toArray(new Transaction[validTxs.size()]);
    }		

	public UTXOPool  getUTXOPool()
	{
		return uPool;
	}
}
