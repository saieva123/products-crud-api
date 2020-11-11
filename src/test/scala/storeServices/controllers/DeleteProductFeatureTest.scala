package storeServices.controllers

import java.util.UUID

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import storeServices.models.http.{CreateProductSuccess, QueryProductsSuccess}
//import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.whisk.docker.scalatest.DockerTestKit
import storeServices.{DockerMongoKitSpotify, Server}

class DeleteProductFeatureTest extends FeatureTest with DockerTestKit with DockerMongoKitSpotify {

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

  test("Server should be available to delete specific data") {
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

    server.httpDelete(
      path = s"/admin/api/v1/products/${id1.productId}",
      andExpect = Ok
    )

    val r1 = server.httpGetJson[QueryProductsSuccess](
      path = s"/admin/api/v1/products",
      andExpect = Ok
    )
    r1.num shouldBe 1
    r1.detail.size shouldBe 1
    r1.detail.head.productId shouldBe id2.productId

    server.httpGet(
      path = s"/admin/api/v1/products?productId=${id1.productId}",
      andExpect = NoContent
    )

  }

  test("invalid request") {
    server.httpDelete(
      path = s"/admin/api/v1/products/12345678",
      andExpect = BadRequest
    )
  }

  test("delete nothing") {
    server.httpDelete(
      path = s"/admin/api/v1/products/${UUID.randomUUID().toString}",
      andExpect = NoContent
    )
  }

  test("db lost connection") {

    // 500
    stopAllQuietly()
    server.httpDelete(
      path = s"/admin/api/v1/products/${UUID.randomUUID().toString}",
      andExpect = InternalServerError
    )
  }

}
