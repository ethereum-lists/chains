---
layout: home
title: Home
nav_order: 1
---

# EVM Networks

A list of EVM networks. Wallets and Web3 middleware providers should be able to use the appropriate Chain ID and Network ID to connect to the correct chain.

This table is also available as a JSON feed at [chains.json](/chains.json).

<table>
  <tr>
    <th>Chain ID</th>
    <th>Name</th>
    <th>Short Name</th>
    <th>Chain</th>
    <th>Network</th>
    <th>Network ID</th>
  </tr>
{% for json in site.data.chains %}
  {% assign chain = json[1] %}
  <tr>
    <td>{{ chain.chain_id }}</td>
    <td>{{ chain.name }}</td>
    <td>{{ chain.short_name }}</td>
    <td>{{ chain.chain }}</td>
    <td>{{ chain.network }}</td>
    <td>{{ chain.network_id }}</td>
  </tr>
{% endfor %}
</table>
