package client.module.render;

import client.module.Category;
import client.module.Module;
import client.setting.BooleanSetting;

public class Optimization extends Module {
    private final BooleanSetting entitiesSetting;
    private final BooleanSetting particlesSetting;

    public Optimization() {
        super("Optimization", Category.RENDER);
        
        this.entitiesSetting = new BooleanSetting("", "Скрыть сущности", true);
        this.particlesSetting = new BooleanSetting("", "Скрыть частицы", true);
        
        this.addSetting(this.entitiesSetting);
        this.addSetting(this.particlesSetting);
    }

    @Override
    public void onEnable() {
        // Здесь можно добавить отключение лишних систем
    }

    @Override
    public void onDisable() {
    }

    public boolean hideEntities() {
        return this.entitiesSetting.isFlag3();
    }

    public boolean hideParticles() {
        return this.particlesSetting.isFlag3();
    }
}