const fs = require('fs')
const path = require('path')
const {
  CHAINS_DIRECTORY,
  startSpinner,
  stopSpinner,
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

  startSpinner(`Verifying ${CHAINS_DIRECTORY} files`)

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

  stopSpinner()

  tableLog(result)

  console.log(
    `Successfully verified and wrote ${files.length} file${
      files.length > 1 ? 's' : ''
    }`
  )
})
