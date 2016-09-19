package vymajoris.roboq;

import android.graphics.Bitmap;
import android.graphics.Color;


/**
 * Created by VyMajoriss on 9/7/2016.
 */

public class BitMapUtil {
    public static Bitmap changeColor(Bitmap bit, int oldColor, int newColor) {
        int width =  bit.getWidth();
        int height = bit.getHeight();
        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int [] allpixels = new int [ myBitmap.getHeight()*myBitmap.getWidth()];
        bit.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(),myBitmap.getHeight());
        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);
        for(int i =0; i<myBitmap.getHeight()*myBitmap.getWidth();i++){
            if( allpixels[i] == oldColor)

                allpixels[i] = Color.alpha(newColor);
        }
        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        return myBitmap;
    }

}
