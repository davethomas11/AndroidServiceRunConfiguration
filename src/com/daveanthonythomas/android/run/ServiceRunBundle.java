package com.daveanthonythomas.android.run;

import com.intellij.CommonBundle;
import com.intellij.reference.SoftReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

public class ServiceRunBundle {
    @NonNls
    private static final String BUNDLE_NAME = "messages.ServiceRunConfig";
    private static Reference<ResourceBundle> ourBundle;

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = SoftReference.dereference(ourBundle);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME);
            ourBundle = new java.lang.ref.SoftReference(bundle);
        }

        return bundle;
    }

    private ServiceRunBundle() {
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE_NAME) String key,
                                 @NotNull Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }
}
