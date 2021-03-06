package services.connectors

/**
  * Created by soh-l on 08/11/2016.
  */

import java.util.Properties

import com.typesafe.config.ConfigFactory
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import play.api.Logger
import play.api.libs.json.Json
import services.models.Tick

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Produces messages to kafka topic defined by config "producer.topic.name.errorHandling"
  */

object StreamProducer {

  val CHARSET = "UTF-8"
  lazy val CONF = ConfigFactory.load
  lazy val TOPIC = CONF.getString("kafka.topic.name.stream")
  lazy val BROKER_LIST = CONF.getString("producer.metadata.broker.list")
  lazy val BROKER_LIST_PARSED: List[String] = parseBrokersToString
  lazy val BROKER_LIST_PARSED_HP: List[(String, Int)] = parseBrokersToHostPort


  // http://kafka.apache.org/documentation.html#producerconfigs
  // "serializer.class" default is kafka.serializer.DefaultEncoder Array[Byte] to Array[Byte]
  val props = new Properties()
  props.put("metadata.broker.list", BROKER_LIST)
  props.put("request.required.acks", "1") // 1 is leader received
  props.put("producer.type", "sync")
  props.put("compression.codec", "none")
  props.put("message.send.max.retries", "3")

  val PRODUCER = new Producer[AnyRef, AnyRef](new ProducerConfig(props))

  def parseBrokersToString: List[String] = {
    val all = BROKER_LIST.split(",")
    all.map(_.trim).toList
  }

  def parseBrokersToHostPort: List[(String, Int)] = {
    val r = BROKER_LIST_PARSED.map { s =>
      val hp = s.split(":")
      (hp(0), hp(1).toInt)
    }
    r.toList
  }

  /**
    * Converts to json and calls produce(message: String)
    *
    * @param tick
    */
  def produce(tick: Tick): Future[Boolean] = {
    val message = Json.stringify(Json.toJson(tick))
    produce(tick.ts.toString, message)
  }

  /**
    * Calls PRODUCER.send(new KeyedMessage(TOPIC, message.getBytes(CHARSET)))
    *
    * @param key     as String
    * @param message assumes verified json
    */
  def produce(key: String, message: String): Future[Boolean] = {
    if (Logger.isDebugEnabled) Logger.debug("producing " + key + " " + message)
    val km: KeyedMessage[AnyRef, AnyRef] =
      new KeyedMessage(TOPIC, key.getBytes(CHARSET), message.getBytes(CHARSET))
    send(message, km)
  }

  /**
    * Produce just message with no key.  Key will be null upon consume
    *
    * @param message
    * @return
    */
  def produce(message: String): Future[Boolean] = {
    if (Logger.isDebugEnabled) Logger.debug("producing " + message)
    val km: KeyedMessage[AnyRef, AnyRef] = new KeyedMessage(TOPIC, message.getBytes(CHARSET))
    send(message, km)
  }

  /**
    *
    * @param message
    * @param km
    * @return
    */
  def send(message: String, km: KeyedMessage[AnyRef, AnyRef]): Future[Boolean] = {
    Future {
      try {
        PRODUCER.send(km)
        true
      } catch {
        case t: Throwable => {
          Logger.error("Failed to send " + message, t)
          false
        }
      }
    }
  }

}

