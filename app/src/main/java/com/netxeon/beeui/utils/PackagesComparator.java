package com.netxeon.beeui.utils;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.text.Collator;
import java.util.Comparator;

/**
 * sort the packages based on app's label
 */
public class PackagesComparator implements Comparator<ResolveInfo> {

    private PackageManager pm;

    public PackagesComparator(PackageManager pm){
        this.pm = pm;
    }

    @Override
    public int compare(ResolveInfo lhs, ResolveInfo rhs) {
        Collator collator = Collator.getInstance();
        return collator.compare(lhs.activityInfo.loadLabel(pm).toString(),
                rhs.activityInfo.loadLabel(pm).toString());
    }
}
