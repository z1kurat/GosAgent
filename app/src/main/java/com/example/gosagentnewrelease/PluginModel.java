package com.example.gosagentnewrelease;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.yandex.mapkit.MapKitFactory;

public class PluginModel {
    public static PluginData Data = new PluginData();

    public static ReadData Reading = new ReadData();

    public static SetApiKey ApiKey = new SetApiKey();

    public static String getTableName() { return PluginData.TABLE_NAME + Data.MarkerType; }

    public static Bitmap getIconBitMap(Context context) {
        Drawable drawableResource = xmlToDrawable(context);
        return drawableToBitmap(drawableResource);
    }

    public static Drawable getIconDrawable(Context context) {
        return xmlToDrawable(context);
    }

    private static Drawable xmlToDrawable(Context context) {
        int vectorResId = getMarkerResource();
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return vectorDrawable;
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int getMarkerResource() {
        int icon;
        switch (Data.MarkerType) {
            case 1: icon = R.drawable.ic_museum; break;
            case 2: icon = R.drawable.ic_city_hall; break;
            case 3: icon = R.drawable.ic_hardware_store; break;
            case 4: icon = R.drawable.ic_bakery; break;
            case 5: icon = R.drawable.ic_finance; break;
            case 6: icon = R.drawable.ic_train_station; break;
            case 7: icon = R.drawable.ic_postal_code_prefix; break;
            case 8: icon = R.drawable.ic_political; break;
            case 9: icon = R.drawable.ic_electronics_store; break;
            case 10: icon = R.drawable.ic_aquarium; break;
            case 11: icon = R.drawable.ic_fishing_pier; break;
            case 12: icon = R.drawable.ic_bank; break;
            case 13: icon = R.drawable.ic_lawyer; break;
            case 14: icon = R.drawable.ic_general_contractor; break;
            case 15: icon = R.drawable.ic_volume_control_telephone; break;
            case 16: icon = R.drawable.ic_electrician; break;
            case 17: icon = R.drawable.ic_electronics_store; break;
            default: icon = R.drawable.ic_postal_code_prefix; break;
        }
        return icon;
    }

    public static void clear() {
        Data.clear();
        Reading.clear();
    }

     static class SetApiKey {
        private static boolean isActivate = false;

        public static void Activate() {
            if (isActivate)
                return;

            MapKitFactory.setApiKey(PluginData.MAPKIT_API_KEY);
            isActivate = true;
        }

    }

}


