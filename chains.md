---
layout: default
title: Chains
permalink: /chains/
nav_order: 10
---

List of chains in block format. You can link directly to each section using the header name:

{% for chain in site.data.chains %}
{% assign chainlink = chain.name | downcase | replace: " ", "-" | append: "-" | append: chain.short_name | append: "-" | append: chain.network_id %}
<a name="{{ chainlink }}"/>
<h2><a href="#{{ chainlink }}">{{ chain.name }} ({{ chain.short_name }})</a></h2>
<ul>
<li>Short Name: {{ chain.short_name }}</li>
<li>Chain: {{ chain.chain }}</li>
<li>Chain ID: {{ chain.chain_id }}</li>
<li>Network: {{ chain.network }}</li>
<li>Network ID: {{ chain.network_id }}</li>
</ul>
<hr />
{% endfor %}
