package com.example.administrator.thumbtofull;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by Lei Xiaoyue on 2015-11-12.
 */
public class ThumbToFullActivity extends Activity implements View.OnClickListener{
    private ImageView mImageView1, mImageView2, mImageView3, mImageView4;
    private ImageView mExpandedImageView;

    private View mOriginalView;

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private float mScale;

    private final static int TEST_LONG_DURATION = 2000;

    final Rect mStartBounds = new Rect();
    final Rect mEndBounds = new Rect();
    final Point mStartOffset = new Point();
    final Point mGlobalOffset = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        initView();
        addListener();
    }

    private void initView() {
        mImageView1 = (ImageView) findViewById(R.id.image1);
        mImageView2 = (ImageView) findViewById(R.id.image2);
        mImageView3 = (ImageView) findViewById(R.id.image3);
        mImageView4 = (ImageView) findViewById(R.id.image4);
        mExpandedImageView = (ImageView) findViewById(R.id.expanded_view);
    }

    private void addListener() {
        mImageView1.setOnClickListener(this);
        mImageView2.setOnClickListener(this);
        mImageView3.setOnClickListener(this);
        mImageView4.setOnClickListener(this);
        mExpandedImageView.setOnClickListener(this);
    }

    private void zoomImageFromThumb(final View thumbView){
        if(mCurrentAnimator != null){
            mCurrentAnimator.cancel();
        }

        mExpandedImageView.setImageDrawable(((ImageView)thumbView).getDrawable());

        thumbView.getGlobalVisibleRect(mStartBounds,mStartOffset);
        findViewById(R.id.container).getGlobalVisibleRect(mEndBounds,mGlobalOffset);
        mStartBounds.offset(-mGlobalOffset.x,-mGlobalOffset.y);
        mEndBounds.offset(-mGlobalOffset.x,-mGlobalOffset.y);

        mExpandedImageView.setVisibility(View.VISIBLE);
        mExpandedImageView.setPivotX(0f);
        mExpandedImageView.setPivotY(0f);
        if ((float) mEndBounds.width() / mEndBounds.height()
                > (float) mStartBounds.width() / mStartBounds.height()) {
            // Extend start bounds horizontally
            mScale = (float) mStartBounds.height() / mEndBounds.height();
        } else {
            // Extend start bounds vertically
            mScale = (float) mStartBounds.width() / mEndBounds.width();
        }
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(mExpandedImageView, View.X, mStartBounds.left, mEndBounds.left))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, View.Y, mStartBounds.right, mEndBounds.right))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, View.Y, mStartBounds.top, mEndBounds.top))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, View.SCALE_X,mScale,1f))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, View.SCALE_Y, mScale, 1f));
        set.setDuration(TEST_LONG_DURATION);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                thumbView.setVisibility(View.INVISIBLE);
                mOriginalView = thumbView;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    private void zoomImageToThumb(final View v){
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(v, View.X, mStartBounds.left))
                .with(ObjectAnimator.ofFloat(v, View.Y, mStartBounds.top))
                .with(ObjectAnimator.ofFloat(v, View.SCALE_X, mScale))
                .with(ObjectAnimator.ofFloat(v, View.SCALE_Y, mScale));
        set.setDuration(TEST_LONG_DURATION);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mOriginalView.setVisibility(View.VISIBLE);
//                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
        mCurrentAnimator = set;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.expanded_view: {
                zoomImageToThumb(v);
                break;
            }
            case R.id.image1:
            case R.id.image2:
            case R.id.image3:
            case R.id.image4:{
                zoomImageFromThumb(v);
                break;
            }
        }
    }
}
