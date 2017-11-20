package cn.alexchao.dcnn_master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private Master mMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preSettingOfWS();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.master_ip)).setText(Util.getLocalIp(this));

        startServer();

        findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaster.broadcast("test");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void preSettingOfWS() {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
    }

    private void startServer() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                int port = 8888;
                try {
                    mMaster = new Master(port);
                    mMaster.start();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

            }
        };

        thread.start();
    }
}
