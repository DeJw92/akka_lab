import akka.actor.Actor
import commands.Command.Notify

/**
 * Created by Dawid Pawlak.
 */
class Notifier extends Actor{
  override def receive: Receive = {
    case Notify(title, buyerPath, currentPrice) => {
      println("Receive notification. Auction with title " + title + " has current price " + currentPrice)
      val auctionPublisher = context.actorSelection("akka.tcp://AuctionSystem@127.0.0.1:2552/user/auctionPublisher")

      auctionPublisher ! Notify(title, buyerPath, currentPrice)
    }
  }
}
