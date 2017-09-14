package org.tio.common.channel;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

import static org.tio.common.CoreConstant.ConnectionStatus;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/12
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface Channel extends AttributeMap {

    Integer id();

    void bind(AsynchronousSocketChannel channel) throws IOException;

    ConnectionStatus status();

    void changeStatus(ConnectionStatus status);

    void invalidate();

    ChannelStat stat();

    boolean useChecksum();

    ChannelContext channelContext();
}
