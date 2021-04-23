package com.sduoj.judgeserver.rpc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: Song yang Ji
 * @ProjectName: sduoj-judge-server
 * @Version 1.0
 * @Description: RPC请求类
 */

@Getter
@Setter
@NoArgsConstructor
public class RpcRequest extends RpcProtocol {

    RequestBody requestBody;

    protected RpcRequest(RpcRequestBuilder builder) {
        this.header = builder.header;
        this.procedure = builder.procedure;
        this.requestBody = builder.requestBody;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @ToString
    public static class RequestBody {
        String body;

        /**
         * @param body 这里的 body 为请求体，是对应实体类的json串
         */
        public RequestBody(String body) {
            this.body = body;
        }
    }


    public RpcResponse generateResponse() {
        return new RpcResponse(procedure, header);
    }

    public static class RpcRequestBuilder {

        RpcProcedure procedure;

        RpcHeader header;

        RequestBody requestBody;

        public RpcRequestBuilder setProcedure(RpcProcedure procedure) {
            this.procedure = procedure;
            return this;
        }

        public RpcRequestBuilder setHeader(RpcHeader header) {
            this.header = header;
            return this;
        }

        public RpcRequestBuilder setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public RpcRequest build() {
            return new RpcRequest(this);
        }

    }


    @Override
    public String toString() {
        return "RpcRequest{" +
                "procedure=" + procedure +
                ", header=" + header +
                ", requestBody=" + requestBody +
                '}';
    }
}
