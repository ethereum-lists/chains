
import org.ethereum.lists.chains.FileNameMustMatchChainId
import org.ethereum.lists.chains.ShouldHaveNoExtraFields
import org.ethereum.lists.chains.ShouldHaveNoMissingFields
import org.ethereum.lists.chains.checkChain
import org.junit.Test
import java.io.File

class TheChainChecker {

    @Test
    fun shouldPassForValidChain() {
        val file = getFile("valid/1.json")

        checkChain(file)
    }

    @Test(expected = FileNameMustMatchChainId::class)
    fun shouldFailForInvalidFilename() {
        val file = getFile("invalid/invalid_filename.json")

        checkChain(file)
    }

    @Test(expected = FileNameMustMatchChainId::class)
    fun shouldFailForChainNotMatchingFilename() {
        val file = getFile("invalid/3.json")

        checkChain(file)
    }

    @Test(expected = ShouldHaveNoExtraFields::class)
    fun shouldFailForExtraField() {
        val file = getFile("invalid/1.json")

        checkChain(file)
    }

    @Test(expected = ShouldHaveNoMissingFields::class)
    fun shouldFailForMissingField() {
        val file = getFile("invalid/4.json")

        checkChain(file)
    }


    private fun getFile(s: String) = File(javaClass.classLoader.getResource("test_chains/$s").file)

}