import argonaut._, Argonaut._

object ImplicitConversion {

  // data model based on http://slick.typesafe.com/doc/2.1.0/orm-to-slick.html
  case class Person (id: Int,name: String,age: Int,addressId:Int)
  case class Address (id: Int, street: String,city: String)

  case class MetaModelDefinition(mClasses: String, mReferences:String, mEnums: String)


  implicit def MetaModelDefinitionJson : DecodeJson[MetaModelDefinition] =
    DecodeJson(c => for {
      name <- (c --\ "Class").as[String]

    } yield MetaModelDefinition(mClasses = null,mReferences = null,mEnums = null))


  // no direct reference, to fit with slick database models
  case class PersonWithAddress(person: Person, address: Address)

  // implicit conversion with argonaut
  implicit def PersonAddressEncodeJson: EncodeJson[PersonWithAddress] =
    EncodeJson((p: PersonWithAddress) =>
      ("id" := p.person.id) ->:
        ("name" := p.person.name) ->:
        ("age" := p.person.age) ->:
        ("address" := Json (
          ("id" := p.address.id),
          ("street" := p.address.street),
          ("city" := p.address.city)
        )
          ) ->: jEmptyObject)

  implicit def PersonAddressDecodeJson: DecodeJson[PersonWithAddress] =
    DecodeJson(c => for {

      id <- (c --\ "id").as[Int]
      name <- (c --\ "name").as[String]
      age <- (c --\ "age").as[Int]
      address <- (c --\ "address").as[Json]

      // extract data from address
      addressid <- (address.acursor --\ "id").as[Int]
      street <- (address.acursor --\ "street").as[String]
      city <- (address.acursor --\ "city").as[String]

    } yield PersonWithAddress(Person(id, name, age= 12, addressid), Address(addressid, street, city)))

  def main(args: Array[String]) {
    // running a sample
   // val person = Person(0, "John Rambo" , 67, 0)
    //val address = Address(0, "101 W Main St", "Madison, Kentucky")
    //val pa = PersonWithAddress(person, address)

    // convert the person to json
    //val json = pa.asJson

    //val json = """{"address":{"id":0,"street":"101 W Main St","city":"Madison, Kentucky"},"age":67,"name":"John Rambo","id":0}"""
    val json = """{"Class": {"Test"}}"""

    val content = json

    println (content)

    // we should get a person instance here
    //var padecoded : PersonWithAddress = content.decodeOption[PersonWithAddress].get
    var padecoded : MetaModelDefinition = content.decodeOption[MetaModelDefinition].get

    println (padecoded)
  }
}