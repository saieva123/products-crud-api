package storeServices.models.http

sealed trait CreateProductResponse extends Product with Serializable

final case class CreateProductSuccess(productId: String) extends CreateProductResponse

final case class CreateProductFail(reason: String) extends CreateProductResponse

case object CreateProductIdDuplicate extends CreateProductResponse
