package org.tio.common.misc;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tio.common.channel.Channel;

import java.util.Map;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/13
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 拦截器链
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TioIntercepterChain {

    private IntercepterChain head = new IntercepterChain(null);
    private IntercepterChain tail = head;

    private Map<TioException, IntercepterChain> errorMap = Maps.newConcurrentMap();

    private static TioIntercepterChain chain;

    private final String channel_default_key = "default_key:tio_exception_for_intercepter";

    public static TioIntercepterChain newInstance() {
        if (chain == null) {
            chain = new TioIntercepterChain();
        }
        return chain;
    }

    public void addIntercepter(TioIntercepter intercepter) {
        IntercepterChain intercepterChain = new IntercepterChain(intercepter);
        intercepterChain.setPre(tail);
        tail.setNext(intercepterChain);
        tail = intercepterChain;
    }

    public void preHandle(Channel channel) {
        if (head == tail) {
            return;
        }

        IntercepterChain curIntercepter = head.getNext();
        try {
            while (curIntercepter != null) {
                curIntercepter.getIntercepter().preHandle(channel);
                curIntercepter = curIntercepter.getNext();
            }
        } catch (TioException e) {
            curIntercepter.getIntercepter().abort(channel, e);
            errorMap.put(e, curIntercepter);
            channel.setAttribute(channel_default_key, e);
        }
    }

    public void postHandle(Channel channel) {
        if (head == tail) {
            return;
        }

        IntercepterChain curIntercepter = tail;
        TioException e = (TioException) channel.getAttribute(channel_default_key);
        if (e != null && errorMap.containsKey(e)) {
            curIntercepter = errorMap.get(e).getPre();
            errorMap.remove(e);
        }
        try {
            while (curIntercepter != null) {
                curIntercepter.getIntercepter().postHandle(channel);
                curIntercepter = curIntercepter.getPre();
            }
        } catch (TioException exception) {
            curIntercepter.getIntercepter().abort(channel, exception);
        }
    }

    @Data
    private class IntercepterChain {
        private TioIntercepter intercepter;
        private IntercepterChain next;
        private IntercepterChain pre;

        private IntercepterChain(TioIntercepter intercepter) {
            this.intercepter = intercepter;
        }
    }
}
