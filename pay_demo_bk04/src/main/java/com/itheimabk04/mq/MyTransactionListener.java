package com.itheimabk04.mq;

import com.itheimabk04.service.PayService;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyTransactionListener implements TransactionListener {

    //记录对应事务消息的执行状态   1:正在执行，2：执行成功，3：失败了
    //对于mq来说,正在事务发起方正在执行查询结果,只要未收到明确的commit或者rollback,都是未知结果unkown
    //对于mq来说,commit执行成功,才发送消息
    //对于mq来说,事务执行失败了将不再发送消息,并且将消息队列中的half消息干掉,以免再次扫描到再次回查
    //通过事务的id来辨别不同的事务

    private ConcurrentHashMap<String,Integer> transMap = new ConcurrentHashMap<String,Integer>();

    @Resource
    private PayService payService;
    /**
     * 消息发送方执行自身业务操作的方法
     * @param msg 发送方发送的东西
     * @param arg 额外的参数
     * @return
     */
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) throws RuntimeException {
            //业务代码写这里

        String transactionId = msg.getTransactionId();
        //设置执行状态为正在执行,state=1
        transMap.put(transactionId, 1);
        //取id和ispay参数
        Map payArgs= (Map) arg;
        Integer id= (Integer) payArgs.get("id");
        Integer ispay= (Integer) payArgs.get("ispay");

        try {
            //控制本地事务
            System.out.println("支付表更新开始");
            payService.updatePayTable(id, ispay);
            System.out.println("支付表更新成功");
            //测试用例1
//            int i=1/0;
           // 测试用例2 测试网络超时状态
//            Thread.sleep(70000);
            System.out.println("更新订单状态");
            System.out.println("订单已更新");
            //执行成功时,返回提交事务消息成功的标识
            transMap.put(transactionId, 2);
//            if(1==1){
//                return  LocalTransactionState.UNKNOW;
//            }


        }catch (Exception e){
            //发生异常时，返回回滚事务消息
            //执行成功时,返回提交事务消息成功的标识
            transMap.put(transactionId, 3);
            System.out.println("事务执行失败,事务执行状态为:"+ LocalTransactionState.ROLLBACK_MESSAGE);
            return  LocalTransactionState.ROLLBACK_MESSAGE;
        }
        System.out.println("事务执行成功,事务执行状态为:"+ LocalTransactionState.COMMIT_MESSAGE);
        return LocalTransactionState.COMMIT_MESSAGE;

    }




    /***
     * 事务超时，回查方法
     * @param msg:携带要回查的事务ID
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        //根据transaction的id回查该事务的状态,并返回给消息队列
        //未知状态:查询事务状态,但始终无结果,或者由于网络原因发送不成功,对mq来说都是未知状态,LocalTransactionState.UNKNOW
        //正确提交返回LocalTransactionState.COMMIT_MESSAGE
        //事务执行失败返回LocalTransactionState.ROLLBACK_MESSAGE
        String transactionId = msg.getTransactionId();
        Integer state = transMap.get(transactionId);
        System.out.println("回查的事务id为:"+transactionId+",当前的状态为"+state);

        if (state==2){
            //执行成功,返回commit
            System.out.println("回查结果为事务正确提交,返回状态为:"+ LocalTransactionState.COMMIT_MESSAGE);
            return  LocalTransactionState.COMMIT_MESSAGE;

        }else if(state==3){
            //执行失败,返回rollback
            System.out.println("回查结果为事务回滚,返回状态为:"+ LocalTransactionState.ROLLBACK_MESSAGE);
            return  LocalTransactionState.ROLLBACK_MESSAGE;
        }

            //正在执行
            System.out.println("回查正在执行,返回状态为:"+ LocalTransactionState.UNKNOW);
            return  LocalTransactionState.UNKNOW;

    }
}
