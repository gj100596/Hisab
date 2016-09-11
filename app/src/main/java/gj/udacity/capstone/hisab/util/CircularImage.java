package gj.udacity.capstone.hisab.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircularImage implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = source.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size/2f;
        canvas.drawCircle(r, r, r, paint);

        source.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}