package my.test

case class Film(
    title: String,
    description: String,
    language: String,
    length: Int,
    releaseYear: Int
)

case class User(
    user: String,
    name: String
)

case class RequestTwo(
    ccy: String,
    tenor: String
)
