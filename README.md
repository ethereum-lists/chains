# EVM-based Chains

The source data is in _data/chains. Each chain has its own file with the filename being the [CAIP-2](https://github.com/ChainAgnostic/CAIPs/blob/master/CAIPs/caip-2.md) representation as name and `.json` ans extension.

## Example

```json
{
  "name": "Ethereum Mainnet",
  "chain": "ETH",
  "network": "mainnet",
  "rpc": [
    "https://mainnet.infura.io/v3/${INFURA_API_KEY}",
    "https://api.mycryptoapi.com/eth"
  ],
  "faucets": [],
  "nativeCurrency": {
    "name": "Ether",
    "symbol": "ETH",
    "decimals": 18
  },
  "infoURL": "https://ethereum.org",
  "shortName": "eth",
  "chainId": 1,
  "networkId": 1,
  "icon": "ethereum",
  "explorers": [{
    "name": "etherscan",
    "url": "https://etherscan.io",
    "icon": "etherscan",
    "standard": "EIP3091"
  }]
}
```

when an icon is used in either the network or a explorer there must be a json in _data/icons with the name used (e.g. in the above example there must be a `ethereum.json` and a `etherscan.json` in there) - the icon jsons look like this:

```json

[
    {
      "url": "ipfs://QmdwQDr6vmBtXmK2TmknkEuZNoaDqTasFdZdu3DRw8b2wt", 
      "width": 1000,
      "height": 1628,
      "format": "png"
    }
]

```

where:
 * the URL must be a IPFS url that is publicly resolveable
 * width and height are optional - but when one is there then the other must be there also
 * format is either "png", "jpg" or "svg"

If the chain is an L2 or a shard of another chain you can link it to the parent chain like this:


```json
{
  ...
  "parent": {
   "type" : "L2",
   "chain": "eip155-1",
   "bridges": [ {"url":"https://bridge.arbitrum.io"} ]
  }
}
```

where you need to specify type 2 and the reference to an existing parent. The field about bridges is optional.

## Aggregation  

There are also aggregated json files with all chains automatically assembled:
 * https://chainid.network/chains.json
 * https://chainid.network/chains_mini.json (miniaturized - fewer fields for smaller filesize)

## Collision management

 If different chains have the same chainID we list the one with the oldest genesis.

## Usages

 * [chainlist.org](https://chainlist.org) or [networklist-org.vercel.app](https://networklist-org.vercel.app) as a staging version with a more up-to-date list
 * [chainid.network](https://chainid.network)
 * [WallETH](https://walleth.org)
 * [TREZOR](https://trezor.io)
 * [networks.vercel.app](https://networks.vercel.app)
 * [eth-chains](https://github.com/taylorjdawson/eth-chains)
 * Your project - contact us to add it here!
