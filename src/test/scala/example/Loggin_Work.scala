package example

// required for Gatling core structure DSL
import io.gatling.core.Predef._
import io.gatling.core.structure
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.core.structure.ScenarioBuilder

// required for Gatling HTTP DSL
import io.gatling.http.Predef._

// can be omitted if you don't use jdbcFeeder
// used for specifying durations with a unit, eg "5 minutes"

class Loggin_Work extends Simulation with BaseAPI {

  //Http protocol builder
  val httpprotocol: HttpProtocolBuilder =http
    .baseUrl("https://videogamedb.uk:443/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  //Chainbuilder
  var scn3 : ScenarioBuilder= scenario("LOAD API")
    //.feed(customFeederPost)
    .exec(authenticate_token())
    .exec(create_customfeedvideo())


var scn : ScenarioBuilder= scenario("LOAD API")
  .exec(authenticate_token())
  .exec(create_vide0("data/postbody.json"))
  .exec(create_jsonvideo())
  .exec(get_onevideoCustom())
  .exec(get_onevideoarray())
  //.exec(get_selfparamvideo())



  var scn1 : ScenarioBuilder= scenario("LOAD API 2")
    .exec(get_video(true,3))
    .exec(get_onevideo(2))
    .exec(get_onevideoCSV())
    .exec(get_onevideoindexsequesnce())


    setUp(
     // scn.inject(atOnceUsers(1)),
    // scn1.inject(atOnceUsers(1)),
      scn3.inject(atOnceUsers(2))

    ).protocols(httpprotocol)



}
