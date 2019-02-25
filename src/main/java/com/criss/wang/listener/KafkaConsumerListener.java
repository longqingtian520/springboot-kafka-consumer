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

	@KafkaListener(topics = { "criss-test", "criss-out-topic", "criss-another-test" }, groupId = "test-consumer-group")
	public void processMsg(ConsumerRecord<String, String> record) {
		switch (record.topic()) {
		case "criss-test":
			consumerService.processData(record);
//			consumerService.test();
			break;
		case "criss-another-test":
			consumerService.processData(record);
			break;
		case "criss-out-topic":
			consumerService.processOutData(record);
		default:
			break;
		}
	}
}
