const fs = require("fs")
const Ajv = require("ajv")
const ajv = new Ajv()
const schema = require("./schema/chainSchema.json")
const { exit } = require("process")
const path = require('path')
const http = require('http')

const resolve = (_path) => path.resolve(__dirname, _path)
const chainFiles = fs.readdirSync(resolve("../_data/chains/"))

// https://chainagnostic.org/CAIPs/caip-2
const parseChainId = (chainId) =>
  /^(?<namespace>[-a-z0-9]{3,8})-(?<reference>[-a-zA-Z0-9]{1,32})$/u.exec(
    chainId
  )

const filesWithErrors = []
for (const chainFile of chainFiles) {
  const fileLocation = resolve(`../_data/chains/${chainFile}`)
  const fileData = fs.readFileSync(fileLocation, "utf8")
  const fileDataJson = JSON.parse(fileData)
  const fileName = chainFile.split(".")[0]
  const parsedChainId = parseChainId(fileName)?.groups
  const chainIdFromFileName = parsedChainId?.reference
  if (chainIdFromFileName != fileDataJson.chainId) {
    throw new Error(`File Name does not match with ChainID in ${chainFile}`)
  }
  const valid = ajv.validate(schema, fileDataJson)
  if (!valid) {
    console.error(ajv.errors)
    filesWithErrors.push(chainFile)
  }

  // Custom validation for rpc URLs
  if (fileDataJson.rpc) {
    for (const url of fileDataJson.rpc) {
      try {
        const request = http.get(url, (response) => {
          if (response.statusCode !== 200) {
            throw new Error(`RPC URL ${url} is not reachable`)
          }
        })
        request.on('error', (err) => {
          throw new Error(`RPC URL ${url} is not reachable: ${err.message}`)
        })
      } catch (err) {
        console.error(err.message)
        filesWithErrors.push(chainFile)
      }
    }
  }

  // Custom validation for nativeCurrency fields
  if (fileDataJson.nativeCurrency) {
    const { name, symbol, decimals } = fileDataJson.nativeCurrency
    if (!name || !symbol || typeof decimals !== 'number') {
      console.error(`Invalid nativeCurrency fields in ${chainFile}`)
      filesWithErrors.push(chainFile)
    }
  }
}

if (filesWithErrors.length > 0) {
  filesWithErrors.forEach(file => {
    console.error(`Invalid JSON Schema in ${file}`)
  })
  exit(-1);
}
else {
  console.info("Schema check completed successfully");
  exit(0);
}
