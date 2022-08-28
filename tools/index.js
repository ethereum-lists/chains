/**
 * This removed `network` param from all the chain files
 * Since this is the only tool here, it is here in index.js
 */
 const fs = require('fs');
 const { exec } = require('child_process');
 const Ajv = require("ajv")
 const ajv = new Ajv()
 const fs = require('fs');
 const schema = require('./chainSchema.json')
 const chainFiles = fs.readdirSync('../_data/chains/');
 
 for(const chainFile of chainFiles){
     const fileLocation = `../_data/chains/${chainFile}`
     const fileData = fs.readFileSync(fileLocation,'utf8')
     const fileDataJson = JSON.parse(fileData)
 
     if(fileDataJson.network){
         delete fileDataJson.network
         fs.writeFileSync(fileLocation, JSON.stringify(fileDataJson, null, 2))
     }
 }
 
 // TODO: Move to Different FIle
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
 
 
 
 // TODO: Run `npx prettier --write --ignore-unknown _data`from Project Directory