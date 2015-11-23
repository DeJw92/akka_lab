import commands.Command
import Command.{CreateBid, CreateAuctions}
import akka.actor.{ActorSystem, Props}

/**
 * Created by Dawid Pawlak.
 */
object MainApp extends App{

  val system = ActorSystem("system")

  val auctionSearch = system.actorOf(Props[AuctionSearch],"auctionSearch")

  val seller = system.actorOf(Props[Seller],"seller")

  seller ! CreateAuctions(List[(String,Int)](("BMW M5", 30), ("Lenovo E430",600)))

  val GO = readLine()
  val buyer1 = system.actorOf(Props(new Buyer(1000,100)),"buyer1")
  val buyer2 = system.actorOf(Props(new Buyer(1000,100)),"buyer2")
  buyer1 ! CreateBid("Lenovo")
  buyer2 ! CreateBid("Lenovo")
}
