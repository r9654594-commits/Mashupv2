package client.module.client;

import client.api.Theme;
import client.concurrent.SystemClient;
import client.gui.widget.RenderElement;
import client.module.Feature;
import client.module.Module;
import client.render.ShapeShader;
import client.render.TextShader;
import client.setting.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
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
    public String getString() { return "Модули (Листалка)"; }

    @Override
    public String getString3() { return "scroller"; }

    @Override
    public float getFloat9() { return 40.0F; }

    @Override
    public float getFloat10() { return 150.0F; }

    @Override
    protected boolean isHudModule(HudModule hudModule) { return true; }

    @Override
    protected void onHudModuleBoolean(HudModule hudModule, boolean flag) {}

    @Override
    protected void onFloatDrawContextMatrix4fFloat(float value, DrawContext drawContext, Matrix4f matrix4f, float value2) {
        if (Feature.mc == null || Feature.mc.player == null) return;

        SystemClient client = SystemClient.getInstance();
        if (client == null || client.getModuleRegistry() == null) return;

        List<Module> activeModules = client.getModuleRegistry().getList22();
        if (activeModules.isEmpty()) return;

        if (currentIndex >= activeModules.size()) currentIndex = 0;

        float x = this.getValue260();
        float y = this.getValue261();
        float w = this.getFloat10() * value2;
        float h = this.getFloat9() * value2;

        if (this.showBackground.isFlag3()) {
            int bgColor = (int) (Theme.background() * (value * 0.8F)) << 24;
            ShapeShader.onFloatFloatIntMatrix4fFloatFloatFloatFloat(6.0F, x, bgColor, matrix4f, w, h, value2, y);
        }

        Module currentModule = activeModules.get(currentIndex);
        String text = currentModule.getName();
        String pageText = (currentIndex + 1) + "/" + activeModules.size();

        TextShader.update2();
        float textWidth = TextShader.getFloatByStringFloat(text, 14.0F);
        TextShader.onFloatFloatIntFloatFloatStringMatrix4f(x + (w - textWidth) / 2.0F, y + h / 2.0F - 4.0F, Theme.foreground(), 14.0F, value * value2, text, matrix4f);
        
        float pageWidth = TextShader.getFloatByStringFloat(pageText, 10.0F);
        TextShader.onFloatFloatIntFloatFloatStringMatrix4f(x + (w - pageWidth) / 2.0F, y + h / 2.0F + 8.0F, Theme.mutedFg(), 10.0F, value * value2, pageText, matrix4f);

        if (Feature.mc.currentScreen == null) {
            // Получаем реальные координаты мыши (с учетом масштаба окна)
            double mouseX = Feature.mc.mouse.getX();
            double mouseY = Feature.mc.mouse.getY();
            double scaledMouseX = mouseX * Feature.mc.getWindow().getScaledWidth() / Feature.mc.getWindow().getWidth();
            double scaledMouseY = mouseY * Feature.mc.getWindow().getScaledHeight() / Feature.mc.getWindow().getHeight();

            boolean overPanel = scaledMouseX >= x && scaledMouseX <= x + w && scaledMouseY >= y && scaledMouseY <= y + h;

            // Используем GLFW для проверки нажатия
            long handle = Feature.mc.getWindow().getHandle();
            boolean isLeftPressed = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

            if (overPanel && !isLeftPressed && !isTouching) {
                // Одиночный клик (просто тапнули)
            }

            if (isLeftPressed) {
                if (!isTouching && overPanel) {
                    isTouching = true;
                    lastTouchX = (float) scaledMouseX;
                } else if (isTouching) {
                    float deltaX = (float) scaledMouseX - lastTouchX;
                    if (Math.abs(deltaX) > 30.0F) {
                        if (deltaX > 0) {
                            currentIndex--;
                            if (currentIndex < 0) currentIndex = activeModules.size() - 1;
                        } else {
                            currentIndex++;
                            if (currentIndex >= activeModules.size()) currentIndex = 0;
                        }
                        lastTouchX = (float) scaledMouseX;
                    }
                }
            } else {
                if (isTouching) {
                    // Если отпустили палец, и он не сдвинулся сильно — считаем это кликом
                    isTouching = false;
                    if (Math.abs((float) scaledMouseX - lastTouchX) < 10.0F && overPanel) {
                        currentModule.toggle();
                    }
                }
            }
        }
    }
}