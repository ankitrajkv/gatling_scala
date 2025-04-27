package example

import com.github.javafaker.Faker
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure._
import io.gatling.http.protocol.HttpProtocolBuilder

import java.util.concurrent.TimeUnit
import java.time.ZoneId

class Final_Code extends Simulation {

  val httpprotocol: HttpProtocolBuilder = http
    .baseUrl("https://videogamedb.uk:443/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  def authenticate_token(): ChainBuilder = {
    exec(http("T00_DafToken_Generate")
      .post("/authenticate")
      .body(RawFileBody("data/token.json"))
      .check(jsonPath("$.token").saveAs("Token"))
    )
  }

  val faker = new Faker()

  val customFeederPost: Iterator[Map[String, _]] = Iterator.continually(Map(
    "category" -> faker.commerce().department(),
    "name" -> faker.name().fullName(),
    "rating" -> faker.number().numberBetween(1,10),
    "releaseDate" -> faker.date().past(10000, TimeUnit.DAYS).toInstant.atZone(ZoneId.systemDefault).toLocalDate.toString,
    "reviewScore" -> faker.number().numberBetween(1,100)
  ))

  def create_customfeedvideo(): ChainBuilder = {
    feed(customFeederPost)
      .exec(http("T02_Create create_customfeedvideo Case API JSON")
        .post("/videogame")
        .header("Authorization", "Bearer #{Token}")
        .body(StringBody(session =>
          s"""{
             |  "category": "${session("category").as[String]}",
             |  "name": "${session("name").as[String]}",
             |  "rating": "${session("rating").as[String]}",
             |  "releaseDate": "${session("releaseDate").as[String]}",
             |  "reviewScore": "${session("reviewScore").as[String]}"
             |}""".stripMargin)).asJson
        .check(
          status.is(200),
          bodyString.saveAs("responsebodypost")
        )
      )
      .exec { session =>
        println("Postbody is -----" + session("responsebodypost").as[String])
        session
      }
  }

  def create_customfeedjsonvideo(): ChainBuilder = {
    feed(customFeederPost)
      .exec(http("T02_Create create_customfeedvideo Case API JSON")
        .post("/videogame")
        .header("Authorization", "Bearer #{Token}")
        .body(ElFileBody("data/creatcasebody.json")).asJson
        .check(
          status.is(200),
          bodyString.saveAs("responsebodypost")
        )
      ).pause(10)
      .exec { session =>
        println("Postbody is -----" + session("responsebodypost").as[String])
        session
      }
  }


  var finalscn: ScenarioBuilder = scenario("LOAD API 2")
    .exec(authenticate_token())
    //.exec(create_customfeedvideo())
    .exec(create_customfeedjsonvideo())

  setUp(


    finalscn.inject(
      rampUsers(5).during(60), // Ramp-up 5 users over 60 seconds
      constantUsersPerSec(0.25).during(900), // 0.25 TPS (1 transaction every 4 seconds) for 1 hour (3600 seconds)
      rampUsers(5).during(60) // Ramp-down 5 users over 60 seconds
    )
  ).maxDuration(1020) // Total duration: 1 hour + 2 minutes ramp-up/down
    .protocols(httpprotocol)




}
