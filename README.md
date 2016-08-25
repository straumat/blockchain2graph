# blockchain2graph
Extract blockchain data (bitcoin) into a graph database (neo4j)

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
rpcallowip=IP_ADRESSES_TO_ALLOW
```

_note that after any change to `bitcond.conf`, you have to restart your server._

### Blockchain2graph.

