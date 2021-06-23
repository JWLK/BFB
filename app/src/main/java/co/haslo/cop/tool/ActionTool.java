package co.haslo.cop.tool;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import co.haslo.cop.R;


public class ActionTool extends Activity {

    ImageButton btnBackShift;

    /*layout*/

    /*Value*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Top Menu*/
        ActionTool actionTool = new ActionTool();
//        btnBackShift = (ImageButton) findViewById(R.id.btn_back_shift);

        /*Text View*/

        /*Button*/

        /*Get Value*/

        /*Listener*/
    }

    public void backClickListener(Activity activity, ImageButton btnBack){

        final Activity fnlActivity = activity;

        btnBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("Log.d", "onClick: BackShift ");
                fnlActivity.finish();
                ((Activity) fnlActivity).overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
    }

}

