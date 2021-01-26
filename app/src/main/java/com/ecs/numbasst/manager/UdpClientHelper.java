package com.ecs.numbasst.manager;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zw
 * @time 2021/1/22
 * @description
 */
public class UdpClientHelper {

    private static final String TAG = "zwcc";

    private static final String  SERVER_IP="192.168.90.33";//目标ip
    //private static final String  SERVER_IP="192.168.0.100";//目标ip
    //private static final String  SERVER_IP="172.28.32.190";//目标ip
    private static final int  SERVER_RECEIVER_PORT=9002;//目标接收端端口号

    private static final int  CLIENT_SEND_PORT= 8888;//本地发送端端口号 , 本地接收端端口号

    private static final int  CLIENT_RECEIVER_PORT=9002;//本地接收端端口号

    private static volatile UdpClientHelper instance;

    private DatagramSocket dataSocketSend;//发送的数据通道
    private DatagramSocket dataSocketReceiver;//接收的数据通道

    private InetAddress destNetAddress;//目标地址,ip需要包装为它才能传入包

    private boolean receiveFlag = true;

    DatagramPacket receivedPacket;


    private ExecutorService executorSend; //用于发送数据的 单核线程池
   // private ThreadPoolExecutor executorSend; //用于发送数据的 单核线程池

    private ExecutorService executorReceiver;
   // private ThreadPoolExecutor executorReceiver;

    private UdpClientHelper() {
        init();
    }


    public static UdpClientHelper getInstance() {
        if (instance == null) {
            synchronized (UdpClientHelper.class) {
                if (instance == null) {
                    instance = new UdpClientHelper();
                }
            }
        }
        return instance;
    }


    /**
     * 参数配置（工厂）
     */
    public void init(){
        try {
            dataSocketSend=new DatagramSocket(CLIENT_SEND_PORT);
            dataSocketReceiver=new DatagramSocket(CLIENT_RECEIVER_PORT);
            //dataSocketReceiver=new DatagramSocket(CLIENT_SEND_PORT);

            destNetAddress = InetAddress.getByName(SERVER_IP);

            executorSend = Executors.newSingleThreadExecutor();
           // executorSend = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);;
            executorReceiver = Executors.newSingleThreadExecutor();
            //executorReceiver = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);;
            Log.d(TAG,"UDP服务初始化成功");

        } catch (Exception e) {
            Log.d(TAG,"UDP服务初始化失败");
            e.printStackTrace();
        }

    }

    public void realse(){
        if (dataSocketSend!=null){
            dataSocketSend.close();
        }
        if (dataSocketReceiver!=null){
            dataSocketReceiver.close();
        }
    }


    public void sendMsg(byte[] buffer){
        Log.d(TAG, "sendMsg："+ ByteUtils.bytesToString(buffer));
        executorSend.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramPacket datagramPacket=new DatagramPacket(buffer,buffer.length,destNetAddress,SERVER_RECEIVER_PORT);//创建数据报
                    dataSocketSend.send(datagramPacket); //发送数据报
                    Log.d("zwcc","dataSocketSend 消息发送完成");
                } catch (Exception e) {
                    Log.v(TAG, "sendMsg error");
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopReceiverMsgListener(){
        receiveFlag = false;
    }


    public void startReceivedMsgListener(CallBack callBack){
        executorReceiver.execute(new Runnable() {
            @Override
            public void run() {
                while (receiveFlag) {
                    Log.d("zwcc","startReceivedMsgListener");
                    byte[] bytes = new byte[1024];
                    receivedPacket= new DatagramPacket(bytes, 0, bytes.length);
                    try {
                        dataSocketReceiver.receive(receivedPacket);
                        //dataSocketSend.receive(receivedPacket);
                        if (callBack!=null){
                            callBack.onReceived(receivedPacket.getData());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    public  interface CallBack{
        void onReceived(byte[] data);
    }


}
