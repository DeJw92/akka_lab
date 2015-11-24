import java.util.concurrent.ThreadLocalRandom

import akka.actor.Actor
import commands.Command.{NotifyResult, Notify}
import exceptions.Exceptions.NetworkException

/**
 * Created by Dawid Pawlak.
 */
class NotifierRequest(message:Notify) extends Actor{

  override def preStart(): Unit = {
    println("Pre started called")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    self ! message.get
    println("Resend message")
  }

  override def receive: Receive = {
    case Notify(title,buyer,currentPrice) => {
      println("Receive notification. Auction with title " + message.title + " has current price " + message.currentPrice)
      val auctionPublisher = context.actorSelection("akka.tcp://AuctionSystem@127.0.0.1:2552/user/auctionPublisher")

      if(ThreadLocalRandom.current().nextDouble() < 0.4) {
        println("Remote service not available")
        throw new NetworkException
      }
      println("Now available")
      auctionPublisher ! Notify(message.title, message.buyer, message.currentPrice)
      context.stop(self)
      context.parent ! NotifyResult(message.title, message.buyer, message.currentPrice, good = true)
    }
  }
}
