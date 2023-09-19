package com.hznu.easyrent.MyUI;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekView;

/**
 * 演示一个变态需求的周视图
 * Created by huanghaibin on 2018/2/9.
 */

public class MyWeekView extends WeekView {


    private int mRadius;

    /**
     * 自定义魅族标记的文本画笔
     */
    private Paint mTextPaint = new Paint();


    /**
     * 24节气画笔
     */
    private Paint mSolarTermTextPaint = new Paint();
    private Paint mSolarTermTextPaintNot;//非范围内的画笔

    /**
     * 背景圆点
     */
    private Paint mPointPaint = new Paint();

    /**
     * 今天的背景色
     */
    private Paint mCurrentDayPaint = new Paint();


    /**
     * 圆点半径
     */
    private float mPointRadius;

    private int mPadding;

    private float mCircleRadius;
    /**
     * 自定义魅族标记的圆形背景
     */
    private Paint mSchemeBasicPaint = new Paint();

    private float mSchemeBaseLine;

    /**
     * 今天，Calendar类型
     */
    private Calendar today = new Calendar();

    public MyWeekView(Context context) {
        super(context);

        /**
         * 需要通过系统时间获取当前日子
         */
        java.util.Calendar jc = java.util.Calendar.getInstance();
        today.setDay(jc.get(java.util.Calendar.DATE));
        today.setYear(jc.get(java.util.Calendar.YEAR));
        today.setMonth(jc.get(java.util.Calendar.MONTH) + 1);//java.util.Calendar，谷歌提供的calendar月份从0开始计算的

        mTextPaint.setTextSize(dipToPx(context, 8));
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);


