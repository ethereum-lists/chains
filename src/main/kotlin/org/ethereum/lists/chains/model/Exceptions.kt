package org.ethereum.lists.chains.model

class FileNameMustMatchChainId : Exception("chainId must match the filename")
class ExtensionMustBeJSON : Exception("filename extension must be json")
class ShouldHaveNoExtraFields(fields: Set<String>) : Exception("should have no extra field $fields")
class ShouldHaveNoMissingFields(fields: Set<String>) : Exception("missing field(s) $fields")
class RPCMustBeList : Exception("rpc must be a list")
class RPCMustBeListOfStrings : Exception("rpc must be a list of strings")
class ENSMustBeObject: Exception("ens must be an object")
class ENSMustHaveOnlyRegistry: Exception("ens can only have a registry currently")
class ENSRegistryAddressMustBeValid: Exception("ens registry must have valid address")
class NameMustBeUnique(dup: String): Exception(" name must be unique - but found `$dup` more than once")
class ShortNameMustBeUnique(dup: String): Exception("short name must be unique - but found `$dup` more than once")
class UnsupportedNamespace(): Exception("So far only the EIP155 namespace is supported")