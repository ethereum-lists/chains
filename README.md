# Chains Repository

This repository contains data and configurations related to EVM-based chains. Each chain is represented using a JSON file conforming to the [CAIP-2](https://github.com/ChainAgnostic/CAIPs/blob/master/CAIPs/caip-2.md) standard.

## Project Structure

The data is stored in the `_data/chains` directory. Each chain's configuration is stored in a separate JSON file using its CAIP-2 representation as the filename.

### Example JSON Configuration

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

## Icon Requirements

Icons used in the configuration files must meet the following criteria:
- The `icon` field references a JSON file in the `_data/icons` directory.
- Example icon JSON:

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

### Constraints for Icons
- **URL**: Must be a publicly resolvable IPFS URL.
- **Dimensions**: Specify positive integers for width and height.
- **Format**: Must be `png`, `jpg`, or `svg`.

## Linking to Parent Chains

For chains that are L2s or shards, the `parent` field can be used to link to the parent chain.

```json
"parent": {
   "type" : "L2",
   "chain": "eip155-1",
   "bridges": [ {"url":"https://bridge.arbitrum.io"} ]
}
```

## Deprecation and Status

Chains should not be deleted to avoid replay attacks. Use the `status` field to indicate the chain's state:
- `active` (default)
- `deprecated`
- `incubating`

## Aggregated JSON Files

Aggregated data for all chains is available:
- [Full JSON](https://chainid.network/chains.json)
- [Mini JSON](https://chainid.network/chains_mini.json)

## Constraints

- **Unique Identifiers**: The `shortName` and `name` fields must be unique as per [EIP-3770](https://eips.ethereum.org/EIPS/eip-3770).
- **Parent Chains**: If referencing a parent chain, it must already exist in the repository.

## Contribution Guidelines

1. Fork the repository and create a new branch.
2. Add or update chain configurations following the JSON format.
3. Ensure that all required fields are included and valid.
4. Submit a pull request for review.

## License

This project is licensed under the MIT License.

## Contact

For any questions or support, please reach out via the repository's [Issues](https://github.com/nodoubtz/chains/issues) section.
