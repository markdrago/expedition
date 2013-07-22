// Comment to get more information during initialization
logLevel := Level.Warn

// Local repository added after building git version of play
resolvers += Resolver.file("Local Repository", file("/home/mdrago/code/playframework/repository/local"))(Resolver.ivyStylePatterns)

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1-GIT")