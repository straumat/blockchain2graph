# blockchain2graph [![Build Status](https://travis-ci.org/straumat/blockchain2graph.svg?branch=master)](https://travis-ci.org/straumat/blockchain2graph) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/99d74d003bbc4f56abed38301003c0b0)](https://www.codacy.com/app/stephane-traumat/blockchain2graph?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=straumat/blockchain2graph&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/99d74d003bbc4f56abed38301003c0b0)](https://www.codacy.com/app/stephane-traumat/blockchain2graph?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=straumat/blockchain2graph&amp;utm_campaign=Badge_Coverage)
blockchain2graph extract data from the bitcoin blockchain into a graph database (neo4j for now). Once the data is there you can do anything you want like :  

* Process and query blockchain data with the [neo4j query language](https://neo4j.com/developer/cypher-query-language/).
* Build web sites that presents information like [Blockchain info](https://blockchain.info/fr).
* Add meta information to addresses and transactions directly in the nodes.

At [Blockchain Inspector](http://www.blockchaininspector.com/), we are using Artificial Intelligence to fight fraud in the Blockchain. To do so, we needed to have a more conveniant way to represent blockchain data than the one offered by bitcoind. Thanks to this tool, we can analyse blockchain data as transactions between nodes and add meta informations on them.

## Installation.

### The bitcoindnode.
Blockchain2graph needs to connect to a bitcoind node accepting REST queries and it certainly requires some changes to its configuration. 

First the bitcoind must reply to `getrawtransaction` queries. To do so, your `bitcond.conf` file must contain the line `txindex=1`. If your server was already running without this parameter, you will have to restart it with the `-reindex` option.

Then, if Blockchain2graph is not on the same server than bitcoind, you have to change your `bitcond.conf` file to allow REST queries from other IP :
```
rpcuser=bitcoinrpc
rpcpassword=YOUR_BITCOIND_PASSWORD
server=1
rest=1
rpcallowip=AN_IP_ADRESS_TO_ALLOW
rpcallowip=ANOTHER_IP_ADRESS_TO_ALLOW
```

_note that after any change to `bitcond.conf`, you have to restart your server._

### Blockchain2graph.
