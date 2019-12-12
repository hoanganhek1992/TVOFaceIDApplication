package com.example.tvofaceidapplication.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.R;

public class BaseToolbar {

    public static final int TITLE_ALIGN_CODE_LEFT = 0;
    public static final int TITLE_ALIGN_CODE_CENTER = 1;
    public static final int TITLE_ALIGN_CODE_RIGHT = 2;

    private Toolbar toolbar;
    private ImageView toolbar_leftIcon;
    private TextView toolbar_title;

    BaseToolbar(Toolbar toolbar) {
        try {
            this.toolbar = toolbar;
            this.toolbar_leftIcon = this.toolbar.findViewById(R.id.toolbar_icon);
            this.toolbar_title = this.toolbar.findViewById(R.id.toolbar_title);
        } catch (Exception ignored) {
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public void onSetTitle(String title) {
        toolbar_title.setText(title);
    }

    public void onHideTitle() {
        toolbar_title.setText("");
    }

    public void setToolbar_leftIcon(int icon_id) {
        if (toolbar_leftIcon != null) {
            toolbar_leftIcon.setImageResource(icon_id);
            toolbar_leftIcon.setVisibility(View.VISIBLE);
        }
    }

    public void setLeftIconOnclick(final LeftIconClickCallback callback) {
        toolbar_leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onLeftIconClick();
            }
        });
    }

    public void setTitleAlign(int number) {
        switch (number) {
            case TITLE_ALIGN_CODE_LEFT:
                toolbar_title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                break;
            case TITLE_ALIGN_CODE_CENTER:
                toolbar_title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                break;
            case TITLE_ALIGN_CODE_RIGHT:
                toolbar_title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
        }
    }

    public interface LeftIconClickCallback {
        void onLeftIconClick();
    }
}
