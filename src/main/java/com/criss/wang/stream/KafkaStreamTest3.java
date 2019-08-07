package com.criss.wang.stream;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

public class KafkaStreamTest3 {

	public static void main(String[] args) throws Exception{
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "office-server:9092");
		props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "500");// 默认30s commit一次
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		// Serializers/deserializers (serde) for String and Long types
		final Serde<String> stringSerde = Serdes.String();
		final Serde<Long> longSerde = Serdes.Long();

		// Construct a `KStream` from the input topic "streams-plaintext-input", where
		// message values
		// represent lines of text (for the sake of this example, we ignore whatever may
		// be stored
		// in the message keys).
		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, String> textLines = builder.stream("criss-test",
				Consumed.with(stringSerde, stringSerde));

		KTable<String, Long> wordCounts = textLines
				// Split each text line, by whitespace, into words.
				.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))

				// Group the text words as message keys
				.groupBy((key, value) -> value)

				// Count the occurrences of each word (message key).
				.count();

		// Store the running counts as a changelog stream to the output topic.
//		wordCounts.toStream().to("criss-out-topic", Produced.with(Serdes.String(), Serdes.Long()));
		// 我们这里为了方便观察，直接打印到控制台
		wordCounts.toStream().print(Printed.toFile("d:\\ks.txt"));
		wordCounts.toStream().print(Printed.toSysOut());
		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();

		Thread.currentThread().join();
	}

}
