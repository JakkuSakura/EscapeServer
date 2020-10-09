name := "EscapeServer"

version := "0.1"

scalaVersion := "2.13.3"

// Netty is the core of Xitrum's HTTP(S) feature
libraryDependencies += "io.netty" % "netty-all" % "4.1.43.Final"

// https://github.com/netty/netty/wiki/Native-transports
// Only works on Linux
libraryDependencies += "io.netty" % "netty-transport-native-epoll" % "4.1.43.Final" classifier "linux-x86_64"

// https://github.com/netty/netty/wiki/Forked-Tomcat-Native
// https://groups.google.com/forum/#!topic/netty/oRATC6Tl0A4
// Include all classifiers for convenience
libraryDependencies += "io.netty" % "netty-tcnative" % "2.0.27.Final" classifier "linux-x86_64"
libraryDependencies += "io.netty" % "netty-tcnative" % "2.0.27.Final" classifier "osx-x86_64"
libraryDependencies += "io.netty" % "netty-tcnative" % "2.0.27.Final" classifier "windows-x86_64"

// Javassist boosts Netty 4 speed
libraryDependencies += "org.javassist" % "javassist" % "3.26.0-GA"

// https://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.6"

resolvers += "Jitpack" at "https://jitpack.io/"
libraryDependencies += "com.github.Steveice10" % "MCProtocolLib" % "1.15.2-1"
libraryDependencies += "com.github.ProtocolSupport" % "ProtocolSupport" % "master-SNAPSHOT"

