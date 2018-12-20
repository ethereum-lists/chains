---
layout: home
---

# EVM Networks

A list of EVM networks.

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
{% for chain in site.data.chains %}
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