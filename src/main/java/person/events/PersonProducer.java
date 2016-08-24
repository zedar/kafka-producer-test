package person.events;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import person.PersonOuterClass;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class PersonProducer {
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
      String name = "Name_" + i;
      Integer age = i * 10;
      String email = "test_" + i + "@test.com";
      PersonOuterClass.Person person = PersonOuterClass.Person.newBuilder()
        .setName(name)
        .setAge(age)
        .setEmail(email)
        .build();
      try {
        producer.send(new ProducerRecord<Integer, byte[]>("test", person.toByteArray())).get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    });
  }
}
