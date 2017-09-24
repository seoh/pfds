name := "purely-functional-data-structure"
scalaVersion in Global := "2.12.3"

lazy val ch02 = (project in file("ch02")).settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "1.0.0-MF",
    "io.monix" %% "monix" % "2.3.0"
  )
)
