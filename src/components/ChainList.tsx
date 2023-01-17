import { SimpleGrid } from "@chakra-ui/react";
import React from "react";
import { ChainData } from "../types/chain";
import { Chain } from "./Chain";

export const ChainList = ({
  chains,
}: {
  chains: (ChainData & { id: string })[];
}) => (
  <>
    <SimpleGrid minChildWidth="300px" spacing={4}>
      {chains.map((c) => (
          <Chain key={c.id} {...c} />
      ))}
    </SimpleGrid>
  </>
);
