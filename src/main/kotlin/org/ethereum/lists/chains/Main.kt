package org.ethereum.lists.chains

import java.io.File


fun main() {

    File("_data/chains").listFiles().forEach {
        checkChain(it)
    }

}
