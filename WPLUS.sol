// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

/**
 * @title WPLUS
 * @dev Wrapped PLUS token contract with checked low-level call payouts and reentrancy protection.
 * @notice Resolves CertiK findings PLM-04, PLM-05, PLM-07.
 */
contract WPLUS is ERC20 {
    uint256 private _status;
    uint256 private constant _NOT_ENTERED = 1;
    uint256 private constant _ENTERED = 2;

    constructor() ERC20("Wrapped PLUS", "WPLUS") {
        _status = _NOT_ENTERED;
    }

    modifier nonReentrant() {
        require(_status != _ENTERED, "ReentrancyGuard: reentrant call");
        _status = _ENTERED;
        _;
        _status = _NOT_ENTERED;
    }

    function deposit() public payable {
        require(msg.value > 0, "Must deposit more than zero");
        _mint(msg.sender, msg.value);
    }

    function withdraw(uint256 amount) external nonReentrant {
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
