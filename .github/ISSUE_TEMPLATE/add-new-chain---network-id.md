---
name: Add new chain / network ID
about: Adding a new chain and/or network ID by filing an issue
title: "[New Chain / Network]"
labels: ''
assignees: ''

---

> Fill out the following to add your chain / network id, OR propose a Pull Request making your edits to the ```_data/chains.json``` file directly

```
{
  "name": "Ethereum Mainnet",
  "shortName": "eth",
  "chain": "ETH",
  "network": "mainnet",
  "chainId": 1,
  "networkId": 1,
  "rpc": ["https://mainnet.infura.io"],
  "faucets": [],
  "infoURL: "https://ethereum.org",
  "nativeCurrency": {"name":"Ether","symbol":"ETH","decimals":18}
}
```
