/**
 * This removed `network` param from all the chain files
 * Since this is the only tool here, it is here in index.js
 */

const fs = require('fs');
const chainFiles = fs.readdirSync('../_data/chains/');

for (const chainFile of chainFiles) {
    const fileLocation = `../_data/chains/${chainFile}`
    const fileData = fs.readFileSync(fileLocation, 'utf8')
    const fileDataJson = JSON.parse(fileData)

    if (fileDataJson.network) {
        delete fileDataJson.network
        fs.writeFileSync(fileLocation, JSON.stringify(fileDataJson, null, 2))
    }
}

// Note: 
// Run `npx prettier --write --ignore-unknown _data`from Project Directory