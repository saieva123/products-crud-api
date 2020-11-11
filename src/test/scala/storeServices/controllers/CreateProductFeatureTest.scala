package storeServices.controllers

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import com.twitter.util.Await
import storeServices.models.http.{CreateProductResponse, CreateProductSuccess}

import scala.concurrent.duration._
//import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.Matchers.convertToAnyShouldWrapper
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import storeServices.{DockerMongoKitSpotify, Server}

class CreateProductFeatureTest extends FeatureTest with DockerTestKit with DockerMongoKitSpotify {

  val serviceVersion: String = "0.9.9"

  override def beforeAll(): Unit = {
    super.beforeAll()
    isContainerReady(mongodbContainer).futureValue shouldBe true
  }

  override def afterAll(): Unit = {
    stopAllQuietly()
    super.afterAll()
  }

  override val server: EmbeddedHttpServer =
    new EmbeddedHttpServer(twitterServer = new Server, flags = Map("service.version" -> serviceVersion))

  test("Server should be available to store product to DB") {

    /** Case 1: return 201, create product successfully  **/

    server.httpPostJson[CreateProductSuccess](
      path = "/admin/api/v1/products/product",
      postBody = """
                   |{
                   | "title": "goldenHoneyLemon",
                   | "showName": "黃金檸檬蜂蜜",
                   | "desc": "good to drink",
                   | "tags": ["yummy", "sweet", "health"],
                   | "published": true,
                   | "images": "/images/p1.jpg"
                   |}
                   |""".stripMargin,
      andExpect = Created
    )
  }

  test("Server should be available to handle duplicate productName") {
    // 202
    server.httpPost(
      path = "/admin/api/v1/products/product",
      postBody = """
                   |{
                   | "title": "goldenHoneyLemon",
                   | "showName": "黃金檸檬蜂蜜",
                   | "desc": "good to drink",
                   | "tags": ["yummy", "sweet", "health"],
                   | "published": true,
                   | "images": "/images/p1.jpg"
                   |}
                   |""".stripMargin,
      andExpect = Accepted
    )
  }

  test("db lost connection") {
    // 500
    stopAllQuietly()

    server.httpPost(
      path = "/admin/api/v1/products/product",
      postBody = """
                   |{
                   | "title": "goldenHoneyLemon",
                   | "showName": "黃金檸檬蜂蜜",
                   | "desc": "good to drink",
                   | "tags": ["yummy", "sweet", "health"],
                   | "published": true,
                   | "images": "/images/p1.jpg"
                   |}
                   |""".stripMargin,
      andExpect = InternalServerError
    )
  }

}
