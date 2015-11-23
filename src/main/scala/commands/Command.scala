package commands

import akka.actor.ActorPath

/**
 * Created by Dawid Pawlak.
 */
object Command {
  case class CreateAuctions(auctions:List[(String, Int)])
  case class RegisterForSearch(auction:String, auctionPath:ActorPath)
  case class StartAuction(duration:Int)
  case class CreateBid(title:String)
  case class FindAuctions(title:String)
  case class SearchResult(title:String, auctions:List[ActorPath])
  case class Bid(maxAmount:Int, step:Int)
  case object Expire
  case class AuctionFinished(title:String)
  case class Win(title:String, cost:Int)
  case class NewBid(auctionPath:ActorPath, bid:Int)
}
