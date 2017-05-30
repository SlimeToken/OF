package com.cs.inje.of;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

    Button confirm;
    EditText schor_num;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        confirm = (Button)findViewById(R.id.confirm_Button);
        schor_num = (EditText)findViewById(R.id.scholar_num);
        password = (EditText)findViewById(R.id.add_password);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpClient hc = new HttpClient(schor_num.getText().toString(),password.getText().toString());
                try {
                    hc.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(hc.parsing("#lbl소속")!=null)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("학생인증 성공").setPositiveButton("확인",null).create().show();
                    Intent mainActivityIntent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(mainActivityIntent);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("학생인증 실패").setNegativeButton("다시 시도",null).create().show();
                }
            }
        });
    }





}
