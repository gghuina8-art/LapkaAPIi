package com.rocket_autoskin;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class ClickerService extends AccessibilityService {

    private static ClickerService instance;

    @Override 
    public void onServiceConnected() { 
        instance = this; 
    }

    @Override 
    public void onAccessibilityEvent(AccessibilityEvent e) {}

    @Override 
    public void onInterrupt() {}

    public static boolean ready() { 
        return instance != null; 
    }

    // Главный метод клика (аналог pfc.click)
    public static void click(float x, float y) {
        if (instance == null) return;
        Path p = new Path();
        p.moveTo(x, y);
        GestureDescription.StrokeDescription stroke =
			new GestureDescription.StrokeDescription(p, 0, 30);
        GestureDescription gd = new GestureDescription.Builder()
			.addStroke(stroke).build();
        instance.dispatchGesture(gd, null, null);
    }
}
