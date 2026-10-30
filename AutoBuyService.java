package com.rocket_autoskin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.view.WindowManager;

public class AutoBuyService extends Service {

    private static final String CH = "autoskin";
    private static final int REFRESH_MS = 10000;
    private static final int DELAY_MS   = 15;

    private ScreenAnalyzer analyzer = new ScreenAnalyzer();
    private Profile profile;
    private volatile boolean running = true;
    private MediaProjection projection;
    private ImageReader reader;
    private VirtualDisplay display;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        profile = ProfileManager.load(this);

        if (profile != null) {
            profile.slots = Prefs.getSlots(this);
        }

        if (profile == null) { 
            stopSelf(); 
            return START_NOT_STICKY; 
        }

        createChannel();
        startForeground(1, notif("Мониторинг запущен…"));

        MediaProjectionManager mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        int code = intent.getIntExtra("proj_code", 0);
        Intent data = intent.getParcelableExtra("proj_data");

        if (data == null) { 
            stopSelf(); 
            return START_NOT_STICKY; 
        }

        projection = mpm.getMediaProjection(code, data);
        startCapture();
        new Thread(new Runnable() {
				@Override
				public void run() {
					loop();
				}
			}).start();

        return START_STICKY;
    }

    private void startCapture() {
        Point size = new Point();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRealSize(size);
        profile.screenW = size.x;
        profile.screenH = size.y;

        reader = ImageReader.newInstance(size.x, size.y, PixelFormat.RGBA_8888, 2);
        display = projection.createVirtualDisplay("autoskin", size.x, size.y,
												  getResources().getDisplayMetrics().densityDpi,
												  DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
												  reader.getSurface(), null, null);
    }

    private void grabFrame() {
        Image img = reader.acquireLatestImage();
        if (img == null) return;
        Image.Plane plane = img.getPlanes()[0];
        Bitmap bmp = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(plane.getBuffer());
        analyzer.setFrame(bmp);
        img.close();
    }

    private void loop() {
        long lastRefresh = System.currentTimeMillis();

        while (running) {
            try {
                grabFrame();

                int maxSlots = profile.slots;
                if (profile.px(profile.slotHeightPct * maxSlots, profile.screenH) > profile.screenH) {
                    maxSlots = 3; 
                }

                for (int i = 0; i < maxSlots; i++) {
                    int offY = profile.px(profile.slotHeightPct * i, profile.screenH);
                    int lx = profile.px(profile.stickerLx, profile.screenW);
                    int ly = profile.px(profile.stickerLy, profile.screenH) + offY;
                    int rx = profile.px(profile.stickerRx, profile.screenW);
                    int ry = profile.px(profile.stickerRy, profile.screenH) + offY;

                    int contours = analyzer.getContoursCount(lx, ly, rx, ry);
                    int buyColor = analyzer.getColor(profile.buyPxX(), profile.buyPxY(i));

                    if (contours > 3 && buyColor < 0x11111111) {
                        ClickerService.click(profile.buyPxX(), profile.buyPxY(i));
                        sleep(DELAY_MS);

                        for (int k = 0; k < 3; k++) {
                            ClickerService.click(profile.px(profile.confirmX, profile.screenW), profile.px(profile.confirmY, profile.screenH));
                        }
                        sleep(40);

                        ClickerService.click(profile.px(profile.checkX, profile.screenW), profile.px(profile.checkY, profile.screenH));
                        sleep(60);

                        ClickerService.click(profile.px(profile.checkX, profile.screenW), profile.px(profile.checkY, profile.screenH));
                    }
                }

                if (analyzer.getColor(profile.px(profile.backX, profile.screenW), profile.px(profile.backY, profile.screenH)) < 0x1000000) {
                    ClickerService.click(profile.px(profile.backX, profile.screenW), profile.px(profile.backY, profile.screenH));
                    sleep(40);
                }

                int checkColor = analyzer.getColor(profile.px(profile.checkX, profile.screenW), profile.px(profile.checkY, profile.screenH));
                if (checkColor < 0x10000000) {
                    ClickerService.click(profile.px(profile.checkX, profile.screenW), profile.px(profile.checkY, profile.screenH));
                    sleep(60);
                }

                int okColor = analyzer.getColor(profile.px(profile.okX, profile.screenW), profile.px(profile.okY, profile.screenH));
                if (okColor < 0x10000000) {
                    ClickerService.click(profile.px(profile.okX, profile.screenW), profile.px(profile.okY, profile.screenH));
                    sleep(50);
                    ClickerService.click(profile.px(profile.checkX, profile.screenW), profile.px(profile.checkY, profile.screenH));
                    sleep(60);
                    ClickerService.click(profile.px(profile.checkX, profile.screenW), profile.px(profile.checkY, profile.screenH));
                    sleep(50);
                }

                if (System.currentTimeMillis() - lastRefresh > REFRESH_MS) {
                    lastRefresh = System.currentTimeMillis();
                    ClickerService.click(profile.px(profile.checkX, profile.screenW), profile.px(profile.checkY, profile.screenH));
                    sleep(40);
                    ClickerService.click(profile.px(profile.checkX, profile.screenW), profile.px(profile.checkY, profile.screenH));
                }

                sleep(50);
            } catch (Exception ignored) {}
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CH, "AutoSkin", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    private Notification notif(String s) {
        if (Build.VERSION.SDK_INT >= 26) {
            return new Notification.Builder(this, CH).setContentTitle("LAPKAAPI").setContentText(s).setSmallIcon(android.R.drawable.stat_notify_sync).build();
        } else {
            return new Notification.Builder(this).setContentTitle("LAPKAAPI").setContentText(s).setSmallIcon(android.R.drawable.stat_notify_sync).build();
        }
    }

    @Override public void onDestroy() { 
        running = false; 
        super.onDestroy(); 
    }

    @Override public IBinder onBind(Intent i) { 
        return null; 
    }
}
