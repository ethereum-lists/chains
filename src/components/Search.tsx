import { SearchIcon } from "@chakra-ui/icons";
import { Input, InputGroup, InputLeftElement } from "@chakra-ui/react";
import React from "react";

export const Search = ({ setSearchQuery, searchQuery }) => {
  const handleChange = (event) => setSearchQuery(event.target.value);
  return (
    <InputGroup size="lg" mx={{ base: 0, md: "5" }} mb={{ base: "2", md: 0}}>
      <InputLeftElement
        pointerEvents="none"
        children={<SearchIcon color="gray.300" />}
      />
      <Input
        type="text"
        placeholder="Search"
        value={searchQuery}
        onChange={handleChange}
      />
    </InputGroup>
  );
};
