import org.ethereum.lists.chains.*
import org.junit.Test
import java.io.File

class TheChainChecker {

    @Test
    fun shouldPassForValidChain() {
        val file = getFile("valid/1.json")

        checkChain(file, false)
    }

    @Test(expected = FileNameMustMatchChainId::class)
    fun shouldFailForInvalidFilename() {
        val file = getFile("invalid/invalid_filename.json")

        checkChain(file, false)
    }

    @Test(expected = FileNameMustMatchChainId::class)
    fun shouldFailForChainNotMatchingFilename() {
        val file = getFile("invalid/3.json")

        checkChain(file, false)
    }

    @Test(expected = ShouldHaveNoExtraFields::class)
    fun shouldFailForExtraField() {
        val file = getFile("invalid/1.json")

        checkChain(file, false)
    }

    @Test(expected = ShouldHaveNoMissingFields::class)
    fun shouldFailForMissingField() {
        val file = getFile("invalid/4.json")

        checkChain(file, false)
    }


    @Test(expected = ENSRegistryAddressMustBeValid::class)
    fun shouldFailForInvalidENSAddress() {
        val file = getFile("invalid/99.json")

        checkChain(file, false)
    }

    @Test(expected = ENSMustHaveOnlyRegistry::class)
    fun shouldFailForExtraENSFields() {
        val file = getFile("invalid/100.json")

        checkChain(file, false)
    }

    @Test(expected = ENSMustHaveOnlyRegistry::class)
    fun shouldFailForNoRegistryField() {
        val file = getFile("invalid/101.json")

        checkChain(file, false)
    }

    @Test(expected = ENSMustBeObject::class)
    fun shouldFailForENSISNotObject() {
        val file = getFile("invalid/102.json")

        checkChain(file, false)
    }

    @Test(expected = ExtensionMustBeJSON::class)
    fun shouldFailFoNonJSON() {
        val file = getFile("invalid/1.nojson")

        checkChain(file, false)
    }


    private fun getFile(s: String) = File(javaClass.classLoader.getResource("test_chains/$s").file)

}