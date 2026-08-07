# PLUS Mainnet Smart Contracts Audit Package (CertiK Security Verification)

Official Solidity Smart Contracts for PLUS Mainnet (Chain ID: 88088).

## Deployed Smart Contract Addresses & Source Files

1. **Staking Vault (스테이킹 보상 금고)**
   - Address: `0x5CfEa22674e2E7d251dEB693c0490b6389334F0f`
   - File: [`PlusStaking.sol`](./PlusStaking.sol)

2. **WPLUS Token (Wrapped PLUS Token)**
   - Address: `0x69a9875609258d2fB2B2D7FE84584b491b0FF301`
   - File: [`WPLUS.sol`](./WPLUS.sol)

3. **PLUS-USDT Token (Tether USD Bridged)**
   - Address: `0x55d398326199059f775485246999027b3197955`
   - File: [`PLUS_USDT.sol`](./PLUS_USDT.sol)

4. **PlusGPT AI Token (PGPT Token)**
   - Address: `0x1acBa8e5f7T58e803f841d7beE1C271F90E92923`
   - File: [`PlusGPT.sol`](./PlusGPT.sol)

```

where:
 * The URL MUST be publicly resolvable through IPFS
 * width and height MUST be positive integers
 * format is either "png", "jpg" or "svg"
 * size MUST be less than 250kb

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

where you need to specify the type and the reference to an existing parent. The field about bridges is optional.

You can add a `status` field e.g. to deprecate (via status `deprecated`) a chain (a chain should never be deleted as this would open the door to replay attacks)
Other options for `status` are `active` (default) or `incubating`

## Aggregation

There are also aggregated json files with all chains automatically assembled:
 * https://chainid.network/chains.json
 * https://chainid.network/chains_mini.json (miniaturized - fewer fields for smaller filesize)

## Constraints

 * the shortName and name MUST be unique - see e.g. EIP-3770 on why
 * if referencing a parent chain - the chain MUST exist in the repo
 * if using an IPFS CID for the icon - the CID MUST be retrievable via `ipfs get` - not only through some gateway (means please do not use pinata for now)
 * for more constraints you can look into the CI

## Collision management

 We cannot allow more than one chain with the same chainID - this would open the door to replay attacks.
 The first pull request gets the chainID assigned. When creating a chain we can expect that you read EIP155 which states this repo.
 All pull requests trying to replace a chainID because they think their chain is better than the other will be closed.
 The only way to get a chain reassigned is when the old chain gets deprecated. This can e.g. be used for testnets that are short-lived. But then you will get the redFlag "reusedChainID" that should be displayed in clients to warn them about the dangers here.

## Getting your PR merged
### before PR is submitted

Before submitting a PR, please ensure all checks pass by running:

```bash
$ ./gradlew run

BUILD SUCCESSFUL in 7s
9 actionable tasks: 9 executed
```

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
 * [ChainIndex.dev](https://chainindex.dev/)

### Other
 * [FaucETH](https://github.com/komputing/FaucETH)
 * [Sourcify playground](https://playground.sourcify.dev)
 * [Smart Contract UI](https://xtools-at.github.io/smartcontract-ui)

 * Your project - contact us to add it here!
