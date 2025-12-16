A checklist for things to check before merging a chain PR.

* If the PR contains explorers claim to adhere to EIP3091 - check if they really do.
* Check if a PR does not remove a chain - chains cannot be removed - only deprecated (to protect from replay attacks)
* Check if a PR does not assign a chainID to a newer chain (something like https://github.com/ethereum-lists/chains/pull/1750)

Please also make sure to go from oldest PR to newest.

If anyone has ideas on how to automate these things in CI - PRs welcome!
