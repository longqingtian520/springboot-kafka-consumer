package com.criss.wang.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

	private boolean flag = true;

	public void processData(ConsumerRecord<String, String> record) {
		String str = record.value();
		System.out.println(str);

	}

	public static void main() {

			System.out.println("================");
			StreamsBuilder kStreamBuilder = new StreamsBuilder();
			// 数据输入流
			KStream<String, String> left = kStreamBuilder.stream("criss-test");
			KStream<String, String> right = kStreamBuilder.stream("criss-another-test");
			// 作业处理
			KStream<String, String> all = left.selectKey((key, value) -> value.split(",")[1])
	                .join(right.selectKey((key, value) -> value.split(",")[0]), new ValueJoiner<String, String, String>() {
	            @Override
	            public String apply(String value1, String value2) {
	                return value1 + "--" + value2;
	            }
	        }, JoinWindows.of(30000));

			System.out.println("******");

	}

	public void processOutData(ConsumerRecord<String, String> record) {
		System.out.println("----------------");
		String str = record.value();
		System.out.println("===> " + str);
	}




}
