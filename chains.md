---
layout: default
title: Chains
permalink: /chains/
nav_order: 10
---

List of chains in block format. You can link directly to each section using the header name:

{% for json in site.data.chains %}

{% assign chain = json[1] %}
{% assign chainlink = chain.name | downcase | replace: " ", "-" | append: "-" | append: chain.short_name | append: "-" | append: chain.network_id %}
<a name="{{ chainlink }}"/>

<h2><a href="#{{ chainlink }}">{{ chain.name }} ({{ chain.short_name }})</a></h2>
<ul>
<li>Short Name: {{ chain.shortName }}</li>
<li>Chain: {{ chain.chain }}</li>
<li>Chain ID: {{ chain.chainId }}</li>
<li>Network: {{ chain.network }}</li>
<li>Network ID: {{ chain.networkId }}</li>
<li>File: <pre style="display: inline">{{ json[0] }}.json</pre> </li>
</ul>
<hr />
{% endfor %}
