import android.os.Build
import androidx.annotation.RequiresApi
import java.security.*
import java.util.*


data class Block(
    val index: Int,
    val previousHash: String,
    val timestamp: Long,
    val transactions: List<Transaction>,
    val hash: String
)
data class Transaction(
    val sender: String,
    val recipient: String,
    val amount: Double
)

data class WalletTO52(
    val username: String,
    var address: String,
    val publicKey: PublicKey,
    val privateKey: PrivateKey,
    var balance: Double
)


@RequiresApi(Build.VERSION_CODES.O)
class BlockchainTO52 {
    private val blockchain = mutableListOf<Block>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.genKeyPair()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addBlock(transactions: List<Transaction>, previousHash: String) {
        val index = blockchain.size
        val timestamp = System.currentTimeMillis()
        val hash = calculateHash(index, previousHash, timestamp, transactions)
        val newBlock = Block(index, previousHash, timestamp, transactions, hash)
        blockchain.add(newBlock)

        println("Block ajouté: $hash")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateHash(
        index: Int,
        previousHash: String,
        timestamp: Long,
        transactions: List<Transaction>
    ): String {
        val transactionString = transactions.joinToString { "${it.sender}:${it.recipient}:${it.amount}" }
        val input = "$index$previousHash$timestamp$transactionString"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    fun createTransaction(sender: String, recipient: String, amount: Double, senderWallet: WalletTO52,recipientWallet:WalletTO52) {

        if (senderWallet.balance >= amount) {
            val newTransaction = Transaction(sender, recipient, amount)
            val lastBlock = blockchain.last()
            val updatedTransactions = lastBlock.transactions.toMutableList()
            updatedTransactions.add(newTransaction)

            addBlock(updatedTransactions, lastBlock.hash)
            println("Transaction de $amount TO52COIN de $senderWallet à $recipientWallet")
            senderWallet.balance -= amount
            recipientWallet.balance += amount
        } else {
            println("Transaction échouée")
        }
    }

    fun printBlockchain() {
        for (block in blockchain) {
            println("Index: ${block.index}")
            println("Hash Précédent: ${block.previousHash}")
            println("Timestamp: ${block.timestamp}")
            println("Transactions: ${block.transactions}")
            println("Hash: ${block.hash}")
            println("--------------------------------------")
        }
    }
}
