package com.itheimabk04;

import com.alibaba.druid.pool.DruidDataSource;
import com.itheimabk04.mq.MyTransactionListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

@SpringBootApplication(scanBasePackages = {"com.itheimabk04.*"})
@MapperScan("com.itheimabk04.mapper")
public class PayDemoBk04Application {


	public static void main(String[] args) throws MQClientException, UnsupportedEncodingException {
		SpringApplication.run(PayDemoBk04Application.class, args);

//		//创建事务消息消费者
//		TransactionMQProducer transactionMQProducer = new TransactionMQProducer("trans_producer_group_zhb");
//		//指定链接的服务器地址(nameserver)
//		transactionMQProducer.setNamesrvAddr("127.0.0.1:9876");
//		//创建消息回查的类,我们自己的监听器
//		transactionMQProducer.setTransactionListener(new MyTransactionListener());
//		//创建线程池,
//		ExecutorService executorService = new ThreadPoolExecutor(
//				2,
//				5,
//				100,
//				TimeUnit.SECONDS,
//				new ArrayBlockingQueue<Runnable>(
//						2000),
//				new ThreadFactory() {
//					@Override
//					public Thread newThread(Runnable runnable) {
//						Thread thread = new Thread(runnable);
//						thread.setName("zhb_client-transaction-msg-check-thread");
//						return thread;
//					}
//				}
//		);
//
//		//创建发送的消息
//		Message message = new Message(
//				"zhbtopic", "zhbtags", "zhbkeys", "zhb的消息".getBytes(RemotingHelper.DEFAULT_CHARSET)
//		);
//		//启动发送者
//		transactionMQProducer.start();
//		//发送消息
//		transactionMQProducer.sendMessageInTransaction(message,"额外的消息");
//		//关闭消息的发送者
//		transactionMQProducer.shutdown();
	}



	@Autowired
	private Environment env;   //

	@Bean
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(env.getProperty("spring.datasource.url"));
		dataSource.setUsername(env.getProperty("spring.datasource.username"));//用户名
		dataSource.setPassword(env.getProperty("spring.datasource.password"));//密码
		dataSource.setInitialSize(10);
		dataSource.setMaxActive(50);
		dataSource.setMinIdle(0);
		dataSource.setMaxWait(60000);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setTestOnBorrow(false);
		dataSource.setTestWhileIdle(true);
		dataSource.setPoolPreparedStatements(false);
		return dataSource;
	}
}
