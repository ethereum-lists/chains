const fs = require('fs')
const path = require('path')
const axios = require('axios')
const BigNumber = require('bignumber.js')
const sortBy = require('lodash.sortby')

require('dotenv').config()

const ROOT_DIRECTORY = path.join(__dirname, '../../')

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

function padRight (n, width, z) {
  z = z || '0'
  n = n + ''
  return n.length >= width ? n : n + new Array(width - n.length + 1).join(z)
}

function paddedLog (...args) {
  let output = ''
  args.forEach(arg => {
    output += padRight(`${arg}`, 32, ' ')
  })
  console.log(output)
}

function tableLog (chains) {
  chains = sortBy(chains, ['chainId'])
  console.log('\n')
  paddedLog('Name', 'Chain', 'ChainId', 'NetworkId')
  console.log('\n')
  chains.map(json =>
    paddedLog(json.name, json.chain, json.chainId, json.networkId)
  )
  console.log('\n')
}

function stat (filePath) {
  return new Promise((resolve, reject) => {
    fs.stat(filePath, function (error, stat) {
      if (error) {
        return reject(error)
      }
      resolve(stat)
    })
  })
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

module.exports = {
  ROOT_DIRECTORY,
  CHAINS_DIRECTORY,
  NET_VERSION_REQ,
  CHAIN_ID_REQ,
  padRight,
  paddedLog,
  tableLog,
  stat,
  formatRpcUrl,
  writeJson,
  rpcRequest,
  toNumber,
  getNetworkId,
  getChainId,
  queryMulti,
  verifyJson
}
