package me.calebjones.spacelaunchnow.common.content.util;

import android.content.Context;

import java.util.ArrayList;

import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;

public class FilterBuilder {

    public static String getLocationIds(Context context) {
        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);

        ArrayList<Integer> location_ids = new ArrayList<Integer>();

        if (switchPreferences.getSwitchCASC()) {
            location_ids.add(17);
            location_ids.add(19);
            location_ids.add(8);
            location_ids.add(16);
        }

        if (switchPreferences.getSwitchISRO()) {
            location_ids.add(14);
        }

        if (switchPreferences.getSwitchKSC()) {
            location_ids.add(27);
            location_ids.add(12);
        }

        if (switchPreferences.getSwitchRussia()) {
            location_ids.add(15);
            location_ids.add(5);
            location_ids.add(6);
            location_ids.add(18);
        }

        if (switchPreferences.getSwitchVan()) {
            location_ids.add(11);
        }

        if (switchPreferences.getSwitchWallops()) {
            location_ids.add(21);
        }

        if (switchPreferences.getSwitchNZ()) {
            location_ids.add(10);
        }

        if (switchPreferences.getSwitchJapan()) {
            location_ids.add(24);
            location_ids.add(26);
        }

        if (switchPreferences.getSwitchFG()) {
            location_ids.add(13);
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

        if (switchPreferences.getSwitchISRO()) {
            lsp_ids.add(31);
        }

        if (switchPreferences.getSwitchBO()) {
            lsp_ids.add(141);
        }

        if (switchPreferences.getSwitchRL()) {
            lsp_ids.add(147);
        }

        if (switchPreferences.getSwitchNorthrop()) {
            lsp_ids.add(257);
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
