package org.ethereum.lists.chains

import com.squareup.moshi.JsonEncodingException
import org.ethereum.lists.chains.model.*
import org.junit.Before
import org.junit.Test
import java.io.File

class TheChainChecker {

    @Before
    fun setup() {
        parsedNames.clear()
        parsedShortNames.clear()
    }

    @Test
    fun shouldPassForValidChain() {
        val file = getFile("valid/eip155-1.json")

        checkChain(file, false)
    }

    @Test
    fun shouldPassForValidChainWithExplorers() {
        val file = getFile("valid/withexplorer/eip155-1.json")

        checkChain(file, false)
    }

    @Test
    fun shouldPassForValidChainWithExplorersNoStandard() {
        val file = getFile("valid/withexplorer/eip155-2.json")

        checkChain(file, false)
    }


    @Test
    fun shouldPassForValidChainWithParent() {
        val file = getFile("valid/withparent/eip155-2.json")

        checkChain(file, false)
    }

    @Test
    fun shouldPassForValidChainWithParentBridge() {
        val file = getFile("valid/withparentbridge/eip155-2.json")

        checkChain(file, false)
    }

    @Test(expected = BridgeNoObject::class)
    fun shouldFailForParentBridgeElementNoObject() {
        val file = getFile("invalid/withparentextrabridgeelementnoobject/eip155-2.json")

        checkChain(file, false)
    }

    @Test(expected = ParentMustBeObject::class)
    fun shouldFailForParentNoObject() {
        val file = getFile("invalid/withparentnobject/eip155-2.json")

        checkChain(file, false)
    }

    @Test(expected = ParentHasInvalidType::class)
    fun shouldFailForParentWithInvalidType() {
        val file = getFile("invalid/withparentinvalidtype/eip155-2.json")

        checkChain(file, false)
    }

    @Test(expected = ParentHasExtraFields::class)
    fun shouldFailForParentWithExtraParentField() {
        val file = getFile("invalid/withparentextrafield/eip155-2.json")

        checkChain(file, false)
    }

    @Test(expected = ParentHasExtraFields::class)
    fun shouldFailForParentWithExtraField() {
        val file = getFile("invalid/withparentextrafield/eip155-2.json")

        checkChain(file, false)
    }

    @Test(expected = BridgeOnlyURL::class)
    fun shouldFailForParentWithExtraBridgesField() {
        val file = getFile("invalid/withparentextrabridgesfield/eip155-2.json")

        checkChain(file, false)
    }

    @Test(expected = ParentBridgeNoArray::class)
    fun shouldFailForParentWithExtraBridgeNoArray() {
        val file = getFile("invalid/withparentextrabridgesnoarray/eip155-2.json")

        checkChain(file, false)
    }

    @Test(expected = ParentChainDoesNotExist::class)
    fun shouldFailIfParentChainDoesNotExist() {
        val file = getFile("invalid/withparentchaindoesnotexist/eip155-2.json")

        checkChain(file, false)
    }

    @Test(expected = FileNameMustMatchChainId::class)
    fun shouldFailForInvalidFilename() {
        val file = getFile("invalid/eip155-invalid_filename.json")

        checkChain(file, false)
    }

    @Test(expected = FileNameMustMatchChainId::class)
    fun shouldFailForChainNotMatchingFilename() {
        val file = getFile("invalid/eip155-3.json")

        checkChain(file, false)
    }

    @Test(expected = ShouldHaveNoExtraFields::class)
    fun shouldFailForExtraField() {
        val file = getFile("invalid/eip155-1.json")

        checkChain(file, false)
    }

    @Test(expected = ShouldHaveNoMissingFields::class)
    fun shouldFailForMissingField() {
        val file = getFile("invalid/eip155-4.json")

        checkChain(file, false)
    }


    @Test(expected = ENSRegistryAddressMustBeValid::class)
    fun shouldFailForInvalidENSAddress() {
        val file = getFile("invalid/eip155-99.json")

        checkChain(file, false)
    }

    @Test(expected = ENSMustHaveOnlyRegistry::class)
    fun shouldFailForExtraENSFields() {
        val file = getFile("invalid/eip155-100.json")

        checkChain(file, false)
    }

    @Test(expected = ENSMustHaveOnlyRegistry::class)
    fun shouldFailForNoRegistryField() {
        val file = getFile("invalid/eip155-101.json")

        checkChain(file, false)
    }

    @Test(expected = ENSMustBeObject::class)
    fun shouldFailForENSISNotObject() {
        val file = getFile("invalid/eip155-102.json")

        checkChain(file, false)
    }

    @Test(expected = ExtensionMustBeJSON::class)
    fun shouldFailForNonJSON() {
        val file = getFile("invalid/eip155-1.nojson")

        checkChain(file, false)
    }

    @Test(expected = JsonEncodingException::class)
    fun shouldFailForExtraComma() {
        val file = getFile("invalid/eip155-extracomma.json")

        checkChain(file, false)
    }

    @Test(expected = NameMustBeUnique::class)
    fun shouldFailOnNonUniqueName() {
        checkChain(getFile("valid/eip155-1.json"), false)
        checkChain(getFile("valid/eip155-1.json"), false)
    }

    @Test(expected = ShortNameMustBeUnique::class)
    fun shouldFailOnNonUniqueShortName() {
        checkChain(getFile("invalid/sameshortname/eip155-5.json"), false)
        checkChain(getFile("invalid/sameshortname/eip155-1.json"), false)
    }

    @Test(expected = ExplorersMustBeArray::class)
    fun shouldFailWhenExplorerIsNotArray() {
        checkChain(getFile("invalid/explorersnotarray/eip155-1.json"), false)
    }

    @Test(expected = ExplorerStandardMustBeEIP3091OrNone::class)
    fun shouldFailOnWrongExplorerStandard() {
        checkChain(getFile("invalid/wrongexplorerstandard/eip155-1.json"), false)
    }

    @Test(expected = ExplorerMustHaveName::class)
    fun shouldFailOnNoExplorerName() {
        checkChain(getFile("invalid/explorernoname/eip155-1.json"), false)
    }

    @Test(expected = ExplorerInvalidUrl::class)
    fun shouldFailOnInvalidUrl() {
        checkChain(getFile("invalid/explorerinvalidurl/eip155-1.json"), false)
    }

    @Test(expected = ExplorerInvalidUrl::class)
    fun shouldFailOnMissingURL() {
        checkChain(getFile("invalid/explorermissingurl/eip155-1.json"), false)
    }

    @Test
    fun canParse2chains() {
        checkChain(getFile("valid/eip155-1.json"), false)
        checkChain(getFile("valid/eip155-5.json"), false)
    }

    private fun getFile(s: String) = File(javaClass.classLoader.getResource("test_chains/$s").file)

}