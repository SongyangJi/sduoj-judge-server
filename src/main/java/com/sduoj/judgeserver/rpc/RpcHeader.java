package com.sduoj.judgeserver.rpc;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description:
 */

@Getter
@Setter
public class RpcHeader {
    private static final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    String customer;
    String server;
    String ID;
    Date date;

    public RpcHeader() {
        ID = UUID.randomUUID().toString();
        date = new Date();
    }

    public RpcHeader(String customer, String server) {
        this();
        this.customer = customer;
        this.server = server;
    }

    // 受保护的
    protected RpcHeader(RpcHeaderBuilder builder) {
        this(builder.customer, builder.server);
    }

    @Override
    public String toString() {
        return "RpcHeader{" +
                "customer='" + customer + '\'' +
                ", server='" + server + '\'' +
                ", ID='" + ID + '\'' +
                ", date=" + ft.format(date) +
                '}';
    }

    @Setter
    public static class RpcHeaderBuilder {
        String customer;
        String server;

        public RpcHeader build() {
            return new RpcHeader(this);
        }

    }


}
