package org.tio.handler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.tio.common.Channel;
import org.tio.common.ChannelStat;
import org.tio.common.GroupStat;
import org.tio.common.SystemTimer;

import java.nio.channels.CompletionHandler;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Data
@Slf4j
public class ReadCompletionHandler implements CompletionHandler<Integer, Channel> {

    @Override
    public void completed(Integer result, Channel context) {
        if (result > 0) {
            ChannelStat cStat = context.stat();
            cStat.setLatestTimeOfReceivedPacket(SystemTimer.currentTimeMillis());
            cStat.getReceivedBytes().addAndGet(result);
            GroupStat gStat = context.channelContext().getStatistics();
            gStat.getCountOfReceivedBytes().addAndGet(result);

        } else {

        }
    }

    @Override
    public void failed(Throwable exc, Channel context) {

    }
}
