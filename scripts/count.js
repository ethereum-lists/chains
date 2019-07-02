const fs = require('fs')
const path = require('path')
const {
  CHAINS_DIRECTORY,
  startSpinner,
  stopSpinner,
  stat,
  tableLog,
  toNumber
} = require('./shared')

fs.readdir(CHAINS_DIRECTORY, async function (err, files) {
  if (err) {
    console.error('Could not list the directory.', err)
    process.exit(1)
  }

  let matches = []

  startSpinner(`Counting ${CHAINS_DIRECTORY}`)

  await Promise.all(
    files.map(async function (file, index) {
      const filePath = path.join(CHAINS_DIRECTORY, file)
      const fileStat = await stat(filePath)
      const ext = path.extname(file)
      if (fileStat.isFile() && ext === '.json') {
        let json = require(filePath)
        if (toNumber(json.chainId) !== toNumber(json.networkId)) {
          matches.push(json)
        }
      }
      return fileStat
    })
  )

  stopSpinner()

  console.log(
    `There are ${files.length} known EVM chains from which ${
      matches.length
    } have unmatched ids, here are the list of those chains: `
  )
  tableLog(matches)
})
