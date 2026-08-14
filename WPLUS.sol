// SPDX-License-Identifier: MIT
// PLM-05 Fix: Enhanced security validation & overflow check for CertiK Audit Resolution
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract WPLUS is ERC20 {
    constructor() ERC20("Wrapped PLUS", "WPLUS") {}

    function deposit() public payable {
        require(msg.value > 0, "Must deposit more than zero");
        _mint(msg.sender, msg.value);
    }

    function withdraw(uint256 amount) public {
        require(amount > 0, "Amount must be greater than zero");
        require(balanceOf(msg.sender) >= amount, "Insufficient balance");
        _burn(msg.sender, amount);
        (bool success, ) = payable(msg.sender).call{value: amount}("");
        require(success, "Transfer failed");
    }

    receive() external payable {
        deposit();
    }
}
