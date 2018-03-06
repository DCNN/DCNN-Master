package cn.alexchao.dcnn_master;

import android.util.JsonReader;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;

public class WSServer extends WebSocketServer {
    public static String MASTER_IP = null;
    private static final String TAG = "WSServer";

    // store the master's connection
    private WebSocket mMasterWS;

    // store the list of workers
    private HashMap<String, WebSocket> mWorkerList;
    private HashSet<String> mWorkerSet;

    public WSServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.mWorkerList = new HashMap<>();
        this.mWorkerSet = new HashSet<>();
        Log.d(TAG, "WS Server is running");
    }

    private void addMaster(WebSocket conn) {
        if (conn != null) {
            this.mMasterWS = conn;
        }
    }

    private void addWorker(String ipAddr, WebSocket conn) {
        if (conn != null) {
            this.mWorkerList.put(ipAddr, conn);
            this.mWorkerSet.add(ipAddr);
        }
    }

    public int getConnectedWorkerNum() {
        return this.mWorkerList.size();
    }

    // router
    private void route(WebSocket conn, String jsonStr) {
        String sourceIP = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        Log.d("WSServer", sourceIP);
        if (sourceIP.equals(MASTER_IP)) {
            // master - > workers
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);

                Log.d("WSServer", "func: " + jsonObject.getString("func"));

                // func === send2node
                if (jsonObject.getString("func").equals("calConv")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String targetIP = data.getString("targetIP");
                    this.handleSend2Node(targetIP, jsonStr);
                }
                // func === result
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // worker -> master
            if (this.mMasterWS != null) {
                this.mMasterWS.send(jsonStr);
            }
        }
//        if (sourceAddr.equals(MASTER_IP)) {
//            // message from the master
//            for (String aIp : this.mWorkerSet) {
//                WebSocket aConn = this.mWorkerList.get(aIp);
//                if (aConn != null) {
//                    aConn.send(jsonStr);
//                }
//            }
//        } else {
//            // message from a worker
//            this.mMasterWS.send(jsonStr);
//        }
    }

    // handles
    private void handleSend2Node(String targetIP, String jsonStr) {
        Log.d("WSServer", "handleSend2Node");
        WebSocket aConn = this.mWorkerList.get(targetIP);
        if (aConn != null) {
            aConn.send(jsonStr);
            Log.d("WSServer", "JSON Send to " + targetIP);
        }
    }

    // ----------- handle ws request ------------
    // invoked when a new client connect to the server
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String sourceAddr = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("func", "setIP");
            jsonObject.put("data", sourceAddr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (MASTER_IP.equals(sourceAddr)) {
            Log.d(TAG, "Welcome, Master");
            this.addMaster(conn);
        } else {
            Log.d(TAG, sourceAddr + " has connected to this server");
            this.addWorker(sourceAddr, conn);
        }
        conn.send(jsonObject.toString());
    }

    // invoked when a connected client disconnect from the server
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//        String sourceAddr = conn.getRemoteSocketAddress().getAddress().getHostAddress();
//        Log.d(TAG, sourceAddr + " has disconnected from this server");
//        this.mWorkerList.remove(sourceAddr);
    }

    // invoked when a new message(string) comes
    @Override
    public void onMessage(WebSocket conn, String message) {
        // forwarding JSON
        Log.d("WSServer", "receive message");
        this.route(conn, message);
    }

    // invoked when a new message(byte buffer) comes
    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {}

    // invoked when an error happens
    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    // invoked when this server starts
    @Override
    public void onStart() {
        Log.d("WSServer", "Server started!");
    }
}
