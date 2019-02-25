package com.criss.wang.project;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;


public class KStreamDemo {

	public static void main(String[] arg) {
		Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "office-server:9092");
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "office-server:2181");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
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

        KStreamBuilder  kStreamBuilder = new KStreamBuilder();

		// 数据输入流
		KStream<String, String> left = kStreamBuilder.stream("criss-test");
		KStream<String, String> right = kStreamBuilder.stream("criss-another-test");
		// 作业处理
		KStream<String, String> all = left.selectKey((key, value) -> value.split(",")[1])
                .leftJoin(right.selectKey((key, value) -> value.split(",")[0]), new ValueJoiner<String, String, String>() {
            @Override
            public String apply(String value1, String value2) {
                return value1 + "--" + value2;
            }
        },
        JoinWindows.of(10000));

		all.print();

		all.to("criss-out-topic");

		StreamsConfig config = new StreamsConfig(props);

		KafkaStreams streams = new KafkaStreams(kStreamBuilder, config);
        streams.start();
	}
}
