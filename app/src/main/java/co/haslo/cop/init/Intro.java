package co.haslo.cop.init;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import co.haslo.cop.R;
import co.haslo.cop.main.MainView;


public class Intro extends Activity {

    /*Layout*/
    Button btnRegist;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_intro);

        /*Set Layout*/
        //btnRegist = (Button)findViewById(R.id.regist_btn);
        btnLogin = (Button)findViewById(R.id.login_btn);


        /*Get Value*/


        /*Listener*/
        btnLoginListener();


    }

    private void btnLoginListener(){
        btnLogin.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), MainView.class));

            }
        });
    }
}
