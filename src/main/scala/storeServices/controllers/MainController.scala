package storeServices.controllers

import javax.inject.{Inject, Singleton}
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finatra.json.annotations.CamelCaseMapper
import io.swagger.models.Swagger
import storeServices.models.http._
import storeServices.services.{
  CreateProductService,
  DeleteProductService,
  QueryProductService,
  SampleMessageService,
  UpdateProductService
}

@Singleton
class MainController @Inject() (
    s: Swagger,
    createSrv: CreateProductService,
    querySrv: QueryProductService,
    updateSrv: UpdateProductService,
    deleteSrv: DeleteProductService
) extends SwaggerController {
  implicit protected val swagger = s

  // create
  postWithDoc("/admin/api/v1/products/product") { o =>
    o.summary("Create")
      .description("""
                     |This API serves to create a new product to store.
                     |
                     |## Request
                     |
                     || Name      | Data Type    | Query Type | Example              | Description               | Required | Unique |
                     ||-----------|--------------|------------|----------------------|---------------------------|----------|--------|
                     || title     | String       | Body       | goldenHoneyLemon     | product name abbreviation | Y        | Y      |
                     || showName  | String       | Body       | 黃金檸檬蜂蜜            | show name                 | Y        |        |
                     || desc      | String       | Body       | good to drink        | product description       | N        |        |
                     || tags      | String List  | Body       | yummy, sweet, health | tag                       | N        |        |
                     || published | Boolean      | Body       | true                 | published or not          | Y        |        |
                     || images    | String       | Body       | /images/p1.jpg       | image path                | N        |        |
                     |
                     |
                     |## Request Example
                     |
                     |```
                     |{
                     | "title": "goldenHoneyLemon",
                     | "showName": "黃金檸檬蜂蜜",
                     | "desc": "good to drink",
                     | "tags": ["yummy", "sweet", "health"],
                     | "published": true,
                     | "images": "/images/p1.jpg"
                     |}
                     |```
                     |
                     |## Response
                     |
                     ||Name      | Data Type | Query Type | Example                              | Description             |
                     ||-----------|-----------|------------|--------------------------------------|-------------------------|
                     || productId | String    | Body       | 56c6153a-da93-4495-a070-898c09572b6b | unique id for a product |
                     |
                     |## Response Example
                     |
                     |```
                     |{
                     |  "productId": "56c6153a-da93-4495-a070-898c09572b6b"
                     |}
                     |```
                     |
                     |## Response Status Code
                     |
                     |201 Created
                     |202 Accepted
                     |400 Bad Request
                     |500 Internal Server Error
                     |
                     |""".stripMargin)
      .tag("Admin")
      .bodyParam[String]("body", "request body")
      .responseWith(Status.Created.code, Status.Created.reason)
      .responseWith(Status.Accepted.code, Status.Accepted.reason)
      .responseWith(Status.BadRequest.code, Status.BadRequest.reason)
      .responseWith(Status.InternalServerError.code, Status.InternalServerError.reason)
  } { req: CreateProductRequest =>
    createSrv(req).map {
      case a: CreateProductSuccess  => response.created.body(a)
      case CreateProductIdDuplicate => response.accepted
      case c: CreateProductFail     => response.internalServerError.body(c.reason)
    }.run
  }

  // read
  getWithDoc("/admin/api/v1/products") { o =>
    o.summary("Query")
      .description(
        """
          |This API serves to query one or more products information according to query parameter.
          |
          |## Request
          |
          || Name      | Data Type    | Query Type | Example              | Description               | Required |
          ||-----------|--------------|------------|----------------------|---------------------------|----------|
          || title     | String       | Query      | goldenHoneyLemon     | product name abbreviation | N        |
          || productId | String       | Query      | 1111-2222-3333-4444  | product id                | N        |
          || tag       | String       | Query      | yummy                | tag                       | N        |
          || published | Boolean      | Query      | true                 | published or not          | N        |
          |
          |## Response
          |
          || Name       | Data Type           | Query Type | Example      | Description             |
          ||------------|---------------------|------------|--------------|-------------------------|
          || num        | Int                 | Body       | -            | total count |
          || detail     | List[GetProductInfo]  | Body       | -          | detail info |
          |
          |### GetProductInfo
          |
          || Name      | Data Type    | Query Type | Example              | Description               |
          ||-----------|--------------|------------|----------------------|---------------------------|
          || productId | String       | Body       | 1111-2222-3333-4444   | product unique id        |
          || title     | String       | Body       | goldenHoneyLemon     | product name abbreviation |
          || showName  | String       | Body       | 黃金檸檬蜂蜜            | show name                |
          || desc      | String       | Body       | good to drink        | product description       |
          || tags      | String Array | Body       | yummy, sweet, health | tag                       |
          || published | Boolean      | Body       | true                 | published or not          |
          || images    | String       | Body       | /images/p1.jpg       | image path                |
          |
          |
          |## Response Example
          |
          |```
          |{
          |  "result": [
          |    {
          |      "productId": "1111-2222-333-4444",
          |      "title": "goldenHoneyLemon",
          |      "showName": "黃金檸檬蜂蜜",
          |      "desc": "good to drink",
          |      "tags": [
          |        "yummy",
          |        "sweet",
          |        "health"
          |      ],
          |      "published": true,
          |      "images": "/images/p1.jpg"
          |    },
          |    {
          |      "productId": "1111-2222-333-4444",
          |      "title": "sataySauce",
          |      "showName": "沙茶醬",
          |      "desc": "真材實料",
          |      "tags": [
          |        "real",
          |        "good"
          |      ],
          |      "published": true,
          |      "images": "/images/p2.jpg"
          |    }
          |  ]
          |}
          |```
          |
          |""".stripMargin
      )
      .tag("Admin")
      .queryParam[String]("productId", "product id", false)
      .queryParam[String]("title", "abbreviation product name", false)
      .queryParam[String]("tag", "tag", false)
      .queryParam[Boolean]("published", "published flag", false)
      .responseWith(Status.Ok.code, Status.Ok.reason)
      .responseWith(Status.NoContent.code, Status.NoContent.reason)
      .responseWith(Status.BadRequest.code, Status.BadRequest.reason)
      .responseWith(Status.InternalServerError.code, Status.InternalServerError.reason)
  } { req: QueryProductsRequest =>
    querySrv(req).map {
      case a: QueryProductsSuccess => response.ok.body(a)
      case QueryProductEmpty       => response.noContent
      case c: QueryProductFail     => response.internalServerError.body(c.reason)
    }.run
  }

  // update
  putWithDoc("/admin/api/v1/products/:productId") { o =>
    o.summary("Update")
      .description(
        """
          |This API serves to update a specific product.
          |
          |## Request
          |
          || Name      | Data Type    | Query Type | Example              | Description               | Required |
          ||-----------|--------------|------------|----------------------|---------------------------|----------|
          || productId | String       | Route      | 1111-2222-3333-4444  | product id                | Y        |
          || title     | String       | Body       | goldenHoneyLemon     | product name abbreviation | N        |
          || showName  | String       | Body       | 黃金檸檬蜂蜜            | show name                | N        |
          || desc      | String       | Body       | good to drink        | product description       | N        |
          || tags      | String Array | Body       | yummy, sweet, health | tag                       | N        |
          || published | Boolean      | Body       | true                 | published or not          | N        |
          || images    | String       | Body       | /images/p1.jpg       | image path                | N        |
          |
          |
          |## Request Example
          |
          |```
          |{
          | "title": "goldenHoneyLemon",
          | "showName": "黃金檸檬蜂蜜",
          | "desc": "good to drink",
          | "tags": ["yummy", "sweet", "health"],
          | "published": true,
          | "images": "/images/p1.jpg"
          |}
          |```
          |
          |
          |""".stripMargin
      )
      .tag("Admin")
      .routeParam[String]("productId", "product id")
      .bodyParam[String]("body", "request body")
      .responseWith(Status.Ok.code, Status.Ok.reason)
      .responseWith(Status.NoContent.code, Status.NoContent.reason)
      .responseWith(Status.BadRequest.code, Status.BadRequest.reason)
      .responseWith(Status.InternalServerError.code, Status.InternalServerError.reason)
  } { req: UpdateProductsRequest =>
    updateSrv(req).map {
      case UpdateProductsSuccess => response.ok
      case UpdateProductEmpty    => response.noContent
      case c: UpdateProductFail  => response.internalServerError.body(c.reason)
    }.run
  }

  // delete
  deleteWithDoc("/admin/api/v1/products/:productId") { o =>
    o.summary("Delete")
      .tag("Admin")
      .routeParam[String]("productId", "product id")
      .responseWith(Status.Ok.code, Status.Ok.reason)
      .responseWith(Status.NoContent.code, Status.NoContent.reason)
      .responseWith(Status.InternalServerError.code, Status.InternalServerError.reason)
  } { req: DeleteProductsRequest =>
    deleteSrv(req).map {
      case DeleteProductsSuccess => response.ok
      case DeleteProductEmpty    => response.noContent
      case c: DeleteProductFail  => response.internalServerError.body(c.reason)
    }.run
  }
}
