package remote_actors

import akka.actor.{ActorLogging, Actor}
import commands.Command.Notify

/**
 * Created by Dawid Pawlak.
 */
class AuctionPublisher extends Actor with ActorLogging{
  override def receive: Receive = {
    case Notify(title, buyerPath, currentPrice) => {
    log.info("Remote actor received information. Auction with title " + title + " has current price " + currentPrice)
    }
  }
}
