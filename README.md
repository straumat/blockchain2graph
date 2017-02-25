# blockchain2graph [![Build Status](https://travis-ci.org/straumat/blockchain2graph.svg?branch=master)](https://travis-ci.org/straumat/blockchain2graph) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/99d74d003bbc4f56abed38301003c0b0)](https://www.codacy.com/app/stephane-traumat/blockchain2graph?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=straumat/blockchain2graph&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/99d74d003bbc4f56abed38301003c0b0)](https://www.codacy.com/app/stephane-traumat/blockchain2graph?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=straumat/blockchain2graph&amp;utm_campaign=Badge_Coverage) [![Stories in Ready](https://badge.waffle.io/straumat/blockchain2graph.svg?label=ready&title=Ready)](http://waffle.io/straumat/blockchain2graph)
Blockchain2graph extract informations from the bitcoin blockchain and insert them into a neo4j graph database. 

Here are some use cases : 
* Query blockchain data with the [cypher query language](https://neo4j.com/developer/cypher-query-language/).
* Build web sites like [Blockchain.info](https://blockchain.info/).
* Add meta informations to addresses and transactions.

At [Blockchain Inspector](http://www.blockchaininspector.com/), we are using Artificial Intelligence to fight fraud in the Blockchain and we needed to have a convenient way to access blockchain informations. With blockchain2graph, we can analyse blockchain data as transactions between nodes and add meta informations on them.

The documentation can be found [here](https://github.com/straumat/blockchain2graph/wiki).

Blockchain2graph is released under version 3.0 of the [GNU General Public Licence](https://github.com/straumat/blockchain2graph/blob/master/LICENSE).

![blockchain2graph console log](https://raw.githubusercontent.com/straumat/blockchain2graph/gh-pages/images/b2g-console-screenshot.png)


# Building and Running with docker

You can easily launch a standalone version for test purpose with docker-compose script provided.
3 containers are provided and link together : 
  - Neo4j
  - Bitcoind
  - Blockchain2graph app
  
You need to first install Docker and Docker Compose with official documentation : 
  - [Docker Installation](https://docs.docker.com/engine/installation/)
  - [Docker Compose Installation](https://docs.docker.com/compose/install/)
  
Then you can build Blockchain2graph with maven : 

` maven install docker:build`

And launch all 3 apps :

` docker-compose up -d`

_The first time you launch Neo4j,_ you need to change default password by connecting to http://localhost:7474/ 
and log as neo4j / neo4j. You can set new password to neo4j123 to match docker-compose environment variable and restart app :

` docker-compose restart app`

Blockchain2graph is now available at `http://localhost:8080/`

If you want to view realtime logs :

` docker-compose logs -f`

Note : Bitcoin and Neo4j data are store in data folder.