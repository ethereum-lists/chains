# EVM-based Chains

The source data is in _data/chains. Each chain has its own file with the filename being the [CAIP-2](https://github.com/ChainAgnostic/CAIPs/blob/master/CAIPs/caip-2.md) representation as name and `.json` ans extension.

## Example

```json
{
  "name": "Ethereum Mainnet",
  "chain": "ETH",
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
  "features": [{ "name": "EIP155" }, { "name": "EIP1559" }],
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
 * width and height are positive integers
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

You can add a `status` field e.g. to `deprecate` a chain (a chain should never be deleted as this would open the door to replay attacks)
Other options for `status` are `active` (default) or `incubating`

## Aggregation

There are also aggregated json files with all chains automatically assembled:
 * https://chainid.network/chains.json
 * https://chainid.network/chains_mini.json (miniaturized - fewer fields for smaller filesize)

## Constraints

 * the shortName and name MUST be unique - see e.g. EIP-3770 on why
 * if referencing a parent chain - the chain MUST exist in the repo
 * if using a IPFS CID for the icon - the CID MUST be retrievable via `ipfs get` - not only through some gateway (means please do not use pinata for now)
 * for more constraints you can look into the CI
 
## Collision management

 If different chains have the same chainID we list the one with the oldest genesis.

## Usages
### Wallets
 * [WallETH](https://walleth.org)
 * [TREZOR](https://trezor.io)
 * [Minerva Wallet](https://minerva.digital)

### Explorers
 * [Otterscan](https://otterscan.io)

### EIPs
 * EIP-155
 * EIP-3014
 * EIP-3770
 * EIP-4527

### Listing sites
 * [chainlist.wtf](https://chainlist.wtf)
 * [chainlist.org](https://chainlist.org) or [networklist-org.vercel.app](https://networklist-org.vercel.app) as a staging version with a more up-to-date list
 * [chainid.network](https://chainid.network)
 * [networks.vercel.app](https://networks.vercel.app)
 * [eth-chains](https://github.com/taylorjdawson/eth-chains)
 * [EVM-BOX](https://github.com/izayl/evm-box)
 * [chaindirectory.xyz](https://www.chaindirectory.xyz)
 * [chain-list.org](https://chain-list.org)
 * [DefiLlama's chainlist](https://chainlist.defillama.com/)
 * [chainlist.network](https://chainlist.network/)
 * [evmchainlist.org](https://evmchainlist.org)
 * [evmchainlist.com](https://evmchainlist.com)
 * [thechainlist.io](https://thechainlist.io)
 * [chainlist.info](https://chainlist.info)
 * [chainmap.io](https://chainmap.io) 
 * [chainlist.in](https://www.chainlist.in)
 * [chainz.me](https://chainz.me)
 * [Chainlink docs](https://docs.chain.link/)
 * [Wagmi compatible chain configurations](https://spenhouet.com/chains)

### Other
 * [FaucETH](https://github.com/komputing/FaucETH)
 * [Sourcify playground](https://playground.sourcify.dev)


 * Your project - contact us to add it here!
