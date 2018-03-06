package cn.alexchao.dcnn_master;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        WSServer.MASTER_IP = Util.getLocalIp(this);
        WSServer.MASTER_IP = "192.168.1.110";

        ((TextView) findViewById(R.id.master_ip)).setText(Util.getLocalIp(this));

        findViewById(R.id.start_btn).setOnClickListener(this);
        findViewById(R.id.stop_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_btn: {
                startService(new Intent(this, WSServerService.class));
                break;
            }
            case R.id.stop_btn: {
                stopService(new Intent(this, WSServerService.class));
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, WSServerService.class));
        super.onDestroy();
    }
}
