package org.tio.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class GroupContext {

    private static int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;


    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 4 < 256 ? 256 : CORE_POOL_SIZE * 4;

}