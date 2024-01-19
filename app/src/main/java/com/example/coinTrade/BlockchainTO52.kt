import android.os.Build
import androidx.annotation.RequiresApi
import java.security.*
import java.util.*

data class Block(
    val index: Int,
    val previousHash: String,
    val timestamp: Long,
    val transactions: List<TransactionTO52>,
    val hash: String,
    val signature: ByteArray?
)

data class TransactionTO52(
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
    val blockchain = mutableListOf<Block>()
    val wallets = mutableListOf<WalletTO52>()

    init {
        createGenesisBlock()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGenesisBlock() {
        val transactions = listOf(
            TransactionTO52("Genesis", "Recipient1", 100.0),
            TransactionTO52("Genesis", "Recipient2", 50.0)
        )

        val index = 0
        val previousHash = "0"
        val timestamp = System.currentTimeMillis()
        val hash = calculateHash(index, previousHash, timestamp, transactions)

        val genesisBlock = Block(index, previousHash, timestamp, transactions, hash, null)
        blockchain.add(genesisBlock)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.genKeyPair()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun add_wallet(username: String) {
        val keyPair = generateKeyPair()
        val wallet = WalletTO52(username, UUID.randomUUID().toString(), keyPair.public, keyPair.private, 60.0)
        wallets.add(wallet)
        // Add the wallet to the blockchain
        println("Wallet ajouté: ${wallet.address}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sign_with_key(data: String, privateKey: PrivateKey): ByteArray {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(data.toByteArray())
        return signature.sign()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addBlock(transactions: List<TransactionTO52>, previousHash: String, signerWallet: WalletTO52,recipientWallet: WalletTO52) {
        val index = blockchain.size
        val timestamp = System.currentTimeMillis()
        val hash = calculateHash(index, previousHash, timestamp, transactions)

        // Sign the hash with the signer's private key
        val signature = sign_with_key(hash, signerWallet.privateKey)

        // Include the signature in the new block
        val newBlock = Block(index, previousHash, timestamp, transactions, hash, signature)
        if (verifySignature(newBlock, signerWallet)) {
            blockchain.add(newBlock)
            println("Block ajouté: $hash")
        } else {
            println("La vérification de la signature a échoué. Le bloc n'a pas été ajouté.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateHash(
        index: Int,
        previousHash: String,
        timestamp: Long,
        transactions: List<TransactionTO52>
    ): String {
        val transactionString = transactions.joinToString { "${it.sender}:${it.recipient}:${it.amount}" }
        val input = "$index$previousHash$timestamp$transactionString"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun verifySignature(block: Block, senderWallet: WalletTO52): Boolean {
        val signature = block.signature
        if (signature != null) {
            val data = block.hash.toByteArray()
            val publicKey = senderWallet.publicKey
            val signatureToVerify = Signature.getInstance("SHA256withRSA")
            signatureToVerify.initVerify(publicKey)
            signatureToVerify.update(data)
            return signatureToVerify.verify(signature)
        }
        return false
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

    fun doesUsernameExistInBlockhain(username: String): Boolean {
        return wallets.any { it.username == username }
    }
}
