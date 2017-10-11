# Curator
jar_library(name='curator', jars=[
    jar(org='org.apache.curator', name='curator-framework', rev='4.0.0', excludes=[exclude('org.apache.zookeeper')]),
    jar(org='org.apache.curator', name='curator-recipes', rev='4.0.0', excludes=[exclude('org.apache.zookeeper')]),
    jar(org='org.apache.zookeeper', name='zookeeper', rev='3.4.10'),
    jar(org='org.slf4j', name='slf4j-simple', rev='1.7.25'),
])

# Curator Test
jar_library(name='curator-test', jars=[
    jar(org='org.apache.curator', name='curator-test', rev='4.0.0'),
])

# JUnit
jar_library(name='junit', jars=[
    jar(org='junit', name='junit', rev='4.12'),
    jar(org='org.pantsbuild', name='junit-runner-annotations', rev='0.0.17'),
])

# Scalatest
jar_library(name='scalatest',
    jars=[jar(org='org.scalatest', name='scalatest_2.12', rev='3.0.0', excludes=[exclude('org.scala-lang')])],
    dependencies=['//:scala-library', '//:scala-reflect']
)

