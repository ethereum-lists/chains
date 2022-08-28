const fs = require('fs');
const Ajv = require("ajv")
const ajv = new Ajv()
const schema = require('./schema/chainSchema.json')
const chainFiles = fs.readdirSync('../_data/chains/');

for(const chainFile of chainFiles){
    const fileLocation = `../_data/chains/${chainFile}`
    const fileData = fs.readFileSync(fileLocation,'utf8')
    const fileDataJson = JSON.parse(fileData)
    const valid = ajv.validate(schema, fileDataJson)
    const filesWithErrors = []
    if(!valid) {
        console.error(ajv.errors)
        filesWithErrors.push(chainFile)   
    }
    if(filesWithErrors.length > 0){
        throw new Error(`Invalid JSON Schema in ${filesWithErrors.length} files at ${filesWithErrors.join(",")}`)
    }
}