        mSolarTermTextPaint.setColor(0xff489dff);
        mSolarTermTextPaint.setAntiAlias(true);
        mSolarTermTextPaint.setTextAlign(Paint.Align.CENTER);
        mSolarTermTextPaintNot = mSolarTermTextPaint;
        mSolarTermTextPaintNot.setColor(0x40489dff);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setFakeBoldText(true);
        mSchemeBasicPaint.setColor(Color.WHITE);

        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);
        mPointPaint.setColor(Color.RED);


        mCurrentDayPaint.setAntiAlias(true);
        mCurrentDayPaint.setStyle(Paint.Style.FILL);
        mCurrentDayPaint.setColor(0xffffffff);


        mCircleRadius = dipToPx(getContext(), 7);

        mPadding = dipToPx(getContext(), 3);

        mPointRadius = dipToPx(context, 2);

        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine = mCircleRadius - metrics.descent + (metrics.bottom - metrics.top) / 2 + dipToPx(getContext(), 1);

        //兼容硬件加速无效的代码
        setLayerType(View.LAYER_TYPE_SOFTWARE, mSelectedPaint);
        //4.0以上硬件加速会导致无效
        mSelectedPaint.setMaskFilter(new BlurMaskFilter(28, BlurMaskFilter.Blur.SOLID));

        setLayerType(View.LAYER_TYPE_SOFTWARE, mSchemeBasicPaint);
        mSchemeBasicPaint.setMaskFilter(new BlurMaskFilter(28, BlurMaskFilter.Blur.SOLID));
    }


    @Override
    protected void onPreviewHook() {
        mSolarTermTextPaint.setTextSize(mCurMonthLunarTextPaint.getTextSize());
        mSolarTermTextPaintNot.setTextSize(mCurMonthLunarTextPaint.getTextSize());
        mRadius = Math.min(mItemWidth, mItemHeight) / 11 * 5;
    }


    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        if(calendar.differ(today) >= 2 && calendar.differ(today) <= 30) {
            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
            return true;
        }
        else return false;
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {

        boolean isSelected = isSelected(calendar);
        if (isSelected) {
            mPointPaint.setColor(Color.WHITE);
        } else {
            mPointPaint.setColor(Color.GRAY);
        }

        canvas.drawCircle(x + mItemWidth / 2, mItemHeight - 3 * mPadding, mPointRadius, mPointPaint);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        int top = -mItemHeight / 6;

        /**
         * 调整今天的字体颜色
         * 浅红色,因为今天永远不是范围内的，但是要显示出是今天
         */
        mCurDayTextPaint.setColor(0x40ff0000);
        mCurDayLunarTextPaint.setColor(0x40ff0000);

        if (calendar.isCurrentDay() && !isSelected) {
            canvas.drawCircle(cx, cy, mRadius, mCurrentDayPaint);
        }

        if(hasScheme){
            canvas.drawCircle(x + mItemWidth - mPadding - mCircleRadius / 2, mPadding + mCircleRadius, mCircleRadius, mSchemeBasicPaint);

            mTextPaint.setColor(calendar.getSchemeColor());

            canvas.drawText(calendar.getScheme(), x + mItemWidth - mPadding - mCircleRadius, mPadding + mSchemeBaseLine, mTextPaint);
        }

        if (calendar.differ(today) >= 2 && calendar.differ(today) <= 30) {
            if (calendar.isWeekend() && calendar.isCurrentMonth()) {
                mCurMonthTextPaint.setColor(0xFF489dff);
                mCurMonthLunarTextPaint.setColor(0xFF489dff);
                mSchemeTextPaint.setColor(0xFF489dff);
                mSchemeLunarTextPaint.setColor(0xFF489dff);
                mOtherMonthLunarTextPaint.setColor(0xFF489dff);
                mOtherMonthTextPaint.setColor(0xFF489dff);
            } else {
                mCurMonthTextPaint.setColor(0xff333333);
                mCurMonthLunarTextPaint.setColor(0xffCFCFCF);
                mSchemeTextPaint.setColor(0xff333333);
                mSchemeLunarTextPaint.setColor(0xffCFCFCF);

                mOtherMonthTextPaint.setColor(0xFFe1e1e1);
                mOtherMonthLunarTextPaint.setColor(0xFFe1e1e1);
            }
        }
        else{//不再范围里的颜色变浅
            if (calendar.isWeekend() && calendar.isCurrentMonth()) {
                mCurMonthTextPaint.setColor(0x40489dff);
                mCurMonthLunarTextPaint.setColor(0x40489dff);
                mSchemeTextPaint.setColor(0x40489dff);
                mSchemeLunarTextPaint.setColor(0x40489dff);
                mOtherMonthLunarTextPaint.setColor(0x40489dff);
                mOtherMonthTextPaint.setColor(0x40489dff);
            } else {
                mCurMonthTextPaint.setColor(0x40333333);
                mCurMonthLunarTextPaint.setColor(0x40CFCFCF);
                mSchemeTextPaint.setColor(0x40333333);
                mSchemeLunarTextPaint.setColor(0x40CFCFCF);

                mOtherMonthTextPaint.setColor(0x40e1e1e1);
                mOtherMonthLunarTextPaint.setColor(0x40e1e1e1);
            }
        }

        if (calendar.differ(today) >= 2 && calendar.differ(today) <= 30) {
            if (isSelected) {
                canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                        mSelectTextPaint);
                canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + mItemHeight / 10, mSelectedLunarTextPaint);
            } else if (hasScheme) {

                canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                        calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

                canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + mItemHeight / 10,
                        !TextUtils.isEmpty(calendar.getSolarTerm()) ? mSolarTermTextPaint : mSchemeLunarTextPaint);
            } else {
                canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                        calendar.isCurrentDay() ? mCurDayTextPaint :
                                calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);

                canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + mItemHeight / 10,
                        calendar.isCurrentDay() ? mCurDayLunarTextPaint :
                                !TextUtils.isEmpty(calendar.getSolarTerm()) ? mSolarTermTextPaint :
                                        calendar.isCurrentMonth() ?
                                                mCurMonthLunarTextPaint : mOtherMonthLunarTextPaint);
            }
        }
        else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);

            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + mItemHeight / 10,
                    calendar.isCurrentDay() ? mCurDayLunarTextPaint :
                            !TextUtils.isEmpty(calendar.getSolarTerm()) ? mSolarTermTextPaintNot :
                                    calendar.isCurrentMonth() ?
                                            mCurMonthLunarTextPaint : mOtherMonthLunarTextPaint);
        }

    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
