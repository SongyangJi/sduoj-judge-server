package com.sduoj.judgeserver.rpc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: RPC回复类
 */


@Getter
@Setter
@NoArgsConstructor
public class RpcResponse extends RpcProtocol {

    ResponseBody responseBody;

    protected RpcResponse(RpcResponseBuilder builder) {
        this.header = builder.header;
        this.procedure = builder.procedure;
        this.responseBody = builder.responseBody;
    }

    public RpcResponse(RpcProcedure procedure, RpcHeader header) {
        this.procedure = procedure;
        this.header = header;
    }


    @Setter
    @Getter
    @ToString
    public static class ResponseBody {
        RpcStatus status;
        String body;

        /**
         * @param status 响应状态
         * @param body   这里的 body 为请求体，是对应实体类的json串
         */
        public ResponseBody(RpcStatus status, String body) {
            this.status = status;
            this.body = body;
        }

        public ResponseBody() {
            this(RpcStatus.OK, null);
        }
    }

    public static class RpcResponseBuilder {
        RpcProcedure procedure;

        RpcHeader header;

        ResponseBody responseBody;

        public RpcResponseBuilder setProcedure(RpcProcedure procedure) {
            this.procedure = procedure;
            return this;
        }

        public RpcResponseBuilder setHeader(RpcHeader header) {
            this.header = header;
            return this;
        }

        public RpcResponseBuilder setResponseBody(ResponseBody responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public RpcResponse build() {
            return new RpcResponse(this);
        }

    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "procedure=" + procedure +
                ", header=" + header +
                ", responseBody=" + responseBody +
                '}';
    }
}
