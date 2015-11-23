package remote_actors

import akka.actor.Actor
import commands.Command.Notify

/**
 * Created by Dawid Pawlak.
 */
class AuctionPublisher extends Actor{
  override def receive: Receive = {
    case Notify(title, buyerPath, currentPrice) => {
      println("Remote actor received information. Auction with title " + title + " has current price " + currentPrice)
    }
  }
}
