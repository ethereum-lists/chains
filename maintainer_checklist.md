A checklist for things to check before merging a chain PR.

* If the PR contains explorers claim to adhere to EIP3091 - check if they really do.
* If the PR contains icons:
  * `ipfs get` all icon CIDs
  * check if the size of the icons you got match the size given in the PR
* Check if a PR does not remove a chain - chains cannot be removed - only deprecated (to protect from replay attacks)
* Check if a PR does not assign a chainID to a newer chain (something like https://github.com/ethereum-lists/chains/pull/1750)

If anyone has ideas on how to automate these things in CI - PRs welcome!
