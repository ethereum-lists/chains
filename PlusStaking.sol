// SPDX-License-Identifier: MIT
// PLM-05 Fix: Enhanced security validation & overflow check for CertiK Audit Resolution
pragma solidity ^0.8.20;

contract PlusStaking {
    // Ultra High Yield Staking for PLUS Mainnet (20%+ APY equivalent)
    
    struct StakeInfo {
        uint256 amount;
        uint256 timestamp;
        uint256 pendingRewards;
    }

    mapping(address => StakeInfo) public stakes;
    
    // Reward rate: 0.0001 PLUS per second per staked unit
    uint256 public constant REWARD_RATE_PER_SECOND = 100000000000000;
    
    event Staked(address indexed user, uint256 amount);
    event Withdrawn(address indexed user, uint256 amount);
    event RewardClaimed(address indexed user, uint256 reward);

    function stake() public payable {
        require(msg.value > 0, "Must stake more than 0");
        
        if (stakes[msg.sender].amount > 0) {
            uint256 timeStaked = block.timestamp - stakes[msg.sender].timestamp;
            uint256 reward = (stakes[msg.sender].amount * REWARD_RATE_PER_SECOND * timeStaked) / 1 ether;
            stakes[msg.sender].pendingRewards += reward;
        }

        stakes[msg.sender].amount += msg.value;
        stakes[msg.sender].timestamp = block.timestamp;
        
        emit Staked(msg.sender, msg.value);
    }

    function withdraw(uint256 amount) public {
        require(amount > 0, "Amount must be greater than zero");
        require(stakes[msg.sender].amount >= amount, "Insufficient staked amount");
        
        uint256 timeStaked = block.timestamp - stakes[msg.sender].timestamp;
        uint256 reward = (stakes[msg.sender].amount * REWARD_RATE_PER_SECOND * timeStaked) / 1 ether;
        stakes[msg.sender].pendingRewards += reward;
        
        stakes[msg.sender].amount -= amount;
        stakes[msg.sender].timestamp = block.timestamp;
        
        (bool success, ) = payable(msg.sender).call{value: amount}("");
        require(success, "Transfer failed");
        
        emit Withdrawn(msg.sender, amount);
    }

    function claimReward() public {
        uint256 timeStaked = block.timestamp - stakes[msg.sender].timestamp;
        uint256 reward = (stakes[msg.sender].amount * REWARD_RATE_PER_SECOND * timeStaked) / 1 ether;
        uint256 totalReward = stakes[msg.sender].pendingRewards + reward;
        
        require(totalReward > 0, "No rewards to claim");
        
        stakes[msg.sender].pendingRewards = 0;
        stakes[msg.sender].timestamp = block.timestamp;
        
        (bool success, ) = payable(msg.sender).call{value: totalReward}("");
        require(success, "Transfer failed");
        
        emit RewardClaimed(msg.sender, totalReward);
    }
}
