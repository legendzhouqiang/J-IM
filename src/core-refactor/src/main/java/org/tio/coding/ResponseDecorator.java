package org.tio.coding;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/15
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface ResponseDecorator<resp, packet> {

    resp createResponse(packet p);

}