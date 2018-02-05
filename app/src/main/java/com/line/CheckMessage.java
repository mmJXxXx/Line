/**
    基于POST，判断消息更新并返回
 */
//package com.line;
//public class CheckMessage {
//    static String Messageback(){
//        return urldispose.geturlback(MessageExist);
//    }
//    static class ThreadB extends Thread{//新建线程
//        @Override
//        public void run() {
//
//            try {
//                while (true) {
//                    MessageBuff=HttpPost.sendHttpPost( "http://119.23.110.11:7777/getvalue","tag="+roomnum);//与服务器上TinyWeb服务器POST传输数据
//                    if(!MessageBuff.equals( MessageExist )){
//                        MessageExist=HttpPost.sendHttpPost("http://119.23.110.11:7777/getvalue","tag="+roomnum);
//                        Messageback();
//                    }
//                }
//            }catch (Exception e){
//
//            }
//        }
//
//    }
//}