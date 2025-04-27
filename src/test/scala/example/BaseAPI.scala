package example

import io.gatling.core.Predef._
import io.gatling.core.feeder._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.request.builder.HttpRequestBuilder

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

trait BaseAPI {

  def authenticate_token(): ChainBuilder = {
    exec(http("T00_DafToken_Generate")
      .post("/authenticate")
      .body(RawFileBody("data/token.json"))
      .check(jsonPath("$.token").saveAs("Token"))
      // .check(jmesPath("token").saveAs("Token"))
    ).pause("3")
  }

  //--------------- Iterator----------
  //()
  //    val idNumber : Iterator[Int]=(1 to 10).iterator
  //   val customfeederid: Iterator[Map[String, Int]] = Iterator.continually(Map("id" -> idNumber.next()))
  //
  //
  //  def get_selfparamvideo(): ChainBuilder = {
  //    feed(customfeederid)
  //      .exec(
  //        http("T05_Get customfeederid One Case API for #{id}")
  //          .get(s"/videogame/#{id}")
  //          .check(status.is(200))
  //      )
  //  }
  val jsonfeeder: Feeder[Any] = jsonFile("data/postbody2.json").circular()

  def create_jsonvideo(): ChainBuilder = {
    feed(jsonfeeder).exec(http("T02_Create Case API JSON")
      .post("/videogame")
      .header("Authorization", "Bearer #{Token}")
      .body(RawFileBody("data/postbody.json")).asJson
      .check(jsonPath("$.name").is("#{name}"),
        bodyString.saveAs("responsebody"))

    )
  }
//
//  //assign default value
  def create_vide0(requestBodyFileName: String): ChainBuilder = {
    exec(http("T02_Create Case API")
      .post("/videogame")
      .header("Authorization", "Bearer #{Token}")
      .body(RawFileBody(requestBodyFileName)).asJson
      .check(jsonPath("$.name").is("Mario"),
        bodyString.saveAs("responsebody"))

    )
      .exec {
        session =>
          println("Session is ---------> " + session);
          println("Session responsebody is ---------> " + session("responsebody").as[String]);
          session
      }
  }
//  //exec(flushHttpCache)
//  //      .exec(flushCookieJar)
//  //      .exec(session=> session.set("pause","3"))
//
  def get_video(needrepeat: Boolean = false, count: Int = 0): ChainBuilder = {
    var request = http("T01_Get Case API")
      .get("/videogame")
      .check(status.is(200))
      .check(jsonPath("$[0].name").is("Resident Evil 4"))
      .check(jsonPath("$[2].category").is("Puzzle"))
    repeatcoderequest(request, needrepeat, count)
      .pause(3)
  }
//
  var csvfeeder: Feeder[Any] = csv("data/id.csv").circular()

  def get_onevideo(UserID: Int, needrepeat: Boolean = false, count: Int = 0): ChainBuilder = {
    var request2 = http("T01_Get One Case API")
      .get(s"/videogame/$UserID")
      .check(status.is(200))
    repeatcoderequest(request2, needrepeat, count)
  }
//
  def get_onevideoCSV(): ChainBuilder = {
    feed(csvfeeder)
      .exec(
        http("T05_Get CSV One Case API for #{id}")
          .get(s"/videogame/#{id}")
          .check(status.is(200))
      )
  }
//
  val customfeeder: BatchableFeederBuilder[String] = separatedValues("data/id.txt", '#')
//
  def get_onevideoCustom(): ChainBuilder = {
    feed(customfeeder)
      .exec(
        http("T06_Get Custom CSV One Case API for #{id}")
          .get(s"/videogame/#{id}")
          .check(status.is(200))
      )
  }
//
  val arrayfeeder: Feeder[Any] = array2FeederBuilder(Array(
    Map("id" -> 2, "name" -> "ok"),
    Map("id" -> 3, "name" -> "ok")
  )).circular()

  def get_onevideoarray(): ChainBuilder = {
    feed(arrayfeeder)
      .exec(
        http("T06_Get Custom array One Case API for #{id}")
          .get(s"/videogame/#{id}")
          .check(status.is(200))
      )
  }
//
  val indexsequesncefeeder: Feeder[Any] = IndexedSeq(
    Map("id" -> 2, "name" -> "ok"),
    Map("id" -> 3, "name" -> "ok")
  ).circular()

  def get_onevideoindexsequesnce(): ChainBuilder = {
    feed(indexsequesncefeeder)
      .exec(
        http("T06_Get indexsequesnce array One Case API for #{id}")
          .get(s"/videogame/#{id}")
          .check(status.is(200))
      )
  }
//
  def repeatcoderequest(request: HttpRequestBuilder, needrepeating: Boolean = false, count: Int = 0): ChainBuilder = {
    if (needrepeating) {
      repeat(count) {
        exec(request)
      }
    }
    else {
      exec(request)
    }
  }
//
//
//  //------------- random
//
  val random = new Random()
//
  def randomString(length: Int): String = {
    random.alphanumeric.filter(_.isLetter).take(length).mkString
  }
//
  def randomNumber(length: Int): Int = {
    random.nextInt(math.pow(10, length).toInt)
}
    def randomDate(): String = {
      LocalDate.now().minusDays(random.nextInt(30)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
//
//
    val customFeederPost: Iterator[Map[String, _]] = Iterator.continually(Map(
      "category" -> s"${randomString(7)}",
      "name" -> s"${randomString(5)}",
      "rating" -> s"${randomString(6)}",
      "releaseDate" -> s"${randomDate()}",
      "reviewScore" -> s"${randomNumber(3)}"

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
        bodyString.saveAs("responebodypost")
      )
    )
      .exec{
        session => println("Postbody is -----"+session("responebodypost").as[String]);
          session
      }

  }




  }