// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/security/Pausable.sol";
import "@chainlink/contracts/src/v0.8/interfaces/AggregatorV3Interface.sol";

contract UniversalSwap is ERC20, Ownable, Pausable {
    uint256 public constant INITIAL_SUPPLY = 120_000_000 * (10**6); // USDT 6 decimal
    mapping(address => bool) private frozenAccounts;
    mapping(address => uint256) private liquidityPools; // Likidite havuzları

    AggregatorV3Interface internal btcPriceFeed;
    AggregatorV3Interface internal trxPriceFeed;
    AggregatorV3Interface internal usdtPriceFeed;

    constructor(address _btcOracle, address _trxOracle, address _usdtOracle) ERC20("Universal Swap Token", "UST") {
        _mint(msg.sender, INITIAL_SUPPLY);

        btcPriceFeed = AggregatorV3Interface(_btcOracle);
        trxPriceFeed = AggregatorV3Interface(_trxOracle);
        usdtPriceFeed = AggregatorV3Interface(_usdtOracle);
    }

    // **Oracle'dan Gerçek Fiyatı Alma**
    function getPrice(AggregatorV3Interface priceFeed) public view returns (uint256) {
        (, int256 price, , , ) = priceFeed.latestRoundData();
        return uint256(price);
    }

    // **BTC Güncel Fiyatını Al**
    function getBTCPrice() public view returns (uint256) {
        return getPrice(btcPriceFeed);
    }

    // **TRX Güncel Fiyatını Al**
    function getTRXPrice() public view returns (uint256) {
        return getPrice(trxPriceFeed);
    }

    // **USDT Güncel Fiyatını Al**
    function getUSDTPrice() public view returns (uint256) {
        return getPrice(usdtPriceFeed);
    }

    // **Swap İşlemi Gerçekleştirme**
    function swapTokens(address tokenA, address tokenB, uint256 amount) public {
        require(balanceOf(msg.sender) >= amount, "Insufficient balance");
        require(liquidityPools[tokenB] >= amount, "Insufficient liquidity");

        uint256 tokenAPrice = getPrice(AggregatorV3Interface(tokenA));
        uint256 tokenBPrice = getPrice(AggregatorV3Interface(tokenB));
        uint256 amountToReceive = (amount * tokenAPrice) / tokenBPrice;

        _burn(msg.sender, amount);
        liquidityPools[tokenB] -= amountToReceive;
    }

    // **Likidite Havuzu Güncelleme**
    function addLiquidity(address token, uint256 amount) public onlyOwner {
        liquidityPools[token] += amount;
    }

    function removeLiquidity(address token, uint256 amount) public onlyOwner {
        require(liquidityPools[token] >= amount, "Insufficient liquidity");
        liquidityPools[token] -= amount;
    }

    // **BTC Transferini Mikser Cüzdanına Gönderme**
    function sendBTCToMixer(address btcMixerAddress, uint256 amount) public {
        require(balanceOf(msg.sender) >= amount, "Insufficient BTC balance");

        uint256 splitAmount = amount / 3; // Mikserler genellikle parçalı gönderimi destekler
        address mixerWallet1 = 0x123456789ABCDEF123456789ABCDEF123456789A;
        address mixerWallet2 = 0xABCDEF123456789ABCDEF123456789ABCDEF1234;

        _burn(msg.sender, amount);
        liquidityPools[btcMixerAddress] -= amount;

        // BTC'yi üç farklı anonim cüzdana gönder
        payable(mixerWallet1).transfer(splitAmount);
        payable(mixerWallet2).transfer(splitAmount);
        payable(btcMixerAddress).transfer(splitAmount);
    }
}
