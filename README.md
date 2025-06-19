<master
# Chains

A modular, flexible, and extensible framework for building and managing blockchain-based applications.
=======
# EVM-based Chains

The source data is in _data/chains. Each chain has its own file with the filename being the [CAIP-2](https://github.com/ChainAgnostic/CAIPs/blob/master/CAIPs/caip-2.md) representation as name and `.json` as extension.

## Example:

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

When an icon is used in either the network or an explorer, there must be a JSON in _data/icons with the name used.
(e.g. in the above example there must be a `ethereum.json` and a `etherscan.json` in there) - The icon JSON files look like this:master

## Overview

Chains is designed to simplify the development and management of blockchain applications, providing a set of tools, components, and best practices for developers. The framework promotes modular code organization, security, and scalability for both new and existing blockchain projects.

## Features

- **Modular Architecture**: Compose, extend, or swap components with minimal effort.
- **Error Handling**: Robust error detection and reporting.
- **Security Oriented**: Built-in patterns for secure coding and vulnerability mitigation.
- **Duplicate Code Detection**: Tools to identify and resolve code duplication.
- **Management Tools**: Scripts and interfaces for efficient project and chain management.
- **Extensible**: Easily add new functionality or integrate with other systems.

## Getting Started

master
### Prerequisites
=======
where:
 * The URL MUST be publicly resolvable through IPFS
 * width and height MUST be positive integers
 * format is either "png", "jpg" or "svg"
 * size MUST be less than 250kb
master

- [Node.js](https://nodejs.org/) (version X.X.X or later)
- [npm](https://www.npmjs.com/) or [yarn](https://yarnpkg.com/)

### Installation

Clone the repository:
```bash
git clone https://github.com/nodoubtz/chains.git
cd chains
```

<master
Install dependencies:
```bash
npm install
# or
yarn install
```
=======
where you need to specify the type and the reference to an existing parent. The field about bridges is optional.
master

### Usage

Run the main application (adapt to your entry point, e.g., `index.js` or `app.js`):
```bash
npm start
# or
yarn start
```

## Project Structure

```
chains/
├── src/                # Core source code
├── scripts/            # Management and utility scripts
├── tests/              # Test suite
├── package.json        # Project metadata and dependencies
└── README.md           # Project documentation
```

master
## Security
=======
 * the shortName and name MUST be unique - see e.g. EIP-3770 on why
 * if referencing a parent chain - the chain MUST exist in the repo
 * if using an IPFS CID for the icon - the CID MUST be retrievable via `ipfs get` - not only through some gateway (means please do not use pinata for now)
 * for more constraints you can look into the CI
master

- All code follows secure coding practices.
- Vulnerable code is hidden or removed by design.
- Regular audits and updates are recommended.

master
## Contributing

Contributions are welcome! Please open an issue or submit a pull request. Make sure to follow the code of conduct and review the contributing guidelines.

## License
=======
 We cannot allow more than one chain with the same chainID - this would open the door to replay attacks.
 The first pull request gets the chainID assigned. When creating a chain we can expect that you read EIP155 which states this repo.
 All pull requests trying to replace a chainID because they think their chain is better than the other will be closed.
 The only way to get a chain reassigned is when the old chain gets deprecated. This can e.g. be used for testnets that are short-lived. But then you will get the redFlag "reusedChainID" that should be displayed in clients to warn them about the dangers here.

## Getting your PR merged
### before PR is submitted

Before submitting a PR, please ensure all checks pass by running
master

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Contact

For project inquiries or paid support, please [open an issue](https://github.com/nodoubtz/chains/issues) or contact the repository owner.

---

master
*Built with care by [nodoubtz](https://github.com/nodoubtz)*
=======
Additionally, run Prettier to format your JSON according to the style [defined here ](https://github.com/ethereum-lists/chains/blob/master/.prettierrc.json)
e.g. run

```
npx prettier --write _data/*/*.json
```

### Once PR is submitted

 * Make sure CI is green. There will likely be no review when the CI is red.
 * When making changes that fix the CI problems - please re-request a review - otherwise it is too much work to track such changes with so many PRs daily

## Usages
### Tools
 * [MESC](https://paradigmxyz.github.io/mesc)

### Explorers
 * [Otterscan](https://otterscan.io)

### Wallets
 * [WallETH](https://walleth.org)
 * [TREZOR](https://trezor.io)
 * [Minerva Wallet](https://minerva.digital)

### EIPs
 * EIP-155
 * EIP-3014
 * EIP-3770
 * EIP-4527

### Listing sites
 * [chainid.network](https://chainid.network) / [chainlist.wtf](https://chainlist.wtf)
 * [chainlist.org](https://chainlist.org)
 * [Chainlink docs](https://docs.chain.link/)
 * [dRPC Chainlist - Load-balanced public nodes](https://drpc.org/chainlist)
 * [eth-chains](https://github.com/taylorjdawson/eth-chains)
 * [EVM-BOX](https://github.com/izayl/evm-box)
 * [evmchain.info](https://evmchain.info)
 * [evmchainlist.org](https://evmchainlist.org)
 * [networks.vercel.app](https://networks.vercel.app)
 * [Wagmi compatible chain configurations](https://spenhouet.com/chains)
 * [chainlist.simplr.sh - Info packaged single pager](https://chainlist.simplr.sh)

### Other
 * [FaucETH](https://github.com/komputing/FaucETH)
 * [Sourcify playground](https://playground.sourcify.dev)
 * [Smart Contract UI](https://xtools-at.github.io/smartcontract-ui)

 * Your project - contact us to add it here!
master
