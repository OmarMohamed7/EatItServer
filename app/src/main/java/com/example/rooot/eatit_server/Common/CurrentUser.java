package com.example.rooot.eatit_server.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.rooot.eatit_server.Model.Request;
import com.example.rooot.eatit_server.Model.User;
import com.example.rooot.eatit_server.Remote.IGeoCoordinates;
import com.example.rooot.eatit_server.Remote.RetrofitClient;

public class CurrentUser {

    public static User current_User;
    public static Request current_Request;

    public static final String baseUrl = "https://maps.googleapis.com";

    public static final String UPDATE = "Update";
    public static final String DELETE_CATEGORY = "Delete category";
    public static final String DELETE_ITEM = "Delete item";

    public static String convertCodeToStatus(String code){
        if(code.equals("0"))
            return "placed";
        else if (code.equals("1"))
            return "On the way";
        else
            return "Delivered";
    }

    public static IGeoCoordinates getGeoCodeServices(){

        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap , int newWidth , int newHeight){

        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth , newHeight , Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float) bitmap.getWidth();
        float scaleY = newHeight/(float) bitmap.getHeight();

        float pivotX=0 , pivotY = 0;

        Matrix scaledMatrix = new Matrix();
        scaledMatrix.setScale(scaleX , scaleY , pivotX , pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaledMatrix);
        canvas.drawBitmap(bitmap , 0 , 0 ,new Paint(Paint.FILTER_BITMAP_FLAG));


        return scaledBitmap;
    }


}
