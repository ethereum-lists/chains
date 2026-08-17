// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

/**
 * @title PlusGPT
 * @dev PlusGPT token contract with zero-address validation and optimized visibility.
 * @notice Resolves CertiK findings PLM-05, PLM-06, PLM-07.
 */
contract PlusGPT {
    string public constant name = "PlusGPT";
    string public constant symbol = "PGPT";
    uint8  public constant decimals = 18;
    
    uint256 public immutable totalSupply;

    event Approval(address indexed src, address indexed guy, uint wad);
    event Transfer(address indexed src, address indexed dst, uint wad);

    mapping (address => uint256) public balanceOf;
    mapping (address => mapping (address => uint256)) public allowance;

    constructor(uint256 initialSupply) {
        uint256 total = initialSupply * (10 ** uint256(decimals));
        totalSupply = total;
        balanceOf[msg.sender] = total;
        emit Transfer(address(0), msg.sender, total);
    }

    function approve(address guy, uint256 wad) external returns (bool) {
        require(guy != address(0), "ERC20: approve to zero address");
        allowance[msg.sender][guy] = wad;
        emit Approval(msg.sender, guy, wad);
        return true;
    }

    function _transfer(address src, address dst, uint256 wad) internal {
        require(dst != address(0), "ERC20: transfer to zero address");
        require(wad > 0, "Amount must be greater than zero");
        require(balanceOf[src] >= wad, "Insufficient balance");
        balanceOf[src] -= wad;
        balanceOf[dst] += wad;
        emit Transfer(src, dst, wad);
    }

    function transfer(address dst, uint256 wad) external returns (bool) {
        _transfer(msg.sender, dst, wad);
        return true;
    }

    function transferFrom(address src, address dst, uint256 wad) external returns (bool) {
        if (src != msg.sender && allowance[src][msg.sender] != type(uint256).max) {
            require(allowance[src][msg.sender] >= wad, "Allowance exceeded");
            allowance[src][msg.sender] -= wad;
        }
        _transfer(src, dst, wad);
        return true;
    }
}
