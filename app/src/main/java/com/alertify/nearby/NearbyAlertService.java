package com.alertify.nearby;

import android.content.Context;
import com.alertify.nearby.FirebaseHelper;

public class NearbyAlertService {

    public static void sendAlert(Context context, String locationLink) {
        FirebaseHelper.sendAlert(locationLink);
    }
}
