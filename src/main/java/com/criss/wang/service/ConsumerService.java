package com.criss.wang.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

	public void processData(ConsumerRecord<String, String> record) {
		String str = record.value();
		System.out.println("监听的数据：" + str);

	}

	public void processOutData(ConsumerRecord<String, String> record) {
		System.out.println("----------------");
		String str = record.value();
		System.out.println("处理后的数据===> " + str);
	}
}
