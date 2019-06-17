const fs = require('fs')
const path = require('path')
const axios = require('axios')
const BigNumber = require('bignumber.js')

require('dotenv').config()

const ROOT_DIRECTORY = path.join(__dirname, '../')

const CHAINS_DIRECTORY = path.join(ROOT_DIRECTORY, './_data/chains')

const NET_VERSION_REQ = {
  id: 1,
  jsonrpc: '2.0',
  method: 'net_version',
  params: []
}

const CHAIN_ID_REQ = {
  id: 1,
  jsonrpc: '2.0',
  method: 'eth_chainId',
  params: []
}

function formatRpcUrl (rpcUrl) {
  return rpcUrl.replace(
    '${INFURA_API_KEY}', // eslint-disable-line
    process.env.INFURA_PROJECT_ID
  )
}

async function writeJson (filePath, json) {
  // console.log('Overwriting', filePath)
  return new Promise((resolve, reject) => {
    const data = JSON.stringify(json, null, 2)
    fs.writeFile(filePath, data, (err, res) => {
      if (err) {
        reject(err)
      }
      resolve(res)
    })
  })
}

async function rpcRequest (rpcUrl, body) {
  const response = await axios.post(rpcUrl, body, {
    timeout: 20000, // 20 secs
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json'
    }
  })
  if (!response && !response.data) {
    throw new Error('No Response Body')
  }
  if (response.data.error && response.data.error.message) {
    throw new Error(response.data.error.message)
  }
  return response.data.result
}

function toNumber (value) {
  const BN = new BigNumber(value)
  let result = null
  if (!BN.isNaN()) {
    result = BN.toNumber()
  }
  return result
}

async function getNetworkId (rpcUrl) {
  try {
    rpcUrl = formatRpcUrl(rpcUrl)
    const result = await rpcRequest(rpcUrl, NET_VERSION_REQ)
    const networkId = toNumber(result)
    return networkId
  } catch (error) {
    return null
  }
}

async function getChainId (rpcUrl) {
  try {
    rpcUrl = formatRpcUrl(rpcUrl)
    const result = await rpcRequest(rpcUrl, CHAIN_ID_REQ)
    const chainId = toNumber(result)
    return chainId
  } catch (error) {
    return null
  }
}

async function queryMulti (urls, apiCall) {
  let result = null
  let results = await Promise.all(
    urls.map(async url => {
      try {
        return await apiCall(url)
      } catch (error) {
        return null
      }
    })
  )
  if (results && results.length) {
    results = results.filter(x => !!x)
    result = results[0] || null
  }
  return result
}

async function verifyJson (json) {
  if (json.rpc && json.rpc.length) {
    const chainId = await queryMulti(json.rpc, getChainId)
    if (chainId) {
      json.chainId = chainId
    }
    const networkId = await queryMulti(json.rpc, getNetworkId)
    if (networkId) {
      json.networkId = networkId
    }
  }
  return json
}

fs.readdir(CHAINS_DIRECTORY, function (err, files) {
  if (err) {
    console.error('Could not list the directory.', err)
    process.exit(1)
  }

  files.forEach(function (file, index) {
    const filePath = path.join(CHAINS_DIRECTORY, file)

    fs.stat(filePath, async function (error, stat) {
      if (error) {
        console.error('Error stating file.', error)
        return
      }

      const ext = path.extname(file)
      if (stat.isFile() && ext === '.json') {
        let json = require(filePath)
        const fileName = file.replace(ext, '')
        if (toNumber(fileName)) {
          json.chainId = toNumber(fileName)
        }
        json = await verifyJson(json)
        console.log(
          `${json.chain.toUpperCase()} chainId=${json.chainId} networId=${
            json.networkId
          }`
        )
        await writeJson(filePath, json)
      }
    })
  })
})
