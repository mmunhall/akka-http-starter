package com.canoeventures.sheringham.server

import java.util.concurrent.TimeUnit

import akka.actor.{ActorContext, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config

import scala.concurrent.duration.FiniteDuration

object Settings extends ExtensionId[Settings] with ExtensionIdProvider {
  override def lookup = Settings
  override def createExtension(system: ExtendedActorSystem) = new Settings(system.settings.config, system)

  def apply(context: ActorContext): Settings = apply(context.system)
}

class Settings(config: Config, extendedSystem: ExtendedActorSystem) extends Extension {
  object Http {
    val Port = config.getInt("sheringham.http.port")
    val Interface = config.getString("sheringham.http.interface")
  }

  val askTimeout = FiniteDuration(config.getDuration("sheringham.ask-timeout", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
}
