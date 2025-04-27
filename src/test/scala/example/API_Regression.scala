package example
// required for Gatling core structure DSL
import io.gatling.core.Predef._
import io.gatling.core.protocol.Protocol
import io.gatling.core.structure
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.javaapi.core.ScenarioBuilder

// required for Gatling HTTP DSL
import io.gatling.http.Predef._
import scala.util.matching.Regex

// can be omitted if you don't use jdbcFeeder
import io.gatling.jdbc.Predef._

// used for specifying durations with a unit, eg "5 minutes"
import scala.concurrent.duration._
class API_Regression extends Simulation{



  //Http protocol builder
  val httpprotocol: HttpProtocolBuilder =http
    .baseUrl("https://videogamedb.uk:443/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  //Chainbuilder

  //val chainbuilder : ChainBuilder =

  //scenariobuilder

  val authenticate : structure.ScenarioBuilder = scenario("Authenticate")
    .exec(http("T00_DafToken_Generate")
      .post("/authenticate")
      .body(RawFileBody("data/token.json"))
      .check(jsonPath("$.token").saveAs("Token"))
     // .check(jmesPath("token").saveAs("Token"))
    ).pause("3")
.exec(http("T02_Create Case API")
    .post("/videogame")
    .header("Authorization","Bearer #{Token}")
    .body(RawFileBody("data/postbody.json")).asJson
    .check(jsonPath("$.name").is("Mario"))
  )

  val getAllVideo : structure.ScenarioBuilder = scenario("GET API")
    .exec(flushHttpCache)
    .exec(flushCookieJar)
    .exec(session=> session.set("pause","3"))
    .exec(http("T01_Get Case API")
      .get("/videogame")
    .check(status.is(200))
    .check(jsonPath("$[0].name").is("Resident Evil 4"))
    .check(jsonPath("$[2].category").is("Puzzle")))
    .pause("#{pause}")






    setUp(
      getAllVideo.inject(atOnceUsers(1)),
      authenticate.inject(atOnceUsers(1)),

    ).protocols(httpprotocol)



}
