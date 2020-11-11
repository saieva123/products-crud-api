package storeServices.models.http

sealed trait DeleteProductResponse extends Product with Serializable

case object DeleteProductsSuccess extends DeleteProductResponse

case object DeleteProductEmpty extends DeleteProductResponse

final case class DeleteProductFail(reason: String) extends DeleteProductResponse
