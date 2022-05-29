package org.jeff.distributed.service;

import lombok.extern.slf4j.Slf4j;
import org.jeff.distributed.dao.OrderItemMapper;
import org.jeff.distributed.dao.OrderMapper;
import org.jeff.distributed.dao.ProductMapper;
import org.jeff.distributed.model.Order;
import org.jeff.distributed.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jeff
 * @since 1.0.0
 */
@Slf4j
@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private ProductMapper productMapper;

    //购买商品id
    private int purchaseProductId = 1;
    //购买商品数量
    private int purchaseProductNum = 1;

    @Resource
    private PlatformTransactionManager platformTransactionManager;
    @Resource
    private TransactionDefinition transactionDefinition;

    // 创建一个可重入锁
    private Lock lock = new ReentrantLock();

    @Transactional(rollbackFor = Exception.class)
    // 存在超卖问题的代码,加入synchronization也还存在问题
    public synchronized Integer createOrderQuestion() throws Exception {

        Product product = productMapper.selectByPrimaryKey(purchaseProductId);
        if (product == null) {
            throw new Exception("购买商品：" + purchaseProductId + "不存在");
        }

        //商品当前库存
        Integer currentCount = product.getCount();
        System.out.println(Thread.currentThread().getName() + "库存数：" + currentCount);
        //校验库存
        if (purchaseProductNum > currentCount) {
            throw new Exception("商品" + purchaseProductId + "仅剩" + currentCount + "件，无法购买");
        }

        productMapper.updateProductCount(purchaseProductNum, "xxx", new Date(), product.getId());


        Order order = new Order();
        order.setOrderAmount(product.getPrice().multiply(new BigDecimal(purchaseProductNum)));
        order.setOrderStatus(1);//待处理
        order.setReceiverName("xxx");
        order.setReceiverMobile("13311112222");
        order.setCreateTime(new Date());
        order.setCreateUser("xxx");
        order.setUpdateTime(new Date());
        order.setUpdateUser("xxx");
        orderMapper.insertSelective(order);

        return order.getId();
    }


    // 解决方案1: 同步方法+手动控制事务
    //@Transactional(rollbackFor = Exception.class)
    // 使用同步方法,事务还没提交之前,新的线程进来后查询的还是上次数据 - 手动控制事务可以解决
    public synchronized Integer createOrder() throws Exception {
        TransactionStatus transaction = platformTransactionManager.getTransaction(transactionDefinition);

        Product product = null;

        product = productMapper.selectByPrimaryKey(purchaseProductId);
        if (product == null) {
            platformTransactionManager.rollback(transaction);
            throw new Exception("购买商品：" + purchaseProductId + "不存在");
        }

        //商品当前库存
        Integer currentCount = product.getCount();
        System.out.println(Thread.currentThread().getName() + "库存数：" + currentCount);
        //校验库存
        if (purchaseProductNum > currentCount) {
            platformTransactionManager.rollback(transaction);
            throw new Exception("商品" + purchaseProductId + "仅剩" + currentCount + "件，无法购买");
        }

        productMapper.updateProductCount(purchaseProductNum, "xxx", new Date(), product.getId());


        return createOrder(product, transaction);
    }

    /**
     * 解决方案2: 同步代码块+手动控制事务
     * 严格来说，这里由于是两个事物，如果创建订单失败的话，那么库存和订单数据就会出现不一致，库存扣减成功、订单失败；
     * 这样的有点来说：加锁的时间变少了，并发量提高了，因为后面的操作在实际业务中会比库存扣减耗时（创建订单、订单明细这些操作
     */
    public Integer createOrderSynchronized() throws Exception {
        TransactionStatus transaction = null;


        Product product = null;
        synchronized (this) {
            transaction = platformTransactionManager.getTransaction(transactionDefinition);

            product = productMapper.selectByPrimaryKey(purchaseProductId);
            if (product == null) {
                throw new Exception("购买商品：" + purchaseProductId + "不存在");
            }

            //商品当前库存
            Integer currentCount = product.getCount();
            System.out.println(Thread.currentThread().getName() + "库存数：" + currentCount);
            //校验库存
            if (purchaseProductNum > currentCount) {
                throw new Exception("商品" + purchaseProductId + "仅剩" + currentCount + "件，无法购买");
            }
            productMapper.updateProductCount(purchaseProductNum, "xxx", new Date(), product.getId());
            platformTransactionManager.commit(transaction);
        }


        transaction = platformTransactionManager.getTransaction(transactionDefinition);
        return createOrder(product, transaction);
    }


    /**
     * 解决方案3: ReentrantLock+手动控制事务
     * 严格来说，这里由于是两个事物，如果创建订单失败的话，那么库存和订单数据就会出现不一致，库存扣减成功、订单失败；
     * 这样的有点来说：加锁的时间变少了，并发量提高了，因为后面的操作在实际业务中会比库存扣减耗时（创建订单、订单明细这些操作
     */
    public Integer createOrderReentrantLock() throws Exception {

        Product product = null;
        TransactionStatus transaction = null;


        try {
            lock.lock();
            transaction = platformTransactionManager.getTransaction(transactionDefinition);
            product = productMapper.selectByPrimaryKey(purchaseProductId);
            if (product == null) {
                platformTransactionManager.rollback(transaction);
                throw new Exception("购买商品：" + purchaseProductId + "不存在");
            }

            //商品当前库存
            Integer currentCount = product.getCount();
            System.out.println(Thread.currentThread().getName() + "库存数：" + currentCount);
            //校验库存
            if (purchaseProductNum > currentCount) {
                platformTransactionManager.rollback(transaction);
                throw new Exception("商品" + purchaseProductId + "仅剩" + currentCount + "件，无法购买");
            }
            productMapper.updateProductCount(purchaseProductNum, "xxx", new Date(), product.getId());
            platformTransactionManager.commit(transaction);

        }
        //catch (){} 注意catch导致异常被生吞的情况
        finally {
            lock.unlock();
        }

        TransactionStatus transaction1 = platformTransactionManager.getTransaction(transactionDefinition);
        return createOrder(product, transaction1);

    }

    private Integer createOrder(Product product, TransactionStatus transaction1) {
        Order order = new Order();
        order.setOrderAmount(product.getPrice().multiply(new BigDecimal(purchaseProductNum)));
        order.setOrderStatus(1);//待处理
        order.setReceiverName("xxx");
        order.setReceiverMobile("13311112222");
        order.setCreateTime(new Date());
        order.setCreateUser("xxx");
        order.setUpdateTime(new Date());
        order.setUpdateUser("xxx");
        orderMapper.insertSelective(order);
        platformTransactionManager.commit(transaction1);
        return order.getId();
    }

}
