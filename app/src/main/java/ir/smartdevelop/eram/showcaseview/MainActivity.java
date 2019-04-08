package ir.smartdevelop.eram.showcaseview;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import smartdevelop.ir.eram.showcaseviewlib.GuideSequence;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.GuideWrapperView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.utils.Position;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    View view1;
    View view2;
    View view3;
    View view4;
    View view5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);


        showTooltips();

        view3.setOnClickListener(this);
    }

    private void showTooltips(){
        GuideView guideView1 = new GuideView.Builder(this)
                .setTitle("Guide Title Text 1")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setDismissType(DismissType.Anywhere)
                .setGravity(Gravity.Center)
                .setTargetView(view1)
                .setPosition(Position.Left)
                .overrideYMessage(40)
                .build();

        GuideView guideView2 = new GuideView.Builder(this)
                .setTitle("Guide Title Text 2")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setDismissType(DismissType.Anywhere)
                .setGravity(Gravity.Center)
                .setTargetView(view2)
                .setPosition(Position.Right)
                .overrideYMessage(-60)
                .build();

        GuideView guideView3 = new GuideView.Builder(this)
                .setTitle("Guide Title Text 3")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setTargetView(view3)
                .setGravity(Gravity.Center)
                .setPosition(Position.Bottom)
                .overrideYMessage(150)
                .overrideXMessage(-40)
                .build();

        float h = 0;
        GuideView guideView4 = new GuideView.Builder(this)
                .setTitle("Guide Title Text 4")
                .setTargetView(view4)
                .setGravity(Gravity.Center)
                .setPosition(Position.Bottom)
                .overrideTargetHeight(h)
                .overrideYTarget(-h/2)
                .build();

        GuideView guideView5 = new GuideView.Builder(this)
                .setTitle("Guide Title Text 2")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setDismissType(DismissType.Anywhere)
                .setGravity(Gravity.Center)
                .setTargetView(view2)
                .setPosition(Position.Right)
                .overrideYMessage(-60)
                .build();

        GuideView guideView6 = new GuideView.Builder(this)
                .setTitle("Guide Title Text 3")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setTargetView(view3)
                .setGravity(Gravity.Center)
                .setPosition(Position.Bottom)
                .overrideYMessage(150)
                .overrideXMessage(-40)
                .build();

        new GuideSequence.Builder()
                .addSequence(new GuideWrapperView.Builder(this)
                        .setTargetGuideView(guideView2)
                        .setTargetGuideView(guideView1)
                        .setTargetGuideView(guideView3)
                        .setTargetGuideView(guideView4)
                        .build())
                .addSequence(new GuideWrapperView.Builder(this)
                        .setTargetGuideView(guideView5)
                        .build())
                .addSequence(new GuideWrapperView.Builder(this)
                        .setTargetGuideView(guideView6)
                        .build())
                .build(this)
                .show();
    }

    @Override
    public void onClick(View v) {
        showTooltips();
    }
}
