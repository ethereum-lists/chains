import {
  Box,
  Button,
  Center,
  Flex,
  Stat,
  StatGroup,
  StatLabel,
  StatNumber,
  Text,
} from "@chakra-ui/react";
import React, { useContext } from "react";
import { Web3Context } from "../context/Web3Context";
import { ChainData } from "../types/chain";

export const Chain = ({
  name,
  chainId,
  nativeCurrency,
  ...rest
}: ChainData) => {
  const { isConnected, handleConnect, handleAddChain } =
    useContext(Web3Context);
  const handleAddChainClick = () => {
    handleAddChain({ name, chainId, nativeCurrency, ...rest });
  };
  return (
    <Box px="5" py="4" borderWidth="1px" rounded="md" boxShadow="base">
      <Flex mb="2">
        <Text
          fontSize="lg"
          fontWeight="semibold"
          lineHeight="short"
          isTruncated
          verticalAlign="middle"
        >
          {name}
        </Text>
      </Flex>
      <StatGroup>
        <Stat>
          <StatLabel>Chain ID</StatLabel>
          <StatNumber fontSize="md">{chainId}</StatNumber>
        </Stat>
        <Stat>
          <StatLabel>Currency</StatLabel>
          <StatNumber fontSize="md">{nativeCurrency.symbol}</StatNumber>
        </Stat>
      </StatGroup>
      <Center mt="2">
        {!isConnected ? (
          <Button onClick={handleConnect}>Connect Wallet</Button>
        ) : (
          <Button onClick={handleAddChainClick}>Add Chain</Button>
        )}
      </Center>
    </Box>
  );
};
