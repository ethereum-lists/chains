import WalletConnectProvider from "@walletconnect/web3-provider";
import React, { createContext, useEffect, useState } from "react";
import { Web3Provider as EthersWeb3 } from "@ethersproject/providers";
import { ChainData } from "../types/chain";

// Hack to fix build
const Web3Modal = typeof window !== `undefined` ? require("web3modal") : null;

export const Web3Context = createContext({});

export const Web3Provider = ({ children }) => {
  const [web3, setWeb3] = useState<any | undefined>(undefined);
  const [address, setAddress] = useState(undefined);
  const providerOptions = {
    walletconnect: {
      package: WalletConnectProvider,
      options: {
        infuraId: "854b581018fe44a59897b53ee6a19551",
      },
    },
  };

  const web3Modal =
    Web3Modal &&
    new Web3Modal.default({
      cacheProvider: true, // optional
      providerOptions, // required
    });

  const handleConnect = () => {
    web3Modal.connect().then(setWeb3);
  };

  const isConnected = web3 !== undefined;
  const provider = isConnected && new EthersWeb3(web3, "any");
  const signer = provider && provider.getSigner();

  const handleAddChain = (chain: ChainData) => {
    provider.send("wallet_addEthereumChain", [
      {
        chainId: `0x${chain.chainId.toString(16)}`,
        chainName: chain.name,
        nativeCurrency: chain.nativeCurrency,
        rpcUrls: chain.rpc,
        blockExplorerUrls: chain.explorers?.map((e) => e.url),
      },
    ]);
  };

  const updateInfo = () => {
    signer.getAddress().then((res) => setAddress(res));
  };

  useEffect(() => {
    if (web3) {
      updateInfo();
      web3.on("accountsChanged", updateInfo);
      web3.on("chainChanged", updateInfo);
    }
  }, [web3]);

  return (
    <Web3Context.Provider
      value={{
        web3,
        setWeb3,
        handleConnect,
        isConnected,
        handleAddChain,
        address,
      }}
    >
      {children}
    </Web3Context.Provider>
  );
};
