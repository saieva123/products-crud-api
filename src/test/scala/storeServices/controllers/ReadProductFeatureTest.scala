package storeServices.controllers

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import storeServices.models.http.{CreateProductSuccess, QueryProductsSuccess}
//import com.whisk.docker.impl.dockerjava.DockerKitDockerJava
import com.whisk.docker.scalatest.DockerTestKit
import storeServices.{DockerMongoKitSpotify, Server}

class ReadProductFeatureTest extends FeatureTest with DockerTestKit with DockerMongoKitSpotify {

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

  test("Server should be available to read specific data") {
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

    val r1 = server.httpGetJson[QueryProductsSuccess](
      path = s"/admin/api/v1/products?productId=${id1.productId}",
      andExpect = Ok
    )
    r1.num shouldBe 1
    r1.detail.size shouldBe 1
    r1.detail.head.productId shouldBe id1.productId
    r1.detail.head.title shouldBe "goldenHoneyLemon"
    r1.detail.head.showName shouldBe "黃金檸檬蜂蜜"
    r1.detail.head.desc shouldBe Some("good to drink")
    r1.detail.head.tags shouldBe List("yummy", "sweet", "health")
    r1.detail.head.published shouldBe true
    r1.detail.head.images shouldBe Some("/images/p1.jpg")

    val r2 = server.httpGetJson[QueryProductsSuccess](
      path = s"/admin/api/v1/products?title=sataysauce", // regex
      andExpect = Ok
    )
    r2.num shouldBe 1
    r2.detail.size shouldBe 1
    r2.detail.head.productId shouldBe id2.productId
    r2.detail.head.title shouldBe "sataySauce"
    r2.detail.head.showName shouldBe "沙茶醬"
    r2.detail.head.desc shouldBe Some("真材實料")
    r2.detail.head.tags shouldBe List("real", "good")
    r2.detail.head.published shouldBe true
    r2.detail.head.images shouldBe Some("/images/p2.jpg")

  }

  test("server should be available to get all data") {
    val r = server.httpGetJson[QueryProductsSuccess](
      path = s"/admin/api/v1/products",
      andExpect = Ok
    )
    r.num shouldBe 2
    r.detail.size shouldBe 2
  }

  test("no condition match") {
    server.httpGet(
      path = s"/admin/api/v1/products?published=false",
      andExpect = NoContent
    )
  }

  test("db lost connection") {
    // 500
    stopAllQuietly()

    server.httpGet(
      path = s"/admin/api/v1/products?published=false",
      andExpect = InternalServerError
    )
  }

}
