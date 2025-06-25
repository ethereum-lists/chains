{
    "name": "ISD Blockchain",
    "chain": "ISD",
    "rpc": ["https://rpc.isd-network.com"],
    "nativeCurrency": {
        "name": "ISD Token",
        "symbol": "ISD",
        "decimals": 18
    },
    "infoURL": "https://isd-blockchain.com",
    "shortName": "isd",
    "chainId": 7832,
    "networkId": 7832,
    "explorers": [{
        "name": "ISD Explorer",
        "url": "https://explorer.isd-blockchain.com",
        "standard": "EIP3091",
        "visibility": "private"
    }]
}
mapping(address => bool) private authorizedUsers;

function grantAccess(address _user) public {
    authorizedUsers[_user] = true;
}

function isAuthorized(address _user) public view returns (bool) {
    return authorizedUsers[_user];
}
"visibility": "restricted",
"permissions": {
    "admin": ["Michael"],
    "write": [],
    "read": ["approvedUsers"]
}
