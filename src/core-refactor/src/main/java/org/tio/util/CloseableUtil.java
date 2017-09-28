package org.tio.util;

import com.google.common.io.Closeables;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CloseableUtil {

    public static void closeQuietly(Closeable closeable) {
        try {
            // Here we've instructed Guava to swallow the IOException
            Closeables.close(closeable, true);
        } catch (IOException e) {
            // We instructed Guava to swallow the IOException, so this should
            // never happen. Since it did, log it.
            log.error("IOException should not have been thrown.", e);
        }
    }
}
