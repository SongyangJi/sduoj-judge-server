/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 *
 * 本次评测机集群与后端服务器采用消息队列 RabbitMQ 进行通信，
 * 这里并没有采用 Dubbo 之类的RPC框架，理由主要是杀鸡勿用牛刀，否则会带来无谓的开销。
 * 评测机的任务非常的单一，思来想去，只有两个：
 *  一是判题、二是存储题目。
 *  判题：后端服务器和评测机集群的之间的关系是典型的生产者、消费者关系，也真因此进行负载均衡；
 *  存题：因为每一个评测机都是平权的（最起码目前是这样的），所以后端服务器和评测机集群构成发布、订阅模式。
 *  综上，评测机 v1.0的开发仅仅使用 RabbitMQ 即可充分的完成任务。
 *
 *  即使如此，为了更好地进行消息的交换,需要进行消息体的约定。
 *  这里，借鉴了HTTP协议的相关规定。
 *
 *  不过，这部分很有可能在未来会进行重构。
 *
 */

package com.sduoj.judgeserver.rpc;