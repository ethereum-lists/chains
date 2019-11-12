package org.ethereum.lists.chains

import java.io.File


fun main(args: Array<String>) {

    File("_data/chains").listFiles()?.forEach {
        checkChain(it, args.contains("rpcConnect"))
    }

}
