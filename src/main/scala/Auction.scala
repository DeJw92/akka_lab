import akka.actor.ActorPath
import akka.event.LoggingReceive
import akka.persistence.{PersistentActor, RecoveryCompleted}
import commands.Command._
import events.Event._

import scala.concurrent.duration.Duration

/**
 * Created by Dawid Pawlak.
 */
class Auction(title:String) extends PersistentActor{

  import context._

  var creatorPath:ActorPath = null

  var bestBid:Int = 0

  var bestBuyer:ActorPath = null

  var startTime:Long = 0

  var duration:Int = -1

  var involvedBuyers:Set[ActorPath] = Set()

  def sendInformationToNotifier() = {
    val notifier = context.actorSelection("/user/notifier")
    notifier ! Notify(title, bestBuyer, bestBid)
  }

  def updateState(event:NewBestBid): Unit = {
    bestBid = event.amount
    bestBuyer = event.buyerPath
    involvedBuyers += event.buyerPath
    for(buyer <- involvedBuyers) {
      if(buyer != bestBuyer) {
        val buyerRef = system.actorSelection(buyer)
        buyerRef ! NewBid(self.path, bestBid)
      }
    }
    context become activated
    sendInformationToNotifier()
    println("Auction " + title + " has new best bid " + bestBid)
  }

  def ignored: Receive = LoggingReceive {
    case _ => {
      println("Unfortunately auction with title " + title + " is no longer available")
    }
  }

  def sold: Receive = LoggingReceive {
    case _ => {}
    println("Auction " + title + " has been sold")
  }

  def finishAuction(event: FinishedAuction): Unit = {
    event match {
      case Sold => {
        context become sold
      }
      case NotSold => {
        context become ignored
      }
    }
  }

  def activated: Receive = LoggingReceive {
    case Bid(maxAmount:Int, step:Int) => {
      if(bestBid  < maxAmount) {
        val newValue = math.min(bestBid+step, maxAmount)
        persist(NewBestBid(newValue, sender().path)) {
          event => updateState(event)
        }
      }
    }
    case Expire => {
      informSellerAboutSoldAuction
      val buyer = context.actorSelection(bestBuyer)
      buyer ! Win(title, bestBid)
      println("Auction with " + title + " has expired and someone won")
      persist(finishAuction(Sold)) {
        event => {}
      }
      context become sold
    }
  }

  def informSellerAboutSoldAuction: Unit = {
    val seller = context.actorSelection(creatorPath)
    seller ! AuctionFinished(title)
  }

  def created: Receive = LoggingReceive {

    case Expire => {
      context become ignored
      println("Auction with title " + title + " has finished and no one has won")
      informSellerAboutSoldAuction
      persist(finishAuction(NotSold)) {
        event => {}
      }
    }

    case Bid(maxAmount:Int, step:Int) => {
        persist(NewBestBid(step, sender().path)) {
          event => updateState(event)
        }
    }
  }

  def init(event:CreatedEvent): Unit = {
    println("In init method")
    this.creatorPath = event.creatorPath
    val masterSearch = context.actorSelection("/user/masterSearch")
    masterSearch ! RegisterForSearch(title, self.path)
    this.duration = event.duration
    this.startTime = event.startTime
    context become created
  }


  override def receiveRecover = LoggingReceive  {
    case event:CreatedEvent => {
      println("Recovering event")
      init(event)
    }
    case event:NewBestBid => {
      println("Bid recover")
      updateState(event)
    }
    case RecoveryCompleted => {
      if(duration != -1) {
        val remainingTime = math.max(duration - ((System.currentTimeMillis() - startTime) / 1000),0)
        println("Starting timer. Auction " + title + " will last for " + remainingTime + " seconds")
        context.system.scheduler.scheduleOnce(Duration(remainingTime,"seconds"),self,Expire)
      }
      println("Auctions with title " + title + " has been recovered")
    }
  }

  override def receiveCommand = LoggingReceive {
    case StartAuction(x) => persist(CreatedEvent(sender().path, System.currentTimeMillis(), x)){
        event => {
          println("Scheduler for auction " + title + " has been set to " + event.duration + " seconds ")
          context.system.scheduler.scheduleOnce(Duration(event.duration,"seconds"),self,Expire)
          init(event)
        }
    }
  }

  override def persistenceId: String = "persistent-auction" + self.path.name
}
