const fs = require('fs');
const Ajv = require("ajv")
const ajv = new Ajv()
const schema = require('./schema/chainSchema.json')
const chainFiles = fs.readdirSync('../_data/chains/');

const filesWithErrors = []
for(const chainFile of chainFiles){
    const fileLocation = `../_data/chains/${chainFile}`
    const fileData = fs.readFileSync(fileLocation,'utf8')
    const fileDataJson = JSON.parse(fileData)
    const chainIdFromFileName = chainFile.match(/eip155-(\d+)\.json/)[1]
    if(chainIdFromFileName != fileDataJson.chainId){
        throw new Error(`File Name does not match with ChainID in ${chainFile}`)
    }
    const valid = ajv.validate(schema, fileDataJson)
    if(!valid) {
        console.error(ajv.errors)
        filesWithErrors.push(chainFile)   
    }
}

if(filesWithErrors.length > 0){
    throw new Error(`Invalid JSON Schema in ${filesWithErrors.length} files at ${filesWithErrors.join(",")}`)
}