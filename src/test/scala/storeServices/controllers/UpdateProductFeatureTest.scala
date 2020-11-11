package storeServices.controllers

import java.util.UUID

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import storeServices.models.http.{CreateProductSuccess, QueryProductsSuccess}
//import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.whisk.docker.scalatest.DockerTestKit
import storeServices.{DockerMongoKitSpotify, Server}

class UpdateProductFeatureTest extends FeatureTest with DockerTestKit with DockerMongoKitSpotify {

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

  test("Server should be available to update specific data") {
    // 1st
    val id1 = server.httpPostJson[CreateProductSuccess](
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

    // 2nd
    val id2 = server.httpPostJson[CreateProductSuccess](
      path = "/admin/api/v1/products/product",
      postBody = """
                   |{
                   |  "title": "sataySauce",
                   |  "showName": "沙茶醬",
                   |  "desc": "真材實料",
                   |  "tags": [
                   |    "real",
                   |    "good"
                   |  ],
                   |  "published": true,
                   |  "images": "/images/p2.jpg"
                   | }
                   |""".stripMargin,
      andExpect = Created
    )

    server.httpPut(
      path = s"/admin/api/v1/products/${id1.productId}",
      putBody = """
                  |{
                  | "title": "goldenHoneyLemon",
                  | "showName": "黃金梅利號",
                  | "desc": "good",
                  | "tags": ["yummy"],
                  | "published": false,
                  | "images": "/images/p3.jpg"
                  |}
                  |""".stripMargin,
      andExpect = Ok
    )

    val r1 = server.httpGetJson[QueryProductsSuccess](
      path = s"/admin/api/v1/products?productId=${id1.productId}",
      andExpect = Ok
    )
    r1.num shouldBe 1
    r1.detail.size shouldBe 1
    r1.detail.head.productId shouldBe id1.productId
    r1.detail.head.title shouldBe "goldenHoneyLemon"
    r1.detail.head.showName shouldBe "黃金梅利號"
    r1.detail.head.desc shouldBe Some("good")
    r1.detail.head.tags shouldBe List("yummy")
    r1.detail.head.published shouldBe false
    r1.detail.head.images shouldBe Some("/images/p3.jpg")

  }

  test("invalid request") {
    server.httpPut(
      path = s"/admin/api/v1/products/12345678",
      putBody = """
                  |{
                  | "title": "goldenHoneyLemon",
                  | "showName": "I love samoyed",
                  | "desc": "good",
                  | "tags": ["yummy"],
                  | "published": false,
                  | "images": "/images/p3.jpg"
                  |}
                  |""".stripMargin,
      andExpect = BadRequest
    )
  }

  test("update nothing") {
    server.httpPut(
      path = s"/admin/api/v1/products/${UUID.randomUUID().toString}",
      putBody = """
                  |{
                  | "title": "goldenHoneyLemon",
                  | "showName": "I love samoyed",
                  | "desc": "good",
                  | "tags": ["yummy"],
                  | "published": false,
                  | "images": "/images/p3.jpg"
                  |}
                  |""".stripMargin,
      andExpect = NoContent
    )
  }

  test("db lost connection") {

    // 500
    stopAllQuietly()
    server.httpPut(
      path = s"/admin/api/v1/products/${UUID.randomUUID().toString}",
      putBody = """
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
