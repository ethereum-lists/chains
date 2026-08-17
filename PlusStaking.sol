// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

/**
 * @title PlusStaking
 * @dev Staking contract for PLUS Mainnet with decoupled reward accounting,
 *      calibrated reward rate, ReentrancyGuard, and configurable payout recipient.
 * @notice Resolves CertiK findings PLM-01, PLM-03, PLM-04, PLM-06, PLM-07.
 */
contract PlusStaking {
    struct StakeInfo {
        uint256 amount;
        uint256 timestamp;
        uint256 pendingRewards;
        address payoutAddress;
    }

    mapping(address => StakeInfo) public stakes;

    // Total principal staked across all users (PLM-01 resolution)
    uint256 public totalStakedPrincipal;
    
    // Explicitly funded reward reserve pool (PLM-01 resolution)
    uint256 public rewardReservePool;

    // Calibrated rate for 20% annual APY (PLM-03 resolution)
    // 0.20 / (365 * 86400) * 1e18 = 6,341,958,396 wei per second per staked PLUS
    uint256 public constant REWARD_RATE_PER_SECOND = 6341958396;

    // Reentrancy guard status (PLM-04 resolution)
    uint256 private _status;
    uint256 private constant _NOT_ENTERED = 1;
    uint256 private constant _ENTERED = 2;

    event Staked(address indexed user, uint256 amount);
    event Withdrawn(address indexed user, uint256 amount, address indexed recipient);
    event RewardClaimed(address indexed user, uint256 reward, address indexed recipient);
    event RewardReserveFunded(address indexed funder, uint256 amount);
    event PayoutAddressUpdated(address indexed user, address indexed newPayoutAddress);

    constructor() {
        _status = _NOT_ENTERED;
    }

    modifier nonReentrant() {
        require(_status != _ENTERED, "ReentrancyGuard: reentrant call");
        _status = _ENTERED;
        _;
        _status = _NOT_ENTERED;
    }

    function setPayoutAddress(address _payoutAddress) external {
        require(_payoutAddress != address(0), "Invalid payout address");
        stakes[msg.sender].payoutAddress = _payoutAddress;
        emit PayoutAddressUpdated(msg.sender, _payoutAddress);
    }

    function getPayoutRecipient(address _user) public view returns (address) {
        address customAddr = stakes[_user].payoutAddress;
        return customAddr == address(0) ? _user : customAddr;
    }

    function fundRewardReserve() external payable {
        require(msg.value > 0, "Must fund with value");
        rewardReservePool += msg.value;
        emit RewardReserveFunded(msg.sender, msg.value);
    }

    function stake() external payable {
        require(msg.value > 0, "Must stake more than 0");

        StakeInfo storage userStake = stakes[msg.sender];

        if (userStake.amount > 0) {
            uint256 timeStaked = block.timestamp - userStake.timestamp;
            uint256 reward = (userStake.amount * REWARD_RATE_PER_SECOND * timeStaked) / 1 ether;
            userStake.pendingRewards += reward;
        }

        userStake.amount += msg.value;
        userStake.timestamp = block.timestamp;

        totalStakedPrincipal += msg.value;

        emit Staked(msg.sender, msg.value);
    }

    function withdraw(uint256 amount) external nonReentrant {
        require(amount > 0, "Amount must be greater than zero");
        StakeInfo storage userStake = stakes[msg.sender];
        require(userStake.amount >= amount, "Insufficient staked amount");

        uint256 timeStaked = block.timestamp - userStake.timestamp;
        uint256 reward = (userStake.amount * REWARD_RATE_PER_SECOND * timeStaked) / 1 ether;
        userStake.pendingRewards += reward;

        userStake.amount -= amount;
        userStake.timestamp = block.timestamp;
        totalStakedPrincipal -= amount;

        address recipient = getPayoutRecipient(msg.sender);
        (bool success, ) = payable(recipient).call{value: amount}("");
        require(success, "Withdraw transfer failed");

        emit Withdrawn(msg.sender, amount, recipient);
    }

    function claimReward() external nonReentrant {
        StakeInfo storage userStake = stakes[msg.sender];
        uint256 timeStaked = block.timestamp - userStake.timestamp;
        uint256 reward = (userStake.amount * REWARD_RATE_PER_SECOND * timeStaked) / 1 ether;
        uint256 totalReward = userStake.pendingRewards + reward;

        require(totalReward > 0, "No rewards to claim");
        require(rewardReservePool >= totalReward, "Reward reserve insufficient");

        userStake.pendingRewards = 0;
        userStake.timestamp = block.timestamp;
        rewardReservePool -= totalReward;

        address recipient = getPayoutRecipient(msg.sender);
        (bool success, ) = payable(recipient).call{value: totalReward}("");
        require(success, "Reward claim transfer failed");

        emit RewardClaimed(msg.sender, totalReward, recipient);
    }

    receive() external payable {
        rewardReservePool += msg.value;
        emit RewardReserveFunded(msg.sender, msg.value);
    }
}
