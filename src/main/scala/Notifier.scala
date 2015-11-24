import java.util.UUID

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import commands.Command.{NotifyResult, Notify}
import exceptions.Exceptions.NetworkException

/**
 * Created by Dawid Pawlak.
 */
class Notifier extends Actor with ActorLogging{

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case exception:NetworkException => {
      println("Sending notification to remote service has failed. Restarting")
      Restart
    }
  }

  def sendMessage(title: String, buyerPath: ActorPath, currentPrice: Int): Unit = {
    val notifierRequest = context.actorOf(Props(new NotifierRequest(Notify(title, buyerPath, currentPrice))),"notifierRequest" + UUID.randomUUID())
    notifierRequest ! Notify(title, buyerPath, currentPrice)
  }

  override def receive: Receive = {
    case Notify(title, buyerPath, currentPrice) => {
      println("Received in notify")
      sendMessage(title, buyerPath, currentPrice)
    }
    case NotifyResult(title, buyerPath, currentPrice, good) => {
      if(!good) {
        println("Resending message")
        sendMessage(title, buyerPath, currentPrice)
      } else {
        println("Message was good")
      }
    }
  }
}
