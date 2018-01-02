package cn.alexchao.dcnn_master;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class WSServer extends WebSocketServer {
    private static final String TAG = "WSServer";
    private static final String MASTER_IP = "localhost";
    private HashMap<String, WebSocket> mWorkerList;

    public WSServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.mWorkerList = new HashMap<>();
        Log.d(TAG, "WS Server is running");
    }

    // invoked when a new client connect to the server
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String sourceAddr = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        if (MASTER_IP == sourceAddr) {
            Log.d(TAG, "Welcome, Master");
        } else {
            Log.d(TAG, sourceAddr + " has connected to this server");
        }
        this.mWorkerList.put(sourceAddr, conn);
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
        String sourceAddr = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        Log.d(TAG, sourceAddr + " sent a message");
        // receiving JSON
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(message);
            switch (jsonObject.getString("op")) {
                case "sendModel":
                    handleRecModel(conn, jsonObject.getJSONObject("data"));
                    break;
//                case "sendInputTensor":
//                    handleRecInputTensor(conn, jsonObject.getJSONArray("data"));
//                    break;
                default:
                    handleRecNoMatch(conn);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    /**
     * @param conn: WebSocket Connection
     * @param model: JSON Object
     * {
     *   conv1BiasesInfo: [conv1Biases: JSONArray, size: JSONArray],
     *   conv1WeightsInfo: [conv1Biases: JSONArray, size: JSONArray],
     *   conv2BiasesInfo: [conv1Biases: JSONArray, size: JSONArray],
     *   conv2WeightsInfo: [conv1Biases: JSONArray, size: JSONArray]
     * }
     * @throws JSONException: JSONException
     */
    private void handleRecModel(WebSocket conn, JSONObject model) throws JSONException {
        double res = model.getJSONArray("conv1WeightsInfo").getJSONArray(1).getDouble(1);

        JSONObject sendJsonObject = new JSONObject();
        try {
            sendJsonObject.put("data", res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        conn.send(sendJsonObject.toString());
    }

    private void handleRecInputTensor(WebSocket conn, JSONArray tensor1D) {
        int len = tensor1D.length();

        JSONObject sendJsonObject = new JSONObject();
        try {
            sendJsonObject.put("data", len);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        conn.send(sendJsonObject.toString());
    }

    private void handleRecNoMatch(WebSocket conn) {
        JSONObject sendJsonObject = new JSONObject();
        try {
            sendJsonObject.put("data", "Err: Op No Match");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        conn.send(sendJsonObject.toString());
    }
}
