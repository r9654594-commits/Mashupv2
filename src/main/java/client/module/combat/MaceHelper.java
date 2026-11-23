package client.module.combat;

import client.module.Category;
import client.module.Module;
import client.util.KeyBindings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class MaceHelper extends Module {
    private int originalSlot = -1;
    private int state = 0; // 0 - ожидание, 1 - ветер брошен, 2 - удар булавой
    private int tickCounter = 0;

    public MaceHelper() {
        super("MaceHelper", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        this.originalSlot = -1;
        this.state = 0;
        this.tickCounter = 0;
    }

    @Override
    public void onDisable() {
        this.originalSlot = -1;
        this.state = 0;
        this.tickCounter = 0;
    }

    @Override
    public void update3() { // Этот метод вызывается каждый тик (в System это обычно update3 или onTick)
        if (this.notInGame() || this.player() == null) return;
        if (this.originalSlot == -1) {
            this.originalSlot = this.inventory().selectedSlot;
        }

        if (this.state == 0) {
            int windSlot = findItem(Items.WIND_CHARGE);
            int maceSlot = findItem(Items.MACE);

            if (windSlot != -1 && maceSlot != -1) {
                // 1. Берем ветер
                this.inventory().selectedSlot = windSlot;
                // 2. Кидаем ветер (правый клик)
                KeyBindings.update6(); 
                
                this.state = 1;
                this.tickCounter = 0;
            } else {
                // Нет ветра или булавы — отменяем
                this.originalSlot = -1;
            }
        } else if (this.state == 1) {
            this.tickCounter++;
            // Ждем 2-3 тика (пока подлетим)
            if (this.tickCounter >= 3) {
                int maceSlot = findItem(Items.MACE);
                if (maceSlot != -1) {
                    // 3. Берем булаву
                    this.inventory().selectedSlot = maceSlot;
                    // 4. Бьем (левый клик)
                    KeyBindings.update(); 
                    this.state = 2;
                } else {
                    this.reset();
                }
            }
        } else if (this.state == 2) {
            this.tickCounter++;
            if (this.tickCounter >= 2) {
                // 5. Возвращаем слот
                if (this.originalSlot != -1) {
                    this.inventory().selectedSlot = this.originalSlot;
                }
                this.reset();
            }
        }
    }

    private void reset() {
        this.originalSlot = -1;
        this.state = 0;
        this.tickCounter = 0;
    }

    private int findItem(net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.inventory().getStack(i);
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }
}