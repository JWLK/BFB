package co.haslo.cop.init;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import co.haslo.cop.PHPComm;
import co.haslo.cop.R;
import co.haslo.cop.Value;
import co.haslo.cop.main.MainView;

public class Login extends Activity {

    /*layout*/
    EditText etId;
    EditText etPw;
    Switch autologin;
    Button btnLogin;
    Button btnRegist;

    /*Value*/
    String userID;
    String password;
    Boolean etIdChecked;
    Boolean etPwChecked;
    Boolean loginChecked;

    String uid;
    String admin;
    public SharedPreferences settings;

    Context mContext;

    // 멀티 퍼미션 지정
    private String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE, // 전화걸기 및 관리
            Manifest.permission.WRITE_CONTACTS, // 주소록 액세스 권한
            Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한
    };
    private static final int MULTIPLE_PERMISSIONS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_login);
        mContext = this.getBaseContext();

        if (Build.VERSION.SDK_INT >= 23) { // 안드로이드 6.0 이상일 경우 퍼미션 체크
            checkPermissions();
        }

        // 네트워크 연결상태 체크
        if(NetworkConnection() == false){
            NotConnected_showAlert();
        }

        /*EditText*/
        etId = (EditText) findViewById(R.id.login_id_edit);
        etPw = (EditText) findViewById(R.id.login_pw_edit);

        /*Switch*/
        autologin = (Switch) findViewById(R.id.autologin);
        autologin.setChecked(true);

        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);

        if(autologin.isChecked()){
            etId.setText(settings.getString("userID", ""));
            etPw.setText(settings.getString("password", ""));
            autologin.setChecked(true);
        }else {

        }

        if(!settings.getString("userID", "").equals("")) etPw.requestFocus();


        /*Button*/
        btnLogin = (Button) findViewById(R.id.login_btn);


        /*Listener*/
        etCheckListener();
        btnLoginListener();

    }

    private void etCheckListener(){
        etPw.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if( (etId.getText().length() > 0) && (etPw.getText().length() > 0) ){
                    btnLogin.setBackgroundResource(R.color.colorMainBlack);
                } else {
                    btnLogin.setBackgroundResource(R.color.colorGray9E);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    private void btnLoginListener(){
        btnLogin.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                userID = etId.getText().toString().trim();
                password = etPw.getText().toString().trim();
                Log.d("userID",""+userID);

//                if(userID != null && !userID.isEmpty() && password != null && !password.isEmpty()){
//                    login(userID, password);
//                }

                if(userID != null && !userID.isEmpty() && password != null && !password.isEmpty()){
                    if(userID.equals("test") && password.equals("0000") ){
                        AutoLoginChk();
                        startActivity(new Intent(getApplication(), MainView.class));
                        finish(); // 현재 Activity 를 없애줌
                        Toast.makeText(Login.this,"환영합니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        showAlert();
                    }
                }else{
                    inpuDismiss_showAlert();
                }

            }
        });
    }


    private void login(String loginID, String loginPW){
        // 전달할 인자들
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("userID", loginID)
                .appendQueryParameter("password", loginPW);
        //.appendQueryParameter("deviceID", getDeviceID);
        String urlParameters = builder.build().getEncodedQuery();
        AsyncTask<String, Void, String> vieJsion = new getJSONData().execute(Value.IPADDRESS + "/loginChk.php", urlParameters);
    }

    private class getJSONData extends AsyncTask<String, Void, String> {
        ProgressDialog pdLoading = new ProgressDialog(Login.this);
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\t사용자 확인중...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return PHPComm.getJson(params[0],params[1]);
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        protected void onPostExecute(String result){
            pdLoading.dismiss();
            Log.d("jsonData_Login",""+result);
            showJSONResult(result);
        }
    }

    protected void showJSONResult(String result) {
        Log.d("resultJson",""+result);
        if(result.contains(",")){
            String[] dataString = result.split(",");
            uid = dataString[0];
            admin = dataString[1];
        }else{
            uid = result;
        }

        Log.d("result_uid",""+uid);
        Log.d("result_admin",""+admin);

        if(Integer.parseInt(uid) > 0){ // 로그인 정보 일치하면 uid 값 받음

            AutoLoginChk(); // 정보 저장

            startActivity(new Intent(getApplication(), MainView.class));
            finish(); // 현재 Activity 를 없애줌

            Toast.makeText(Login.this,"환영합니다.", Toast.LENGTH_SHORT).show();

        } else if (Integer.parseInt(uid) == -1){ // 데이터를 모두 입력하지않음.
            inpuDismiss_showAlert();
        } else if (Integer.parseInt(uid) == 0){
            showAlert();
        } else {
            Toast.makeText(Login.this, "서버로부터 정보가 잘못 전송되었습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[i])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showToast_PermissionDeny();
                            }
                        }
                    }
                } else {
                    showToast_PermissionDeny();
                }
                return;
            }
        }

    }

    private void showToast_PermissionDeny() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onStop(){
        // 어플리케이션이 화면에서 사라질때
        super.onStop();
        //AutoLoginChk();
    }

    private void AutoLoginChk(){
        // 자동 로그인이 체크되어 있고, 로그인에 성공했으면 폰에 자동로그인 정보 저장
        if (autologin.isChecked()) {
            settings = getSharedPreferences("settings",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            editor.putString("userID", userID);
            editor.putString("password", password);
            editor.putBoolean("LoginChecked", true);
            editor.putString("uid", uid);

            editor.commit();
        } else {
            // 자동 로그인 체크가 해제되면 폰에 저장된 정보 모두 삭제
            settings = getSharedPreferences("settings",    Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear(); // 모든 정보 삭제
            editor.commit();
        }
    }

    public void inpuDismiss_showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("아이디와 비밀번호를 입력해 주세요.");
        builder.setMessage("아이디와 비밀번호가 입력되지 않았습니다.\n" + "정보를 정확하게 입력해 주시기 바랍니다.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("일치하는 회원정보가 없습니다.");
        builder.setMessage("회원가입을 해주시기 바랍니다.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void NotConnected_showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("네트워크 연결 오류");
        builder.setMessage("사용 가능한 무선네트워크가 없습니다.\n" + "먼저 무선네트워크 연결상태를 확인해 주세요.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // exit
                        //application 프로세스를 강제 종료
                        android.os.Process.killProcess(android.os.Process.myPid() );
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private boolean NetworkConnection() {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if(activeNetwork != null && activeNetwork.getType() == networkType){
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
