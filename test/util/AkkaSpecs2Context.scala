package util

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.specs2.mutable.After

// http://blog.xebia.com/2012/10/01/testing-akka-with-specs2/
abstract class AkkaSpecs2Context extends TestKit(ActorSystem()) with After with ImplicitSender {
  def after = system.shutdown()
}
