package xplore.dialogfragment;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


/**
 * Explorar os recursos da ImageView para criar uma CustomView Redimensionavel
 *
 * Esse projeto possui a classe {@link ZoomableImageView} que ja faz isso, mas
 * para fim de estudo/exploracao essa classe tem diversos metodos que sao so
 * para exploracao.
 *
 * No final essa classe tera acabara da mesma forma, mas vamos comecar do zero
 * para um bom aprendizado
 *
 * */

public class ResizableImageViewLab extends AppCompatImageView {

    public ResizableImageViewLab(Context context) {
        super(context);
    }

    public ResizableImageViewLab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableImageViewLab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void setImageLevel(int level) {
        super.setImageLevel(level);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
    }

    @Override
    public ScaleType getScaleType() {
        return super.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return super.getImageMatrix();
    }
}
