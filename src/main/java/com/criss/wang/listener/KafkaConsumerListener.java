package com.criss.wang.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.criss.wang.service.ConsumerService;

@Component
public class KafkaConsumerListener {

	@Autowired
	private ConsumerService consumerService;

	@KafkaListener(topics = { "criss-test", "criss-out-topic", "criss-another-test", "luffy-uav-flydata-sync", "streams-temperature" }, groupId = "test-consumer-group")
	public void processMsg(ConsumerRecord<String, String> record) {
		switch (record.topic()) {
		case "criss-test":
			consumerService.processData(record);
			break;
		case "criss-another-test":
			consumerService.processData(record);
			break;
		case "criss-out-topic":
			consumerService.processOutData(record);
			break;
		case "luffy-uav-flydata-sync":
//			System.out.println("kdjf=========");
			break;
		case "streams-temperature":
			System.out.println("1111");
			break;
		default:
			break;
		}
	}
}
