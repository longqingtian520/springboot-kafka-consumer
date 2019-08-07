package com.criss.wang.stream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;

public class JoinStream {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-consumer");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "office-server:9092");
		props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "office-server:2181");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);// 自动提交
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");// 自动提交
		props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);// 提交间隔
		props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 1);// 和最大分区数保持一致
		props.put(ProducerConfig.LINGER_MS_CONFIG, 5);// 消息发送延迟
		props.put(ProducerConfig.RETRIES_CONFIG, 0);// 消息发送重试
		props.put(StreamsConfig.POLL_MS_CONFIG, 5);// 消息接收间隔

		StreamsBuilder builder = new StreamsBuilder();
		// 数据输入流
		KStream<String, String> left = builder.stream("criss-test");
		KStream<String, String> right = builder.stream("criss-another-test");
		// 作业处理
		KStream<String, String> all = left.selectKey((key, value) -> value.split(",")[1]).leftJoin(
				right.selectKey((key, value) -> value.split(",")[0]), new ValueJoiner<String, String, String>() {
					@Override
					public String apply(String value1, String value2) {
						return value1 + "--" + value2;
					}
				}, JoinWindows.of(20000));

		// 输出到控制台
		all.print();

		// 转发到第三方topic
		all.to("criss-out-topic");

//		Topology topology = builder.build();
//		StreamsConfig config = new StreamsConfig(props);
//		KafkaStreams streams = new KafkaStreams(topology, config);
//		streams.start();

		final KafkaStreams streams = new KafkaStreams(builder.build(), props);
		final CountDownLatch latch = new CountDownLatch(1);

		// attach shutdown handler to catch control-c
		Runtime.getRuntime().addShutdownHook(new Thread("streams-temperature-shutdown-hook") {
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
