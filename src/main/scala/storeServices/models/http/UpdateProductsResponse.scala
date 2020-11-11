package storeServices.models.http

sealed trait UpdateProductResponse extends Product with Serializable

case object UpdateProductsSuccess extends UpdateProductResponse

case object UpdateProductEmpty extends UpdateProductResponse

final case class UpdateProductFail(reason: String) extends UpdateProductResponse
