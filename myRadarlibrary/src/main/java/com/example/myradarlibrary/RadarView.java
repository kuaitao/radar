package com.example.myradarlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.List;


public class RadarView extends View {

    private final static String TAG = RadarView.class.getSimpleName();

    private List<RadarData> dataList;
    private float expandValue;//扩大点击区域
    private int count;//网圈数
    private float angle;//弧度
    private float radius;//半径
    private float radiusMax;
    private float maxValue;

    private Paint mainPaint;//区画笔
    private Paint valuePaint;//数据区画笔
    private Paint mbPaint;
    private Paint textPaint;//文本画笔
    private Path linePath;
    private Path pathDot;
    private int mainColor;//区颜色
    private int valueColor;//数据区颜色
    private int textColor;//文本颜色

    private float mainLineWidth;//网线宽度dp
    private float valueLineWidth;//数据区边宽度dp
    private float valuePointRadius;//
    private float textSize;//字体大小sp
    private float targetDis ;//目标文字距离目标点的距离
    private String targetName;//目标的值的名称

    private int mWidth, mHeight;

    private Context context;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.netView, defStyleAttr, R.style.defaultradarstyleres);
        mainColor = attributes.getColor(R.styleable.netView_netColor, context.getColor(R.color.defNetColor));
        valueColor = attributes.getColor(R.styleable.netView_overlayColor, context.getColor(R.color.defOverlayColor));
        textColor = attributes.getColor(R.styleable.netView_textColor, context.getColor(R.color.defTextColor));
        textSize = attributes.getFloat(R.styleable.netView_textSize, textSize);
        count = attributes.getInteger(R.styleable.netView_count, count);
        maxValue = attributes.getFloat(R.styleable.netView_maxValue, maxValue);
        targetName = attributes.getString(R.styleable.netView_targetName);
        expandValue = attributes.getFloat(R.styleable.netView_maxValue, expandValue);
        targetDis =  attributes.getFloat(R.styleable.netView_targetDis, targetDis);
        valuePointRadius =  attributes.getFloat(R.styleable.netView_valuePointRadius, valuePointRadius);
        valueLineWidth=  attributes.getFloat(R.styleable.netView_valueLineWidth, valueLineWidth);
        mainLineWidth=  attributes.getFloat(R.styleable.netView_mainLineWidth, mainLineWidth);
        setup();
    }

    private void setup() {
        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setColor(mainColor);
        mainPaint.setStyle(Paint.Style.STROKE);

        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(valueColor);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mbPaint = new Paint();
        mbPaint.setAntiAlias(true);
        mbPaint.setColor(Color.GREEN);
        mbPaint.setStyle(Paint.Style.FILL_AND_STROKE);

         linePath = new Path();
         pathDot = new Path();

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));


    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //设置一个默认值，就是这个View的默认宽度为300，这个看我们自定义View的要求
        int result = dip2px(getContext(), 300);
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        if (specSize > getScreenWidth()) {
            result = getScreenWidth();
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = dip2px(getContext(), 300);
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;

        }
        if (specSize > getScreenWidth()) {
            result = getScreenWidth();
        }
        return result;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(h, w) / 2 * 0.7f;
        radiusMax = Math.min(h, w) / 2;
        mWidth = w;
        mHeight = h;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //中心点
        canvas.translate(mWidth / 2, mHeight / 2);
        drawSpiderweb(canvas);
        drawText(canvas);
        drawRegion(canvas);

    }

    /**
     * 绘制网
     *
     * @param canvas
     */
    private void drawSpiderweb(Canvas canvas) {

        mainPaint.setStrokeWidth(dip2px(getContext(), mainLineWidth));


        int circleNum;
        if (dataList.size() < 3) {
            //数据小于三条给个默认圈数
            circleNum = 5;
        } else {
            circleNum = count;
        }

        float r = radius / (circleNum - 1);//丝之间的间距


        for (int i = 0; i < circleNum; i++) {
            float curR = r * i;//当前半径

            for (int j = 0; j < circleNum; j++) {
                float x = (float) (curR * Math.sin(angle / 2 + angle * j));
                float y = (float) (curR * Math.cos(angle / 2 + angle * j));

                if (i == circleNum - 1) {//当绘制最后一环时绘制连接线
                    linePath.reset();
                    linePath.moveTo(0, 0);
                    linePath.lineTo(x, y);
                    canvas.drawPath(linePath, mainPaint);
                }
            }
            //绘制圆心
            canvas.drawCircle(0, 0, curR, mainPaint);
        }
    }

    /**
     * 绘制文本
     *
     * @param canvas
     */
    float dis;
    float fontHeight;

    private void drawText(Canvas canvas) {
        textPaint.setTextSize(sp2px(getContext(), textSize));
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        fontHeight = fontMetrics.descent - fontMetrics.ascent;

        for (int i = 0; i < count; i++) {

            float x = (float) ((radius + fontHeight * 1.5) * Math.sin(angle / 2 + angle * i));
            float y = (float) ((radius + fontHeight * 1.5) * Math.cos(angle / 2 + angle * i));
            String title = dataList.get(i).getTitle();
            dis = textPaint.measureText(title);//文本长度


            if (angle * i >= Math.PI / 2 && angle * i < 3 * Math.PI / 2.5) {

                canvas.drawText(title, x - dis / 2, y + fontHeight / 2, textPaint);
            } else {

                canvas.drawText(title, x - dis / 2, y, textPaint);
            }
        }


    }

    /**
     * 绘制区域
     *
     * @param canvas
     */
    private void drawRegion(Canvas canvas) {
        textPaint.setTextSize(sp2px(getContext(), textSize));
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        float dis = textPaint.measureText(targetName);//文本长度

        valuePaint.setStrokeWidth(dip2px(getContext(), valueLineWidth));

        valuePaint.setAlpha(255);
        pathDot.reset();

        for (int i = 0; i < count; i++) {
            double percent = dataList.get(i).getPercentage() / maxValue;
            double mbPercent= dataList.get(i).getMbPercentage() / maxValue;;
            double maxPercent =(maxValue+5)/ maxValue;

            //值原点
            float x = dotX(i,percent);
            float y = dotY(i,percent);

            //目标原点
            float mbx = dotX(i,mbPercent);
            float mby = dotY(i,mbPercent);

            //最大原点
            float zdbx =dotX(i,maxPercent);
            float zdby = dotY(i,maxPercent);


            if (i == 0) {
                pathDot.moveTo(x, y);
            } else {
                pathDot.lineTo(x, y);
            }
            //绘制小圆点
            canvas.drawCircle(x, y, dip2px(getContext(), valuePointRadius), valuePaint);
            //绘制目标小圆圈
            canvas.drawCircle(mbx, mby, dip2px(getContext(), valuePointRadius), mbPaint);


            if (angle * i >= 0 && angle * i < Math.PI / 2.5) {
                canvas.drawText(targetName, mbx + dip2px(getContext(), targetDis), mby + fontHeight / 2, textPaint);

            } else if (angle * i >= Math.PI / 2.5 && angle * i < Math.PI / 1.5) {
                canvas.drawText(targetName, mbx, mby + fontHeight / 2 + dip2px(getContext(), targetDis), textPaint);
            } else if (angle * i >= Math.PI / 1.5 && angle * i < Math.PI + 0.1) {
                canvas.drawText(targetName, mbx + dip2px(getContext(), targetDis), mby + fontHeight / 2, textPaint);
            } else if (angle * i >= Math.PI && angle * i < 4.4) {
                canvas.drawText(targetName, mbx, mby - dip2px(getContext(), targetDis), textPaint);
            } else {
                canvas.drawText(targetName, mbx + dip2px(getContext(), targetDis), mby + fontHeight / 2, textPaint);
            }


            //绘制两个点之间的线
            canvas.drawLine(x, y, mbx, mby, valuePaint);

            canvas.drawLine(mbx, mby, zdbx, zdby, mbPaint);
        }
        pathDot.close();
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(pathDot, valuePaint);
        valuePaint.setAlpha(128);
        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(pathDot, valuePaint);
    }

    private boolean isDataListValid() {
        return dataList != null && dataList.size() >= 3;
    }

    public void setDataList(List<RadarData> dataList) {

        this.dataList = dataList;
        count = dataList.size();//圈数等于数据个数
        angle = (float) (Math.PI * 2 / count);
        invalidate();

    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // invalidate();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:

                float x = event.getX();
                float y = event.getY();

                for (int i = 0; i < count; i++) {

                    float xIndex = (float) ((radius + fontHeight * 1.5) * Math.sin(angle / 2 + angle * i));


                    float yIndex;
                    float yIndexMiddle = (float) ((radius + fontHeight * 1.5) * Math.cos(angle / 2 + angle * i));
                    if (angle * i >= Math.PI / 2 && angle * i < 3 * Math.PI / 2.5) {

                        yIndex = yIndexMiddle + fontHeight / 2 + radiusMax;
                    } else {
                        yIndex = yIndexMiddle + radiusMax;
                    }

                    if (x > (radiusMax + (xIndex - dis / 2))-expandValue && x < (radiusMax + (xIndex - dis / 2) + dis)+expandValue && y > (yIndex - fontHeight)-expandValue && y < yIndex+expandValue) {

                        listner.onClick(i,dataList.get(i));

                    }

                }
                return true;

            case MotionEvent.ACTION_UP:
                return true;


        }
        return super.onTouchEvent(event);
    }

    private float dotX(int index,double percent){

        return (float) (radius * Math.sin(angle / 2 + angle * index) * percent);
    }
    private float dotY(int index,double percent){

        return (float) (radius * Math.cos(angle / 2 + angle * index) * percent);
    }
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private OnItemClickListner listner= null;
    public void setOnItemTextClick(OnItemClickListner listner){
       this.listner = listner;
    }
    public interface OnItemClickListner {
        void onClick(int index,RadarData radarData);
    }
}
