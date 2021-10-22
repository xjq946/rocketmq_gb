package com.itheimabk04.controller;

import com.itheimabk04.mq.MyTransactionListener;
import com.itheimabk04.service.PayService;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@RestController
public class PayController {
    @Resource
    private TransactionListener transactionListener;
    @RequestMapping(value = "/pay/updateOrder", method = RequestMethod.POST)
    public String payOrder(@RequestParam("payid") int id, @RequestParam("ispay") int ispay) {
        try {
            //创建事务消息消费者
            TransactionMQProducer transactionMQProducer = new TransactionMQProducer("trans_producer_group_zhb");
            //指定链接的服务器地址(nameserver)
            transactionMQProducer.setNamesrvAddr("127.0.0.1:9876");
            //创建消息回查的类,我们自己的监听器
            transactionMQProducer.setTransactionListener(transactionListener);


            //创建发送的消息
            Message message = new Message(
                    "zhbtopic", "zhbtags", "zhbkeys", "zhb的消息".getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
            //启动发送者
            transactionMQProducer.start();
            //发送消息
            //传递参数is和ispay
            Map payAgrs=new HashMap();
            payAgrs.put("id", id);
            payAgrs.put("ispay",ispay);
            transactionMQProducer.sendMessageInTransaction(message,payAgrs );
            //关闭消息的发送者
            transactionMQProducer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            return "发送消息给mq失败!";
        }
        //如果没有问题,
        return "发送消息给mq成功";
    }

}
