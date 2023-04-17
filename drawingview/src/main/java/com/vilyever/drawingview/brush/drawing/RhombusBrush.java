package com.vilyever.drawingview.brush.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.vilyever.drawingview.R;
import com.vilyever.drawingview.model.DrawingPath;
import com.vilyever.drawingview.model.DrawingPoint;
import com.vilyever.resource.Resource;

/**
 * RhombusBrush
 * AndroidDrawingView <com.vilyever.drawingview.brush>
 * Created by vilyever on 2015/10/21.
 * Feature:
 * 菱形绘制
 */
public class RhombusBrush extends ShapeBrush {
    final RhombusBrush self = this;


    /* #Constructors */
    public RhombusBrush() {

    }

    public RhombusBrush(float size, int color) {
        this(size, color, FillType.Hollow);
    }

    public RhombusBrush(float size, int color, FillType fillType) {
        this(size, color, fillType, false);
    }

    public RhombusBrush(float size, int color, FillType fillType, boolean edgeRounded) {
        super(size, color, fillType, edgeRounded);
    }

    /* Public Methods */
    public static RectangleBrush defaultBrush() {
        return new RectangleBrush(Resource.getDimensionPixelSize(R.dimen.drawingViewBrushDefaultSize), Color.BLACK);
    }

    /* #Overrides */
    @Override
    protected void updatePaint() {
        super.updatePaint();

        if (!isEdgeRounded()) {
            getPaint().setStrokeMiter(Integer.MAX_VALUE);
            getPaint().setStrokeWidth(0);
            getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        }
    }

    @NonNull
    @Override
    public Frame drawPath(Canvas canvas, @NonNull DrawingPath drawingPath, @NonNull DrawingState state) {
        updatePaint();
        if (drawingPath.getPoints().size() > 1) {
            DrawingPoint beginPoint = drawingPath.getPoints().get(0);
            DrawingPoint lastPoint = drawingPath.getPoints().get(drawingPath.getPoints().size() - 1);

            RectF drawingRect = new RectF();
            drawingRect.left = Math.min(beginPoint.getX(), lastPoint.getX());
            drawingRect.top = Math.min(beginPoint.getY(), lastPoint.getY());
            drawingRect.right = Math.max(beginPoint.getX(), lastPoint.getX());
            drawingRect.bottom = Math.max(beginPoint.getY(), lastPoint.getY());

            if ((drawingRect.right - drawingRect.left) < getSize() * 2.0f
                    || (drawingRect.bottom - drawingRect.top) < getSize() * 2.0f) {
                return Frame.EmptyFrame();
            }

            //            锐角距差计算，改用新算法
            //            pathFrame = new RectF(drawingRect);
            //
            //            // 计算相似菱形上半三角形比例
            //            double x = pathFrame.right - pathFrame.left; // 底边
            //            double h = (pathFrame.bottom - pathFrame.top) / 2.0f; // 高
            //            double y = Math.sqrt((x / 2.0f) * (x / 2.0f) + h * h); // 斜边
            //            double sin = (x / 2.0f) / y; // 顶角角度一半的sin值
            //            double factor = (h + (getSize() / 2.0f) * (1 / sin)) / h; // 相似比
            //
            //            pathFrame.left -= x * (factor - 1) / 2.0f;
            //            pathFrame.top -= h * (factor - 1);
            //            pathFrame.right += x * (factor - 1) / 2.0f;
            //            pathFrame.bottom += h * (factor - 1);

            Path path = new Path();
            Frame pathFrame;

            if (isEdgeRounded()) {
                pathFrame = super.drawPath(canvas, drawingPath, state);
                if (state.isFetchFrame() || canvas == null) {
                    return pathFrame;
                }

                path.moveTo(drawingRect.left, (drawingRect.top + drawingRect.bottom) / 2.0f);
                path.lineTo((drawingRect.left + drawingRect.right) / 2.0f, drawingRect.bottom);
                path.lineTo(drawingRect.right, (drawingRect.top + drawingRect.bottom) / 2.0f);
                path.lineTo((drawingRect.left + drawingRect.right) / 2.0f, drawingRect.top);
                path.lineTo(drawingRect.left, (drawingRect.top + drawingRect.bottom) / 2.0f);
            } else {
                double w = getSize() / 2.0; // 内外间距
                double x = drawingRect.right - drawingRect.left; // 上三角形底边
                double h = (drawingRect.bottom - drawingRect.top) / 2.0; // 上三角形高
                double a = Math.atan(x / 2.0 / h) * 2.0; // 顶角
                double b = Math.PI - a; // 侧角
                double dy = w / Math.sin(a / 2.0); // y差值
                double dx = w / Math.sin(b / 2.0); // x差值

                RectF outerRect = new RectF(drawingRect);
                outerRect.left -= dx;
                outerRect.top -= dy;
                outerRect.right += dx;
                outerRect.bottom += dy;

                RectF innerRect = new RectF(drawingRect);
                innerRect.left += dx;
                innerRect.top += dy;
                innerRect.right -= dx;
                innerRect.bottom -= dy;

                pathFrame = new Frame(outerRect);
                if (state.isFetchFrame() || canvas == null) {
                    return pathFrame;
                }

                path.moveTo(outerRect.left, (outerRect.top + outerRect.bottom) / 2.0f);
                path.lineTo((outerRect.left + outerRect.right) / 2.0f, outerRect.top);
                path.lineTo(outerRect.right, (outerRect.top + outerRect.bottom) / 2.0f);
                path.lineTo((outerRect.left + outerRect.right) / 2.0f, outerRect.bottom);
                path.lineTo(outerRect.left, (outerRect.top + outerRect.bottom) / 2.0f);

                if (getFillType() == FillType.Hollow) {
                    path.lineTo(innerRect.left, (innerRect.top + innerRect.bottom) / 2.0f);
                    path.lineTo((innerRect.left + innerRect.right) / 2.0f, innerRect.bottom);
                    path.lineTo(innerRect.right, (innerRect.top + innerRect.bottom) / 2.0f);
                    path.lineTo((innerRect.left + innerRect.right) / 2.0f, innerRect.top);
                    path.lineTo(innerRect.left, (innerRect.top + innerRect.bottom) / 2.0f);

                    path.lineTo(outerRect.left, (outerRect.top + outerRect.bottom) / 2.0f);
                }
            }

            if (state.isCalibrateToOrigin()) {
                path.offset(-pathFrame.left, -pathFrame.top);
            }

            canvas.drawPath(path, getPaint());

            return pathFrame;
        }

        return Frame.EmptyFrame();
    }

}