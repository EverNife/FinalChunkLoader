package br.com.finalcraft.finalchunkloader.config.rank;

import br.com.finalcraft.evernifecore.config.yaml.anntation.Loadable;
import br.com.finalcraft.evernifecore.config.yaml.anntation.Salvable;
import br.com.finalcraft.evernifecore.config.yaml.section.ConfigSection;

public class GroupLimiter implements Salvable {

    private final String rankName;
    private final int onlineOnly;
    private final int alwaysOn;
    private final String permission;

    public GroupLimiter(String rankName, int onlineOnly, int alwaysOn, String permission) {
        this.rankName = rankName;
        this.onlineOnly = onlineOnly;
        this.alwaysOn = alwaysOn;
        this.permission = permission;
    }

    public String getRankName() {
        return rankName;
    }

    public int getOnlineOnly() {
        return onlineOnly;
    }

    public int getAlwaysOn() {
        return alwaysOn;
    }

    public String getPermission() {
        return permission;
    }

    @Loadable
    public static GroupLimiter onConfigLoad(ConfigSection section) {
        String rankName     = section.getSectionKey();
        int onlineOnly      = section.getInt("onlineOnly");
        int alwaysOn        = section.getInt("alwaysOn");
        String permission   = section.getString("permission");

        return new GroupLimiter(
                rankName,
                onlineOnly,
                alwaysOn,
                permission
        );
    }

    @Override
    public void onConfigSave(ConfigSection section) {
        section.setCustomKeyIndex(rankName);
        section.setValue("onlineOnly", onlineOnly);
        section.setValue("alwaysOn", alwaysOn);
        section.setValue("permission", permission);
    }

}
