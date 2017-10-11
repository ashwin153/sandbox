# Footprint
A memory footprint calculator built on Dimitris Andreou's [memory-measurer][1]. 

## Build
```
javac -cp lib/*: Footprint.java
java -javaagent:./lib/object-explorer.jar -cp lib/*: Footprint
```

## Results
Results of the memory footprint calculator for various objects of interest in for caching in Caustic
can be found in ```results.txt```.

## Conclusion
In-memory caches are typically bounded in size by a maximum number of cache lines. However, this 
only works well for homogenous value caches (ie. fixed-length cache lines). Otherwise, the cache
would no longer be bounded in size because the memory utilization of the cache grows proportionally
to the total length of its contents.

We may use the results of this experiment to instead bound the size of the cache by the total length
of its contents. This will lead to far more optimal memory utilization and far more predictable
cache sizes for heterogenous value caches. We may compute the length of a cache line using the 
relatively inexpensive function below.

```scala
def sizeof(x: Any): Int = x match {
  case x: String => 40 + math.ceil(x.length / 4.0) * 8
  case x: Flag => 16
  case x: Real => 24
  case x: Text => 16 + sizeof(x.value)
  case x: Revision => 24 + sizeof(x.value) 
}
```

[1]: https://github.com/DimitrisAndreou/memory-measurer
