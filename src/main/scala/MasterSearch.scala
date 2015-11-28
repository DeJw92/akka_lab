import akka.actor.{Actor, Props}
import akka.event.LoggingReceive
import akka.routing.{ActorRefRoutee, BroadcastRoutingLogic, RoundRobinRoutingLogic, Router}
import commands.Command.{FindAuctions, RegisterForSearch}

/**
 * Created by Dawid Pawlak.
 */
class MasterSearch extends Actor{

  val numberOfRoutes:Int = 3

  val routes = Vector.fill(numberOfRoutes) {
    val route = context.actorOf(Props[AuctionSearch])
    context watch route
    ActorRefRoutee(route)
  }

  val searchRouter = {
    Router(RoundRobinRoutingLogic(), routes)
  }

  val registerRouter = {
    Router(BroadcastRoutingLogic(), routes)
  }


  override def receive = LoggingReceive {
    case register:RegisterForSearch => {
      registerRouter.route(register, sender())
    }
    case find:FindAuctions => {
      searchRouter.route(find, sender())
    }
  }
}
