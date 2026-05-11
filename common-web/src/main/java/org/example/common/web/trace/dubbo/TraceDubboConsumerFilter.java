package org.example.common.web.trace.dubbo;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.example.common.web.RequestHeaderNames;
import org.example.common.web.trace.context.TraceContext;
import org.springframework.util.StringUtils;

@Activate(group = CommonConstants.CONSUMER)
public class TraceDubboConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String traceId = TraceContext.getCurrentTraceId();
        if (StringUtils.hasText(traceId)) {
            invocation.setAttachment(RequestHeaderNames.TRACE_ID, traceId);
        }

        return invoker.invoke(invocation);
    }
}
