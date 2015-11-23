package events

import akka.actor.ActorPath

/**
 * Created by Dawid Pawlak.
 */
object Event {
  case class CreatedEvent(creatorPath:ActorPath, startTime:Long, duration:Int)
  case class AuctionCreatedEvent(auctionID: String, title:String)
  case class NewBestBid(amount:Int, buyerPath:ActorPath)

  sealed trait FinishedAuction
  case object NotSold extends FinishedAuction
  case object Sold extends FinishedAuction
}
