package com.itheimabk04.service.impl;




import com.itheimabk04.mapper.PaytableMapper;
import com.itheimabk04.service.PayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class PayServiceImp implements PayService {
    /**
     *1.注入支付表的mapper
     *2. 远程调用订单表的服务,这里使用feign转化为本地服务
     *3.更新支付表   payMapper
     *4.更新订单表的服务
     *5.开启本地事务
     */

    @Resource
    private PaytableMapper paytableMapper;


    @Override
    @Transactional
    public Integer updatePayTable(Integer id, Integer ispay) {
        Integer status;
        //测试用例1,未保存时抛出异常
//        int i=1/0;

        //更新paytable的数据
       status  = paytableMapper.update(id, ispay);
 //测试用例2,保存成功后抛出异常
        System.out.println("更新paytable表,受影响行数:"+status);
//        int i=1/0;
        return status;
    }
}
