package client.module.client;

import client.api.Theme;
import client.concurrent.SystemClient;
import client.gui.widget.RenderElement;
import client.gui.widget.UiContext;
import client.module.Feature;
import client.module.Module;
import client.render.ShapeShader;
import client.render.TextShader;
import client.setting.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import java.util.List;

public class ModuleScrollerHud extends RenderElement {
    private final BooleanSetting showBackground;
    private int currentIndex = 0;
    private float lastTouchX = 0;
    private boolean isTouching = false;

    public ModuleScrollerHud() {
        this.showBackground = new BooleanSetting("", "Фон", true);
        this.addSetting(this.showBackground);
    }

    @Override
    public String getString() {
        return "Модули (Листалка)";
    }

    @Override
    public String getString3() {
        return "scroller";
    }

    @Override
    public float getFloat9() {
        return 40.0F; // Высота
    }

    @Override
    public float getFloat10() {
        return 150.0F; // Ширина
    }

    @Override
    protected boolean isHudModule(HudModule hudModule) {
        return true; // Всегда включен, если добавлен
    }

    @Override
    protected void onHudModuleBoolean(HudModule hudModule, boolean flag) {
        // Заглушка
    }

    @Override
    protected void onFloatDrawContextMatrix4fFloat(float value, DrawContext drawContext, Matrix4f matrix4f, float value2) {
        if (Feature.mc == null || Feature.mc.player == null) return;

        SystemClient client = SystemClient.getInstance();
        if (client == null || client.getModuleRegistry() == null) return;

        List<Module> activeModules = client.getModuleRegistry().getList22();
        if (activeModules.isEmpty()) return;

        // Защита от выхода за пределы
        if (currentIndex >= activeModules.size()) currentIndex = 0;

        float x = this.getValue260(); // Позиция X
        float y = this.getValue261(); // Позиция Y
        float w = this.getFloat10() * value2;
        float h = this.getFloat9() * value2;

        // Отрисовка фона
        if (this.showBackground.isFlag3()) {
            int bgColor = (int) (Theme.background() * (value * 0.8F)) << 24;
            ShapeShader.onFloatFloatIntMatrix4fFloatFloatFloatFloat(6.0F, x, bgColor, matrix4f, w, h, value2, y);
        }

        // Отрисовка активного модуля
        Module currentModule = activeModules.get(currentIndex);
        String text = currentModule.getName();
        
        // Добавляем индикатор страницы (1/5)
        String pageText = (currentIndex + 1) + "/" + activeModules.size();

        TextShader.update2();
        // Рисуем название модуля по центру
        float textWidth = TextShader.getFloatByStringFloat(text, 14.0F);
        TextShader.onFloatFloatIntFloatFloatStringMatrix4f(x + (w - textWidth) / 2.0F, y + h / 2.0F - 4.0F, Theme.foreground(), 14.0F, value * value2, text, matrix4f);
        
        // Рисуем счетчик страниц
        float pageWidth = TextShader.getFloatByStringFloat(pageText, 10.0F);
        TextShader.onFloatFloatIntFloatFloatStringMatrix4f(x + (w - pageWidth) / 2.0F, y + h / 2.0F + 8.0F, Theme.mutedFg(), 10.0F, value * value2, pageText, matrix4f);

        // Логика свайпа (если ты касаешься этой панели)
        long time = UiContext.getTime();
        if (Feature.mc.currentScreen == null) { // Только в игре
            // Проверяем, зажата ли мышь/палец на этой панели
            boolean isHovered = UiContext.isMouseOver(x, y, w, h); // Предполагаем, что такой метод есть в UiContext
            
            // В реальном коде System надо использовать Feature.mc.mouse.getX() и getY()
            double mouseX = Feature.mc.mouse.getX();
            double mouseY = Feature.mc.mouse.getY();
            
            // Переводим координаты мыши в координаты игры (учитывая масштаб)
            double scaledMouseX = mouseX * Feature.mc.getWindow().getScaledWidth() / Feature.mc.getWindow().getWidth();
            double scaledMouseY = mouseY * Feature.mc.getWindow().getScaledHeight() / Feature.mc.getWindow().getHeight();

            boolean overPanel = scaledMouseX >= x && scaledMouseX <= x + w && scaledMouseY >= y && scaledMouseY <= y + h;

            if (overPanel) {
                if (Feature.mc.mouse.wasLeftButtonClicked() || Feature.mc.mouse.wasRightButtonClicked()) {
                    // Тапнули по панели — включаем/выключаем модуль
                    currentModule.toggle();
                }
            }

            // Свайп (если мышь зажата и двигается)
            if (Feature.mc.mouse.isLeftButtonPressed()) {
                if (!isTouching && overPanel) {
                    isTouching = true;
                    lastTouchX = (float) scaledMouseX;
                } else if (isTouching) {
                    float deltaX = (float) scaledMouseX - lastTouchX;
                    if (Math.abs(deltaX) > 30.0F) {
                        if (deltaX > 0) {
                            // Свайп вправо - предыдущий
                            currentIndex--;
                            if (currentIndex < 0) currentIndex = activeModules.size() - 1;
                        } else {
                            // Свайп влево - следующий
                            currentIndex++;
                            if (currentIndex >= activeModules.size()) currentIndex = 0;
                        }
                        lastTouchX = (float) scaledMouseX;
                    }
                }
            } else {
                isTouching = false;
            }
        }
    }
};
            }
        }
    }
}