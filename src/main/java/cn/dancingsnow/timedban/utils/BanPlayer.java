package cn.dancingsnow.timedban.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Calendar;

public class BanPlayer {

    private final String reason;
    private final Calendar calendar;
    private final int sec;

    public BanPlayer(int day, String reason) {
        this.calendar = Calendar.getInstance();
        this.sec = day * 24 * 60 * 60;
        this.reason = reason;
    }

    public Boolean isBaned() {
        Calendar now = Calendar.getInstance();
        if ((calendar.get(Calendar.SECOND) + sec) <= now.get(Calendar.SECOND)) {
            return Boolean.TRUE;
        }
        else return Boolean.FALSE;
    }

    public String getReason() {
        return reason;
    }

    private String getTimeStr(Calendar calendar) {
        String ret = "";

        String hour;
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            hour = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        }else {
            hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        }
        String minute;
        if (calendar.get(Calendar.MINUTE) < 10) {
            minute = "0" + calendar.get(Calendar.MINUTE);
        }else {
            minute = String.valueOf(calendar.get(Calendar.MINUTE));
        }

        String second;
        if (calendar.get(Calendar.SECOND) < 10) {
            second = "0" + calendar.get(Calendar.SECOND);
        }else {
            second = String.valueOf(calendar.get(Calendar.SECOND));
        }
        ret += calendar.get(Calendar.YEAR) + "\u5e74" + (calendar.get(Calendar.MONTH) + 1) + "\u6708" + calendar.get(Calendar.DAY_OF_MONTH) + "\u65e5 " + hour + ":" + minute + ":" + second;
        return ret;
    }

    public Component getBanMsg() {
        Calendar deBanTime = (Calendar) calendar.clone();
        deBanTime.add(Calendar.SECOND, sec);
        return Component.text("\u4f60\u5df2\u7ecf\u88ab\u5c01\u7981").color(NamedTextColor.RED)
                .append(Component.text("\n").append(Component.text("\u89e3\u7981\u65f6\u95f4: " + getTimeStr(deBanTime)).color(NamedTextColor.AQUA)));
    }
}
