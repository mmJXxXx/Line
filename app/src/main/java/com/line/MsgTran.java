package com.line;

import org.litepal.crud.DataSupport;

/**
 消息传输-数据库
 */

public class MsgTran extends DataSupport {
        private String  id;
        private String message;
        private String buff;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getBuff() {
            return buff;
        }

        public void setBuff(String buff) {
            this.buff = buff;
        }
}
