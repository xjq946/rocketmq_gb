package com.itheima.order;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
public class OrderDemoApplication {
	public static void main(String[] args) throws MQClientException {
		SpringApplication.run(OrderDemoApplication.class, args);

		//创建消息的消费者
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("zhb_trans-client-group");
		//设置要链接的服务器地址(nameserver)
		consumer.setNamesrvAddr("127.0.0.1:9876");
		//设置单次消费的消息的数量
		consumer.setConsumeMessageBatchMaxSize(5);
		//设置消息消费的顺序
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		//设置消费者监听哪些消息
		consumer.subscribe("zhbtopic", "zhbtags");
		//进行消息的接收，并返回接收消息的结果
		consumer.registerMessageListener(new MessageListenerConcurrently() {
			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

				try {
					for(MessageExt mes :list){
						String topic = mes.getTopic();
						String tags = mes.getTags();
						String keys = mes.getKeys();
						String s = new String(mes.getBody(), "utf-8");
						String transactionId = mes.getTransactionId();
						System.out.println("接收到的transactionid:"+transactionId+", topic:"+topic+",tags:"+tags+",消息:"+s);

					}
				}catch( Exception e){
					e.printStackTrace();
				}

				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		//启动消费者
		System.out.println("启动完成");
		consumer.start();
	}

}
