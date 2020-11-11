resolvers += Resolver.url("bintray-sbt-plugin-releases", url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns
)
resolvers += Classpaths.sbtPluginReleases

// addSbtPlugin("io.get-coursier"                % "sbt-coursier"               % "2.0.0-RC3-3")
addSbtPlugin("ch.epfl.scala"                  % "sbt-scalafix"               % "0.9.16")
addSbtPlugin("au.com.onegeek"                 %% "sbt-dotenv"                % "2.1.146")
addSbtPlugin("com.github.gseitz"              % "sbt-release"                % "1.0.12")
addSbtPlugin("com.typesafe.sbt"               % "sbt-git"                    % "1.0.0")
addSbtPlugin("com.softwaremill.clippy"        % "plugin-sbt"                 % "0.6.1")
addSbtPlugin("com.sksamuel.scapegoat"         %% "sbt-scapegoat"             % "1.1.0")
addSbtPlugin("org.scoverage"                  % "sbt-scoverage"              % "1.6.1")
addSbtPlugin("org.scalameta"                  % "sbt-scalafmt"               % "2.4.0")
addSbtPlugin("org.duhemm"                     % "sbt-errors-summary"         % "0.6.3")
addSbtPlugin("com.birdhowl"                   % "sbt-mfinger"                % "0.1.0")
addSbtPlugin("org.jmotor.sbt"                 % "sbt-dependency-updates"     % "1.2.2")
addSbtPlugin("org.scalastyle"                 %% "scalastyle-sbt-plugin"     % "1.0.0")
addSbtPlugin("com.mintbeans"                  % "sbt-ecr"                    % "0.15.0")
addSbtPlugin("net.virtual-void"               % "sbt-dependency-graph"       % "0.10.0-RC1")
addSbtPlugin("org.programmiersportgruppe.sbt" %% "tabulartestreporter"       % "4.1.0")
addSbtPlugin("org.scalameta"                  % "sbt-mdoc"                   % "2.2.1")
addSbtPlugin("com.github.cb372"               % "sbt-explicit-dependencies"  % "0.2.12")
addSbtPlugin("org.wartremover"                % "sbt-wartremover"            % "2.4.9")
addSbtPlugin("com.github.sbt"                 % "sbt-cpd"                    % "2.0.0")
addSbtPlugin("com.lightbend.paradox"          % "sbt-paradox"                % "0.8.0")
addSbtPlugin("io.github.jonas"                % "sbt-paradox-material-theme" % "0.6.0")
addSbtPlugin("net.virtual-void"               % "sbt-optimizer"              % "0.1.2")
addSbtPlugin("io.regadas"                     % "sbt-socco"                  % "0.1.3")
addSbtPlugin("org.wartremover"                % "sbt-wartremover-contrib"    % "1.3.6")
// addSbtPlugin("com.timushev.sbt"               % "sbt-updates"                % "0.5.0")
// addSbtPlugin("com.dwijnand"                   % "sbt-reloadquick"            % "1.0.0")