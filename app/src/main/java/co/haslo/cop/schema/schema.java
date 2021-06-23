package co.haslo.cop.schema;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;

import co.haslo.cop.R;
import co.haslo.cop.tool.ActionTool;

public class schema extends Activity {

    ImageButton btnBackShift;

    /*Layout*/

    /*Value*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        /*Top Menu*/
        ActionTool actionTool = new ActionTool();
        //btnBackShift = (ImageButton) findViewById(R.id.btn_back_shift);

        /*Set View*/

        /*Get Value*/

        /*Listener*/
        actionTool.backClickListener(this, btnBackShift);

    }
}
