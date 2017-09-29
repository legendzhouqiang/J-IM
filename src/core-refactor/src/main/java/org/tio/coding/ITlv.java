package org.tio.coding;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/15
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface ITlv {

    byte type();

    byte len();

    byte[] val();

    class Tlv implements ITlv {

        private byte type;
        private byte len;
        private byte[] value;

        Tlv(byte type, byte len, byte[] value) {
            this.type = type;
            this.len = len;
            this.value = value;
        }

        @Override
        public byte type() {
            return type;
        }

        @Override
        public byte len() {
            return len;
        }

        @Override
        public byte[] val() {
            return value;
        }
    }
}
