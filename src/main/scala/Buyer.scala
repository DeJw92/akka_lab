import akka.actor.{ActorLogging, Actor, ActorPath}
import akka.event.LoggingReceive
import commands.Command._

/**
 * Created by Dawid Pawlak.
 */
class Buyer(maxAmount:Int, step:Int) extends Actor with ActorLogging {
   def receive =  LoggingReceive  {
     case CreateBid(title:String) => {
       val masterSearch = context.actorSelection("/user/masterSearch")
       masterSearch ! FindAuctions(title)
     }

     case SearchResult(title, auctions:List[ActorPath]) => {
       for(auctionPath <- auctions) {
         val auctionRef = context.actorSelection(auctionPath)
         auctionRef ! Bid(maxAmount, step)
       }
     }
     case Win(title, cost) => {
       log.info("Buyer " + self.path.name + " won auction with title " + title)
     }
     case NewBid(auctionPath:ActorPath,bid:Int) => {
       log.info("Actor " + self.path.name + " has been informed about new bid = " + bid)
       if(bid < maxAmount) {
         val auction = context.actorSelection(auctionPath)
         auction ! Bid(maxAmount, step)
       }
     }
   }
}
