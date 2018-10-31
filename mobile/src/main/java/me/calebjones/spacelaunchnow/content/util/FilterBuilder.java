package me.calebjones.spacelaunchnow.content.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;

public class FilterBuilder {

    public static String getLocationIds(Context context) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);

        ArrayList<Integer> location_ids = new ArrayList<Integer>();

        if (switchPreferences.getSwitchNasa()) {
            location_ids.add(44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            location_ids.add(115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            location_ids.add(121);
        }

        if (switchPreferences.getSwitchULA()) {
            location_ids.add(124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            location_ids.add(111);
            location_ids.add(163);
            location_ids.add(63);
        }
        if (switchPreferences.getSwitchCASC()) {
            location_ids.add(88);
        }

        if (switchPreferences.getSwitchISRO()) {
            location_ids.add(31);
        }

        if (switchPreferences.getSwitchKSC()) {
            location_ids.add(16);
            location_ids.add(17);
        }

        if (switchPreferences.getSwitchKSC()) {

        }

        if (switchPreferences.getSwitchPles()) {
            location_ids.add(11);
        }

        if (switchPreferences.getSwitchVan()) {
            location_ids.add(18);
        }
        return listToString(location_ids);
    }

    public static String getLSPIds(Context context) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);

        ArrayList<Integer> lsp_ids = new ArrayList<Integer>();

        if (switchPreferences.getSwitchNasa()) {
            lsp_ids.add(44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            lsp_ids.add(115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            lsp_ids.add(121);
        }

        if (switchPreferences.getSwitchULA()) {
            lsp_ids.add(124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            lsp_ids.add(111);
            lsp_ids.add(163);
            lsp_ids.add(63);
        }
        if (switchPreferences.getSwitchCASC()) {
            lsp_ids.add(88);
        }

        if (switchPreferences.getSwitchISRO()) {
            lsp_ids.add(31);
        }

        return listToString(lsp_ids);
    }

    private static String listToString(ArrayList<Integer> list) {
        StringBuilder sb = new StringBuilder();
        if (list.size() > 1) {
            for (int i = list.size() - 1; i >= 0; i--) {
                int num = list.get(i);
                sb.append(num);
                if (i != 0) {
                    sb.append(",");
                }
            }
        } else if (list.size() == 1) {
            sb.append(list.get(0));
        } else {
            return null;
        }
        return  sb.toString();
    }
}
