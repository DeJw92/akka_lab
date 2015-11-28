import com.typesafe.config.ConfigFactory
import commands.Command
import Command.{CreateBid, CreateAuctions}
import akka.actor.{ActorSystem, Props}
import remote_actors.AuctionPublisher

/**
 * Created by Dawid Pawlak.
 */
object MainApp extends App{

  val config = ConfigFactory.load()

  val auctionSystem = ActorSystem("AuctionSystem", config.getConfig("auctionSystem").withFallback(config))

  val auctionPublisherSystem = ActorSystem("AuctionSystem", config.getConfig("auctionPublisherSystem").withFallback(config))

  val auctionPublisher = auctionPublisherSystem.actorOf(Props[AuctionPublisher],"auctionPublisher")

  val masterSearch = auctionSystem.actorOf(Props[MasterSearch],"masterSearch")

  val notifier = auctionSystem.actorOf(Props[Notifier],"notifier")

  val seller = auctionSystem.actorOf(Props[Seller],"seller")

  seller ! CreateAuctions(List[(String,Int)](("BMW M5", 30), ("Lenovo E430",600)))

  val GO = readLine()
  val buyer1 = auctionSystem.actorOf(Props(new Buyer(1000,100)),"buyer1")
//  val buyer2 = auctionSystem.actorOf(Props(new Buyer(1000,100)),"buyer2")
//  val buyer3 = auctionSystem.actorOf(Props(new Buyer(10000,15)),"buyer3")
  buyer1 ! CreateBid("Lenovo")
//  buyer2 ! CreateBid("Lenovo")
//  buyer3 ! CreateBid("Lenovo")
}
