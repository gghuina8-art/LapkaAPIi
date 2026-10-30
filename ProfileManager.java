package com.rocket_autoskin;

import android.content.Context;
import org.json.JSONObject;

public class ProfileManager {
    private static final String FILE = "profile.json";

    public static void save(Context c, Profile p) {
        try {
            java.io.FileOutputStream fos = c.openFileOutput(FILE, Context.MODE_PRIVATE);
            JSONObject o = new JSONObject();
            o.put("w", p.screenW); o.put("h", p.screenH);
            o.put("buyX", p.buyX); o.put("buyY", p.buyY);
            o.put("confirmX", p.confirmX); o.put("confirmY", p.confirmY);
            o.put("slx", p.stickerLx); o.put("sly", p.stickerLy);
            o.put("srx", p.stickerRx); o.put("sry", p.stickerRy);
            o.put("chx", p.checkX); o.put("chy", p.checkY);
            o.put("okx", p.okX); o.put("oky", p.okY);
            o.put("bkx", p.backX); o.put("bky", p.backY);
            o.put("shp", p.slotHeightPct);
            fos.write(o.toString().getBytes());
            fos.close();
        } catch (Exception ignored) {}
    }

    public static Profile load(Context c) {
        try {
            java.io.FileInputStream fis = c.openFileInput(FILE);
            byte[] buf = new byte[fis.available()];
            fis.read(buf);
            fis.close();
            JSONObject o = new JSONObject(new String(buf));
            Profile p = new Profile();
            p.screenW = o.getInt("w"); p.screenH = o.getInt("h");
            p.buyX = (float) o.getDouble("buyX"); p.buyY = (float) o.getDouble("buyY");
            p.confirmX = (float) o.getDouble("confirmX"); p.confirmY = (float) o.getDouble("confirmY");
            p.stickerLx = (float) o.getDouble("slx"); p.stickerLy = (float) o.getDouble("sly");
            p.stickerRx = (float) o.getDouble("srx"); p.stickerRy = (float) o.getDouble("sry");
            p.checkX = (float) o.getDouble("chx"); p.checkY = (float) o.getDouble("chy");
            p.okX = (float) o.getDouble("okx"); p.okY = (float) o.getDouble("oky");
            p.backX = (float) o.getDouble("bkx"); p.backY = (float) o.getDouble("bky");
            p.slotHeightPct = (float) o.getDouble("shp");
            return p;
        } catch (Exception e) { return null; }
    }
}
