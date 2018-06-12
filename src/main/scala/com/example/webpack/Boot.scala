package com.example.webpack

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.scalalogging.StrictLogging
import io.buddho.akka.http.marshalling.PlayTwirlMarshaller._
import pureconfig.loadConfigOrThrow

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class Boot {

  private implicit val system: ActorSystem = ActorSystem("test")
  private implicit val ec: ExecutionContext = system.dispatcher
  private implicit val mat: Materializer = ActorMaterializer()

  private def route(config: ServiceConfig): Route = {
    pathPrefix("assets") {
      if (config.dev) {
        getFromBrowseableDirectory("target/webpack/output")
      } else {
        getFromResourceDirectory("public")
      }
    } ~
    (pathEndOrSingleSlash | path("index.html")) {
      get {
        complete(html.index.render())
      }
    }
  }

  def start(config: ServiceConfig): Future[ServerBinding] = {
    Http().bindAndHandle(route(config), config.interface, config.port)
  }
}


object Boot extends App with StrictLogging {

  import ExecutionContext.Implicits.global

  val config = loadConfigOrThrow[Config]

  val binding: Future[ServerBinding] = new Boot().start(config.service)

  binding.onComplete {
    case Success(b) =>
      logger.info(s"Server started on ${b.localAddress}")
      sys.addShutdownHook {
        b.unbind()
        logger.info("Server stopped")
      }
    case Failure(e) =>
      logger.error(s"Cannot start server.", e)
      sys.addShutdownHook {
        logger.info("Server stopped")
      }
  }
}
