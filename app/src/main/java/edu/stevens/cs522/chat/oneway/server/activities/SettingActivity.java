package edu.stevens.cs522.chat.oneway.server.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.stevens.cs522.chat.oneway.server.R;
import edu.stevens.cs522.chat.oneway.server.contract.Properties;
import edu.stevens.cs522.chat.oneway.server.managers.ServiceHelper;

public class SettingActivity extends Activity implements View.OnClickListener{

    private EditText ServerUrl;
    private EditText ClientName;
    private Button button;
    ;
    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ServerUrl = (EditText)findViewById(R.id.server_address);
        ClientName = (EditText)findViewById(R.id.client_name);
        context = this;
        button = (Button)findViewById(R.id.register_btn);
        button.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){

        switch (v.getId()){
            case R.id.register_btn:
                String url = ServerUrl.getText().toString();
                String clientName = ClientName.getText().toString();
                SharedPreferences setting = getSharedPreferences("SETTING", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();
                editor.putString(Properties.CLIENT_NAME_KEY,clientName);
                editor.putString(Properties.SERVER_URL_KEY, url);
                editor.putLong(Properties.SEQUENCE_NUMBER,0);
                editor.commit();
                ServiceHelper sh = new ServiceHelper(this);
                sh.register(clientName,url);
                Log.v("SettingActivity","huhuhu");
                break;
            default:
                break;

        }
    }

}
