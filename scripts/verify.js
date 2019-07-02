const fs = require('fs')
const path = require('path')
const {
  CHAINS_DIRECTORY,
  tableLog,
  stat,
  writeJson,
  toNumber,
  verifyJson
} = require('./shared')

fs.readdir(CHAINS_DIRECTORY, async function (err, files) {
  if (err) {
    console.error('Could not list the directory.', err)
    process.exit(1)
  }

  console.log('Verifying:', CHAINS_DIRECTORY)

  let result = []

  await Promise.all(
    files.map(async function (file, index) {
      const filePath = path.join(CHAINS_DIRECTORY, file)
      const fileStat = await stat(filePath)
      const ext = path.extname(file)
      if (fileStat.isFile() && ext === '.json') {
        let json = require(filePath)
        const fileName = file.replace(ext, '')
        if (toNumber(fileName)) {
          json.chainId = toNumber(fileName)
        }
        json = await verifyJson(json)
        await writeJson(filePath, json)
        result.push(json)
      }
      return fileStat
    })
  )

  tableLog(result)

  console.log(
    `Successfully verified ${files.length} file${files.length > 1 ? 's' : ''}`
  )
})
