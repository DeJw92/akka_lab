import commands.Command
import Command.{FindAuctions, SearchResult}
import akka.actor.{ActorLogging, ActorRef, Actor, ActorPath}
import akka.event.LoggingReceive

import scala.collection.mutable.ListBuffer

/**
 * Created by Dawid Pawlak.
 */
class AuctionSearch extends Actor with ActorLogging{

  var auctions:ListBuffer[(String, ActorPath)] = ListBuffer()

  def receive = LoggingReceive {
    case Command.RegisterForSearch(auction:String, auctionPath:ActorPath) => {
      log.info("Auction " + auction + " registered in search")
      auctions = auctions :+ (auction, auctionPath)
    }
    case FindAuctions(title) => {
      log.info("Looking for " + title + " in search")
      var result:ListBuffer[ActorPath] = ListBuffer()
      for(element <- auctions) {
        if(element._1.toLowerCase.indexOf(title.toLowerCase) != -1) {
          result = result :+ element._2
        }
      }
      sender ! SearchResult(title, result.toList)
    }
  }
}
