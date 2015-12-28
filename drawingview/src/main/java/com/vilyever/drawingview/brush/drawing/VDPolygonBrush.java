package com.vilyever.drawingview.brush.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.vilyever.drawingview.model.VDDrawingPath;
import com.vilyever.drawingview.model.VDDrawingPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * VDPolygonBrush
 * AndroidDrawingView <com.vilyever.drawingview.brush>
 * Created by vilyever on 2015/10/21.
 * Feature:
 */
public class VDPolygonBrush extends VDShapeBrush {
    final VDPolygonBrush self = this;


    /* #Constructors */
    public VDPolygonBrush() {

    }

    public VDPolygonBrush(float size, int color) {
        this(size, color, FillType.Hollow);
    }

    public VDPolygonBrush(float size, int color, FillType fillType) {
        this(size, color, fillType, false);
    }

    public VDPolygonBrush(float size, int color, FillType fillType, boolean edgeRounded) {
        super(size, color, fillType, edgeRounded);
    }

    /* #Overrides */

    @Override
    public FillType getFillType() {
        return FillType.Hollow;
    }

    @Override
    public boolean isEdgeRounded() {
        return true;
    }

    @NonNull
    @Override
    public Frame drawPath(Canvas canvas, @NonNull VDDrawingPath drawingPath, @NonNull DrawingState state) {
        if (drawingPath.getPoints().size() > 1) {
            VDDrawingPoint beginPoint = drawingPath.getPoints().get(0);
            VDDrawingPoint lastPoint = drawingPath.getPoints().get(drawingPath.getPoints().size() - 1);
            List<VDDrawingPoint> endPoints = new ArrayList<>();

            int currentPointerID = beginPoint.pointerID;
            for (int i = 1; i < drawingPath.getPoints().size(); i++) {
                VDDrawingPoint drawingPoint = drawingPath.getPoints().get(i);
                if (drawingPoint.pointerID != currentPointerID) {
                    endPoints.add(drawingPath.getPoints().get(i - 1));
                    currentPointerID = drawingPoint.pointerID;
                }
            }
            endPoints.add(lastPoint);

            boolean requireMoreDetail = true;
            if (beginPoint.pointerID != lastPoint.pointerID
                    && Math.abs(beginPoint.x - lastPoint.x) < (16.0f + self.getSize())
                    && Math.abs(beginPoint.y - lastPoint.y) < (16.0f + self.getSize())) {
                endPoints.remove(lastPoint);
                endPoints.add(beginPoint);
                requireMoreDetail = false;
            }
            else if (state.isForceFinish()) {
                endPoints.add(beginPoint);
                requireMoreDetail = false;
            }

            RectF drawingRect = new RectF();
            drawingRect.left = beginPoint.x;
            drawingRect.top = beginPoint.y;
            drawingRect.right = beginPoint.x;
            drawingRect.bottom = beginPoint.y;

            for (int i = 0; i < endPoints.size(); i++) {
                VDDrawingPoint point = endPoints.get(i);
                drawingRect.left = Math.min(point.x, drawingRect.left);
                drawingRect.top = Math.min(point.y, drawingRect.top);
                drawingRect.right = Math.max(point.x, drawingRect.right);
                drawingRect.bottom = Math.max(point.y, drawingRect.bottom);
            }

            Frame pathFrame = self.makeFrameWithBrushSpace(drawingRect);
            pathFrame.requireMoreDetail = requireMoreDetail;

            if (state.isFetchFrame() || canvas == null) {
                return pathFrame;
            }

            Path path = new Path();
            path.moveTo(beginPoint.x, beginPoint.y);
            for (int i = 0; i < endPoints.size(); i++) {
                path.lineTo(endPoints.get(i).x, endPoints.get(i).y);
            }

            if (state.isCalibrateToOrigin()) {
                path.offset(-pathFrame.left, -pathFrame.top);
            }

            canvas.drawPath(path, self.getPaint());

            return pathFrame;
        }

        return Frame.EmptyFrame();
    }

    /* #Accessors */
     
    /* #Delegates */     
     
    /* #Private Methods */    
    
    /* #Public Methods */
    public static VDPolygonBrush defaultBrush() {
        return new VDPolygonBrush(5, Color.BLACK);
    }

    /* #Classes */

    /* #Interfaces */     
     
    /* #Annotations @interface */    
    
    /* #Enums */
}