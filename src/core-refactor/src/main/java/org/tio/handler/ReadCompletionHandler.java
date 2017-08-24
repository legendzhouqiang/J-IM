package org.tio.handler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.tio.common.ChannelContext;
import org.tio.common.ChannelStat;
import org.tio.common.GroupStat;
import org.tio.common.SystemTimer;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Data
@Slf4j
public class ReadCompletionHandler implements CompletionHandler<Integer, ChannelContext> {

    @Override
    public void completed(Integer result, ChannelContext context) {
        if (result > 0) {
            ChannelStat cStat = context.getStat();
            cStat.setLatestTimeOfReceivedPacket(SystemTimer.currentTimeMillis());
            cStat.getReceivedBytes().addAndGet(result);
            GroupStat gStat = context.getGroupContext().getStatistics();
            gStat.getReceivedBytes().addAndGet(result);
        } else {

        }
    }

    @Override
    public void failed(Throwable exc, ChannelContext context) {

    }
}
