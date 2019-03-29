package ir.smartdevelop.eram.showcaseview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.GuideWrapperView;
import smartdevelop.ir.eram.showcaseviewlib.Position;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class MainActivity extends AppCompatActivity {

    View view1;
    View view2;
    View view3;
    View view4;
    View view5;
    private GuideView mGuideView;
    private GuideView.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);

//        new GuideView.Builder(this)
//                .setTitle("Guide Title Text")
//                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
//                .setGravity(Gravity.center)
//                .setSemitransparentBackground(true)
//                .setDismissType(DismissType.outside)
//                .setTargetView(view1)
//                .build()
//                .show();

//        builder = new GuideView.Builder(this)
//                .setTitle("Guide Title Text")
//                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
//                .setGravity(Gravity.center)
//                .setSemitransparentBackground(false)
//                .setDismissType(DismissType.outside)
//                .setTargetView(view1)
//                .setGuideListener(new GuideListener() {
//                    @Override
//                    public void onDismiss(View view) {
//                        switch (view.getId()) {
//                            case R.id.view1:
//                                builder.setTargetView(view2).build();
//                                break;
//                            case R.id.view2:
//                                builder.setTargetView(view3).build();
//                                break;
//                            case R.id.view3:
//                                builder.setTargetView(view4).build();
//                                break;
//                            case R.id.view4:
//                                builder.setTargetView(view5).build();
//                                break;
//                            case R.id.view5:
//                                return;
//                        }
//                        mGuideView = builder.build();
//                        mGuideView.show();
//                    }
//                });
//
//        mGuideView = builder.build();
//        mGuideView.show();

        GuideView guideView1 = new GuideView.Builder(this)
                .setTitle("Guide Title Text 1")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setDismissType(DismissType.outside)
                .setGravity(Gravity.center)
                .setTargetView(view1)
                .build();

        GuideView guideView2 = new GuideView.Builder(this)
                .setTitle("Guide Title Text 2")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setDismissType(DismissType.outside)
                .setGravity(Gravity.center)
                .setTargetView(view2)
                .build();

        GuideView guideView3 = new GuideView.Builder(this)
                .setTitle("Guide Title Text 3")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setTargetView(view3)
                .setGravity(Gravity.center)
                .setPosition(Position.Top)
                .build();

        GuideWrapperView sad = new GuideWrapperView.Builder(this)
                .setTargetGuideView(guideView1)
                .setTargetGuideView(guideView2)
                .setTargetGuideView(guideView3)
                .build();

        sad.show();

        //updatingForDynamicLocationViews();
    }

    private void updatingForDynamicLocationViews() {
        view4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mGuideView.updateGuideViewLocation();
            }
        });
    }

}
