package com.attendancesystem.activity;

import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;

import com.attendancesystem.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

/**
 * Created by srujan on 5/2/2018.
 */

public class ReadMeActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.first_slide_background)
                        .buttonsColor(R.color.first_slide_buttons)
                        .image(R.drawable.units)
                        .title("Units Module")
                        .description("Add and Edit the units from Unit module which you can later assign to students.")
                        .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.second_slide_background)
                .buttonsColor(R.color.second_slide_buttons)
                .image(R.drawable.user_profile)
                .title("Students Module")
                .description("Add and Edit students from Students module. You can assign a student to a particular subject.")
                .build());


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.third_slide_background)
                .buttonsColor(R.color.third_slide_buttons)
                .image(R.drawable.units)
                .title("Attendance Module")
                .description("For a selected Subject you can take attendance on a particular date.")
                .build());


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.first_slide_background)
                .buttonsColor(R.color.first_slide_buttons)
                .image(R.drawable.attendance_history)
                .title("Attendance History")
                .description("You can share the attendance data from history and you can also edit attendance for particular day.")
                .build());

    }
}
