package storeServices

//import com.github.dockerjava.api.DockerClient
//import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.spotify.docker.client.DefaultDockerClient
import com.whisk.docker.impl.spotify.SpotifyDockerFactory
import com.whisk.docker.{DockerContainer, DockerFactory, DockerKit, DockerReadyChecker}

import scala.concurrent.duration.DurationInt

trait DockerMongoKitSpotify extends DockerKit {

  val DefaultMongodbPort = 27017

  override val StartContainersTimeout = 300.seconds

  val mongodbContainer = DockerContainer("mongo:3.2.16")
    .withPorts(DefaultMongodbPort -> Some(27018))
    .withReadyChecker(
      DockerReadyChecker.And(
        DockerReadyChecker.LogLineContains("waiting for connections on port"),
        DockerReadyChecker
          .HttpResponseCode(port = DefaultMongodbPort, host = Some("localhost"))
          .within(500.millis)
          .looped(100, 1250.millis)
      )
    )
    .withCommand("mongod", "--nojournal", "--smallfiles", "--syncdelay", "0")

  abstract override def dockerContainers: List[DockerContainer] =
    mongodbContainer :: super.dockerContainers

  implicit override val dockerFactory: DockerFactory = new SpotifyDockerFactory(DefaultDockerClient.fromEnv().build())
}
