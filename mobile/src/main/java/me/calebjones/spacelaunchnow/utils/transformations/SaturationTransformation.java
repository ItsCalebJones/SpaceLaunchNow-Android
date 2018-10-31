package me.calebjones.spacelaunchnow.utils.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import jp.wasabeef.glide.transformations.BitmapTransformation;


public class SaturationTransformation extends BitmapTransformation {

    private BitmapPool mBitmapPool;
    private float mSaturation;

    public SaturationTransformation(Context context, float saturation) {
        this(Glide.get(context).getBitmapPool(), saturation);
    }

    public SaturationTransformation(BitmapPool pool, float saturation) {
        mBitmapPool = pool;
        mSaturation = saturation;
    }

    @Override protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool,
                                         @NonNull Bitmap source, int outWidth, int outHeight) {
        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap.Config config =
                source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = mBitmapPool.get(width, height, config);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, config);
        }

        Canvas canvas = new Canvas(bitmap);
        ColorMatrix saturation = new ColorMatrix();
        saturation.setSaturation(mSaturation);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(saturation));
        canvas.drawBitmap(source, 0, 0, paint);

        return bitmap;
    }

    @Override public String key() {
        return "SaturationTransformation()";
    }
}
