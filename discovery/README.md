# Discovery
A service discovery implementation. Service discovery is built on top of [ZooKeeper][1] 
(__version 3.4.10__) and [Curator][3]. While Curator itself does already provide an implementation 
of [service discovery][4], I personally found it to be a verbose, unintuitive interface and so I 
chose to implement my own version of service discovery instead.

```sh
brew install zookeeper
brew services start zookeeper
```

