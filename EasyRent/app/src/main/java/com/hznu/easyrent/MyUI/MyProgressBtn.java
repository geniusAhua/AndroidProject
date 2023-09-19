package com.hznu.easyrent.MyUI;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.hznu.easyrent.R;

@SuppressLint("AppCompatCustomView")
public class MyProgressBtn extends Button {

    private int width;
    private int height;

    private GradientDrawable backDrawable;

    private boolean isMorphing;
    private int startAngle;
    private String text;

    private Paint paint;
    private ValueAnimator arcValueAnimator;
    private AnimatorSet animatorSet;
    private Context context;

    public MyProgressBtn(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public MyProgressBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public MyProgressBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    private void init(Context context){
        isMorphing = false;
        backDrawable = new GradientDrawable();

        Drawable drawable = getBackground();
        if(drawable != null){
            backDrawable = (GradientDrawable)drawable;
        }
        else{
            int colorDrawable = context.getColor(R.color.cutePink);
            backDrawable.setColor(colorDrawable);
        }
        backDrawable.setCornerRadius(24);
        setBackground(backDrawable);

        if(text != null){
            setText(text);
        }else {
            setText(getText());
            text = getText().toString();
        }

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.white));
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }

        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }
    }

    public void startAnim(){
        isMorphing = true;

        setText("");

        ValueAnimator valueAnimator = ValueAnimator.ofInt(width, height);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                int leftOffset = (width - value) / 2;
                int rightOffset = width - leftOffset;

                backDrawable.setBounds(leftOffset, 0, rightOffset, height);
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(backDrawable, "cornerRadius", 24, height / 2);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator, objectAnimator);
        animatorSet.start();

        //画中间的圆圈
        showArc();
    }

    public void gotoNew(){
        isMorphing = false;

        if(arcValueAnimator != null)
            arcValueAnimator.cancel();
        setVisibility(GONE);
    }

    public void backtoOrigin(){
        isMorphing = false;

        arcValueAnimator.cancel();
        animatorSet.reverse();

        init(context);
        invalidate();
    }

    public void regainBackground(){
        setVisibility(VISIBLE);
        backDrawable.setBounds(0, 0, width, height);
        backDrawable.setCornerRadius(24);
        setBackground(backDrawable);
        setText(text);
        isMorphing = false;
    }

    private void showArc(){
        arcValueAnimator = ValueAnimator.ofInt(0, 1000);
        arcValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startAngle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        arcValueAnimator.setInterpolator(new LinearInterpolator());
        arcValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        arcValueAnimator.setDuration(3000);
        arcValueAnimator.start();
    }

    @Override
    protected void onDraw(final Canvas canvas){
        super.onDraw(canvas);

        if(isMorphing){
            final RectF rectF = new RectF(getWidth()*5/12,getHeight()/7,getWidth()*7/12,getHeight()-getHeight()/7);
            canvas.drawArc(rectF, startAngle, 270, false, paint);
        }
    }
}
