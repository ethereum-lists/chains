import * as IPFS from 'ipfs-core'  
import * as ImageParser from "image-parser"
import * as fs from 'fs'
import * as path from 'path'
import { exit } from 'process'

import * as url from 'url';
const __filename = url.fileURLToPath(import.meta.url);
const __dirname = url.fileURLToPath(new URL('.', import.meta.url))

const iconFiles = fs.readdirSync(path.join(__dirname, '..', '_data', 'icons'))

export default async function main() {
    const ipfs = await IPFS.create()

    const filesWithErrors = []
    for (const iconFile of iconFiles) {
        const fileLocation = path.join(__dirname, '..', '_data', 'icons', iconFile)
        const fileData = fs.readFileSync(fileLocation, "utf8")
        const fileDataJson = JSON.parse(fileData)

        try {
            const imageData = await ipfs.cat(fileData['url'])
            const image = ImageParser.parse(new Buffer(imageData))
            if (fileData['width'] != image.width() || fileData['height'] == image.height()) {
                filesWithErrors.push({file: iconFile, error: "Height or width mismatch"})
            }
        }
        catch {
            filesWithErrors.push({file: iconFile, error: "Does not exist in IPFS"})
        }
        console.info(`Validated ${iconFile}`)
    }

    if (filesWithErrors.length != 0) {
        for (const fileError in filesWithErrors) {
            const singleError = filesWithErrors[fileError]
            console.error(`${singleError['file']}: ${singleError['error']}`)
        }
        exit(-1)
    }
}

await main()