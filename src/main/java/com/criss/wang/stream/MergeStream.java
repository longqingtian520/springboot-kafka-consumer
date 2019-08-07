package com.criss.wang.stream;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

public class MergeStream {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
	}

	public static void test() {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-key-sum");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "office-server:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

		StreamsBuilder builder1 = new StreamsBuilder();
		StreamsBuilder builder2 = new StreamsBuilder();
		try {
			final Serde<Integer> intSerde = Serdes.Integer();
			final Serde<String> stringSerde = Serdes.String();
			KStream<Integer, String> source1 = builder1.stream("criss-another-topic", Consumed.with(intSerde, stringSerde));
			KStream<Integer, String> source2 = builder2.stream("aaaa", Consumed.with(intSerde, stringSerde));
			KStream<Integer, String> merged = source1.merge(source2);
			merged.to("criss-out-topic");
		}catch(Exception e) {
			System.out.println(e);
		}
//		KStream<byte[], String> merged = source.merge(stream2);
		final KafkaStreams streams = new KafkaStreams(builder1.build(), props);
        final CountDownLatch latch = new CountDownLatch(1);
		 // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-key-shutdown-hook") {
            @Override
            public void run() {
            	streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
	}

}
