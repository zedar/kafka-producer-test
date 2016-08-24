package deepbi.events;

import com.advertine.deep.model.DeepBiEvent;
import com.google.protobuf.ByteString;
import jdk.nashorn.internal.objects.NativeJSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.easygson.JsonEntity;
import person.PersonOuterClass.*;

public class DeepBiProducer {
  public static void main(String[] args) {
    // Define kafka broker properties
    Properties properties = new Properties();
    properties.put("bootstrap.servers", "localhost:9092");
    properties.put("zookeeper.connect", "localhost:2181");
    properties.put("client.id", "DeepBiProducer");
    properties.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    //properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

    KafkaProducer<Integer, byte[]> producer = new KafkaProducer<>(properties);
    IntStream.range(1, 11).forEach(i -> {
      int id = (i%2 == 0) ? i+200 : i+100;
      DeepBiEvent.DeepEnvelope envelope = DeepBiEvent.DeepEnvelope.newBuilder()
        .setEventId(ByteString.copyFromUtf8(String.valueOf(i)))
        .setPartitionKey("p1")
        .setRemoteAddress(ByteString.copyFromUtf8("192.168.0.4"))
        .setEventTime(new Date().getTime())
        .setServerTime(new Date().getTime())
        .setClientId("client_id_" + id)
        .setTrackerId("tracker_id_" + id)
        .setDataSchemaUrl("test")
        .setData(JsonEntity.emptyObject()
          .create("time", 123456789)
          .create("event", "abc")
          .create("some", "property")
          .create("userAgent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36")
          .toString())
        .addEnrichments(
          DeepBiEvent.DataEnrichment.newBuilder()
            .setCommand("userAgent")
            .setOptions(JsonEntity.emptyObject()
              .create("field", "userAgent")
              .create("target", "device").toString())
            .build()
        )
        .build();
      try {
        //if (i % 2 == 0) {
        //  producer.send(new ProducerRecord<Integer, byte[]>("dev.deep_attention_events_raw_2", envelope.toByteArray())).get();
        //} else {
          producer.send(new ProducerRecord<Integer, byte[]>("test2"/*"dev.deep_attention_events_raw"*/, envelope.toByteArray())).get();
        //}
      } catch (InterruptedException | ExecutionException e){
        e.printStackTrace();
      }
    });

  }
}
