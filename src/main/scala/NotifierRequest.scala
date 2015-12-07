import java.util.concurrent.ThreadLocalRandom

import akka.actor.{ActorLogging, Actor}
import commands.Command.{NotifyResult, Notify}
import exceptions.Exceptions.NetworkException

/**
 * Created by Dawid Pawlak.
 */
class NotifierRequest(message:Notify) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info("Pre started called")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    self ! message.get
    log.info("Resend message")
  }

  override def receive: Receive = {
    case Notify(title,buyer,currentPrice) => {
      log.info("Receive notification. Auction with title " + message.title + " has current price " + message.currentPrice)
      val auctionPublisher = context.actorSelection("akka.tcp://AuctionSystem@127.0.0.1:2552/user/auctionPublisher")

      if(ThreadLocalRandom.current().nextDouble() < 0.4) { // simulate problems with connection
        log.info("Remote service not available")
        throw new NetworkException
      }
      log.info("Now available")
      auctionPublisher ! Notify(message.title, message.buyer, message.currentPrice)
      context.stop(self)
      context.parent ! NotifyResult(message.title, message.buyer, message.currentPrice, good = true)
    }
  }
}
