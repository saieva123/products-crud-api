package storeServices.models.http

sealed trait QueryProductResponse extends Product with Serializable

final case class QueryProductsSuccess(
    num: Int,
    detail: List[QueryProductInfo]
) extends QueryProductResponse

final case class QueryProductInfo(
    productId: String,
    title: String,
    showName: String,
    desc: Option[String],
    tags: List[String],
    published: Boolean,
    images: Option[String]
)

case object QueryProductEmpty extends QueryProductResponse

final case class QueryProductFail(reason: String) extends QueryProductResponse
