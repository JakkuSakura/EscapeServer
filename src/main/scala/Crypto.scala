import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}
import java.security._
import javax.crypto.Cipher

object Crypto {
    //java default RSA="RSA/ECB/PKCS1Padding"
    private val RSA = "RSA"

    val KEYPAIR: KeyPair = createKeys(1024)
    val VERIFICATION_TOKEN: String = "1234"

    /**
     * Create rsa keys
     * @param length: length of keys
     * @return KeyStore
     */
    @throws[NoSuchAlgorithmException]
    def createKeys(length: Int): KeyPair = {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance(RSA)
            keyPairGenerator.initialize(length)
            val pair = keyPairGenerator.generateKeyPair
            println("Successfully generated key pairs")
            pair
        } catch {
            case ex: Exception =>
                ex.printStackTrace()
                throw ex
        }
    }


    @throws[Exception]
    def encrypt(content: Array[Byte], publicKey: Key): Array[Byte] = {
        val cipher = Cipher.getInstance(RSA)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        cipher.doFinal(content)
    }

    @throws[Exception]
    def decrypt(content: Array[Byte], privateKey: Key): Array[Byte] = {
        val cipher = Cipher.getInstance(RSA)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        cipher.doFinal(content)
    }

}
