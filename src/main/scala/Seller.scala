import akka.persistence.{RecoveryCompleted, PersistentActor}
import commands.Command
import commands.Command.{AuctionFinished, StartAuction}
import akka.actor.{Props}
import akka.event.LoggingReceive
import events.Event.AuctionCreatedEvent

/**
 * Created by Dawid Pawlak.
 */
class Seller extends PersistentActor {

  def receiveCommand = LoggingReceive {
    case Command.CreateAuctions(auctions:List[(String,Int)]) => {
      for(element <- auctions) {
        val auctionID:String = "auction" + java.util.UUID.randomUUID()
        val auction = context.actorOf(Props(new Auction(element._1)), auctionID)
        auction ! StartAuction(element._2)
        println("Saving auction")
        persist(AuctionCreatedEvent(auctionID,element._1)) {
          event => {}
        }
      }
    }
    case AuctionFinished(title) => {
      println("Seller informed that auction with title " + title + " has been finished");
    }

  }

  override def receiveRecover =  LoggingReceive {
    case AuctionCreatedEvent(auctionID, title) => {
      val auction = context.actorOf(Props(new Auction(title)),auctionID)
    }
    case RecoveryCompleted => {
      println("Seller has been recovered")
    }
  }

  override def persistenceId: String = "persistent-seller" + self.path.name
}
