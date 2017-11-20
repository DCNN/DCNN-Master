package cn.alexchao.dcnn_master;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Master extends WebSocketServer {
    public Master(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    // invoked when a new client connect to the server
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d("Master", conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    // invoked when a connected client disconnect from the server
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d("Master", conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has left the room!");
    }

    // invoked when a new message(string) comes
    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d("Master", conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + message);
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
        Log.d("Master", "Server started!");
    }
}
