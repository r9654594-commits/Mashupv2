package dev.leg1tka.storage;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.utils.*;



import dev.leg1tka.modules.render.*;
import java.util.HashMap;
import java.util.Map;

public class LocalizationStorage {
   private final Map<String, String> en = new HashMap<>();
   private final Map<String, String> uk = new HashMap<>();
   private DisplaynameType_1 language = DisplaynameType_1.RUSSIAN;

   public LocalizationStorage() {
      this.add(
         "Combat",
         "Combat",
         "Бій"
      );
      this.add(
         "Movement",
         "Movement",
         "Рух"
      );
      this.add(
         "Render",
         "Render",
         "Візуал"
      );
      this.add(
         "Misc",
         "Misc",
         "Різне"
      );
      this.add(
         "Player",
         "Player",
         "Гравець"
      );
      this.add(
         "Язык",
         "Language",
         "Мова"
      );
      this.add(
         "Русский",
         "Russian",
         "Російська"
      );
      this.add(
         "Английский",
         "English",
         "Англійська"
      );
      this.add(
         "Украинский",
         "Ukrainian",
         "Українська"
      );
      this.add(
         "Sprint",
         "Sprint",
         "Спринт"
      );
      this.add(
         "AutoTotem",
         "Auto Totem",
         "Авто тотем"
      );
      this.add(
         "PacketMace",
         "Packet Mace",
         "Пакетна булава"
      );
      this.add(
         "Interface",
         "Interface",
         "Інтерфейс"
      );
      this.add(
         "GuiMove",
         "Gui Move",
         "Хода з інвентарем"
      );
      this.add(
         "NoVignette",
         "No Vignette",
         "Без віньєтки"
      );
      this.add(
         "Aura",
         "Aura",
         "Аура"
      );
      this.add(
         "ElytraBoost",
         "Elytra Boost",
         "Елітра буст"
      );
      this.add(
         "ElytraTarget",
         "Elytra Target",
         "Таргет елітри"
      );
      this.add(
         "FullBright",
         "Full Bright",
         "Повна яскравість"
      );
      this.add(
         "ElytraBounce",
         "Elytra Bounce",
         "Элитра баунс"
      );
      this.add(
         "PlayerFakeLags",
         "Fake Lag",
         "Фейк лаг"
      );
      this.add(
         "Chams",
         "Chams",
         "Чамси"
      );
      this.add(
         "ClientSounds",
         "Client Sounds",
         "Звуки клієнта"
      );
      this.add(
         "Cosmetics",
         "Cosmetics",
         "Косметика"
      );
      this.add(
         "ServerHelper",
         "Server Helper",
         "Сервер Хелпер"
      );
      this.add(
         "HPFixer",
         "HP Fixer",
         "Фікс HP"
      );
      this.add(
         "KTLeave",
         "KT Leave",
         "КТ лів"
      );
      this.add(
         "NoClip",
         "No Clip",
         "Без кліпу"
      );
      this.add(
         "Particles",
         "Particles",
         "Частинки"
      );
      this.add(
         "ElytraMotion",
         "Elytra Motion",
         "Рух елітри"
      );
      this.add(
         "HitBubbles",
         "Hit Bubbles",
         "Бульбашки удару"
      );
      this.add(
         "RPSpoofer",
         "RP Spoofer",
         "RP спуфер"
      );
      this.add(
         "Projectile",
         "Projectile",
         "Снаряд"
      );
      this.add(
         "AutoExplosion",
         "Auto Explosion",
         "Авто вибух"
      );
      this.add(
         "PacketCriticals",
         "Packet Criticals",
         "Пакетні кріти"
      );
      this.add(
         "EntityESP",
         "Entity ESP",
         "ESP сутностей"
      );
      this.add(
         "NoPush",
         "No Push",
         "Без штовхання"
      );
      this.add(
         "TPLoot",
         "TP Loot",
         "ТП лут"
      );
      this.add(
         "CutDetector",
         "Cut Detector",
         "Детектор кату"
      );
      this.add(
         "AirStuck",
         "Air Stuck",
         "Зависання в повітрі"
      );
      this.add(
         "Scanner",
         "Scanner",
         "Сканер"
      );
      this.add(
         "NoWeb",
         "No Web",
         "Без павутини"
      );
      this.add(
         "Cubes",
         "Cubes",
         "Куби"
      );
      this.add(
         "NoRender",
         "No Render",
         "Без рендера"
      );
      this.add(
         "SwingAnimations",
         "Swing Animations",
         "Анімації свінгу"
      );
      this.add(
         "ViewModel",
         "View Model",
         "Модель рук"
      );
      this.add(
         "TargetESP",
         "Target ESP",
         "ESP цілі"
      );
      this.add(
         "JumpCircle",
         "Jump Circle",
         "Коло стрибка"
      );
      this.add(
         "CustomWorld",
         "Custom World",
         "Світ"
      );
      this.add(
         "Krutaya Camera",
         "Krutaya Camera",
         "Крута камера"
      );
      this.add(
         "BlockOverlay",
         "Block Overlay",
         "Оверлей блоку"
      );
      this.add("ShaderHands", "Shader Hands", "Шейдер рук");
      this.add(
         "Режим",
         "Mode",
         "Режим"
      );
      this.add(
         "Мод",
         "Mode",
         "Режим"
      );
      this.add(
         "Стиль",
         "Style",
         "Стиль"
      );
      this.add(
         "Скорость",
         "Speed",
         "Швидкість"
      );
      this.add(
         "Скорость анимации",
         "Animation Speed",
         "Швидкість анімації"
      );
      this.add(
         "Скорость вращения",
         "Rotation Speed",
         "Швидкість обертання"
      );
      this.add(
         "Скорость волн",
         "Wave Speed",
         "Швидкість хвиль"
      );
      this.add(
         "Скорость нитей",
         "Thread Speed",
         "Швидкість ниток"
      );
      this.add(
         "Дистанция",
         "Distance",
         "Дистанція"
      );
      this.add(
         "Размер",
         "Size",
         "Розмір"
      );
      this.add(
         "Прозрачность",
         "Opacity",
         "Прозорість"
      );
      this.add(
         "Свечение",
         "Glow",
         "Світіння"
      );
      this.add(
         "Сила свечения",
         "Glow Strength",
         "Сила світіння"
      );
      this.add(
         "Сила анимации",
         "Animation Strength",
         "Сила анімації"
      );
      this.add(
         "Плавность",
         "Smoothness",
         "Плавність"
      );
      this.add(
         "Анимация",
         "Animation",
         "Анімація"
      );
      this.add(
         "Анимация крыльев",
         "Wing Animation",
         "Анімація крил"
      );
      this.add(
         "Анимация свинга",
         "Swing Animation",
         "Анімація свінгу"
      );
      this.add(
         "Плавная анимация",
         "Smooth Animation",
         "Плавна анімація"
      );
      this.add(
         "Тип частиц",
         "Particle Type",
         "Тип частинок"
      );
      this.add(
         "Количество",
         "Count",
         "Кількість"
      );
      this.add(
         "Приоритет",
         "Priority",
         "Пріоритет"
      );
      this.add(
         "Ротация",
         "Rotation",
         "Ротація"
      );
      this.add(
         "Обход",
         "Bypass",
         "Обхід"
      );
      this.add(
         "Сервер",
         "Server",
         "Сервер"
      );
      this.add(
         "После лута",
         "After Loot",
         "Після луту"
      );
      this.add(
         "Элементы",
         "Elements",
         "Елементи"
      );
      this.add(
         "Ватермарка",
         "Watermark",
         "Ватермарка"
      );
      this.add(
         "Аррай лист",
         "Array List",
         "Список модулів"
      );
      this.add(
         "Горячие клавиши",
         "Key Binds",
         "Гарячі клавіші"
      );
      this.add(
         "Зелья",
         "Potions",
         "Зілля"
      );
      this.add(
         "Таргет худ",
         "Target HUD",
         "Таргет HUD"
      );
      this.add(
         "Уведомления",
         "Notifications",
         "Сповіщення"
      );
      this.add(
         "Стафф",
         "Staff",
         "Стаф"
      );
      this.add(
         "Сессия",
         "Session",
         "Сесія"
      );
      this.add(
         "КейСтроки",
         "Key Strokes",
         "Кейстроки"
      );
      this.add(
         "Информация",
         "Information",
         "Інформація"
      );
      this.add(
         "Обычный",
         "Default",
         "Звичайний"
      );
      this.add(
         "Красивый",
         "Fancy",
         "Гарний"
      );
      this.add("Шейдер", "Shader", "Шейдер");
      this.add(
         "Нитки",
         "Threads",
         "Нитки"
      );
      this.add(
         "Разлет",
         "Scatter",
         "Розліт"
      );
      this.add(
         "Падение",
         "Fall",
         "Падіння"
      );
      this.add(
         "Возвращаться",
         "Return",
         "Повертатися"
      );
      this.add(
         "Тепаться на спавн",
         "Teleport to Spawn",
         "Телепортуватись на спавн"
      );
      this.add(
         "Картинка 1",
         "Image 1",
         "Картинка 1"
      );
      this.add(
         "Ромб",
         "Image 2",
         "Ромб"
      );
      this.add(
         "Призраки",
         "Ghosts",
         "Привиди"
      );
      this.add(
         "Райдер",
         "Rider",
         "Райдер"
      );
      this.add(
         "Души",
         "Souls",
         "Души"
      );
      this.add(
         "Кристаллы",
         "Crystals",
         "Кристали"
      );
      this.add(
         "Коллизия",
         "Collision",
         "Колізія"
      );
      this.add(
         "Тест",
         "Test",
         "Тест"
      );
      this.add(
         "Дистанция атаки",
         "Attack Range",
         "Дистанція атаки"
      );
      this.add(
         "Только движение",
         "Only Movement",
         "Тільки рух"
      );
      this.add(
         "Только при Aura",
         "Only with Aura",
         "Тільки з Aura"
      );
      this.add(
         "Только с аурой",
         "Only with Aura",
         "Тільки з аурою"
      );
      this.add(
         "Правая рука X",
         "Right Hand X",
         "Права рука X"
      );
      this.add(
         "Правая рука Y",
         "Right Hand Y",
         "Права рука Y"
      );
      this.add(
         "Правая рука Z",
         "Right Hand Z",
         "Права рука Z"
      );
      this.add(
         "Левая рука X",
         "Left Hand X",
         "Ліва рука X"
      );
      this.add(
         "Левая рука Y",
         "Left Hand Y",
         "Ліва рука Y"
      );
      this.add(
         "Левая рука Z",
         "Left Hand Z",
         "Ліва рука Z"
      );
      this.add(
         "Авто-взлёт",
         "Auto Takeoff",
         "Авто зліт"
      );
      this.add(
         "Обходить Grim",
         "Bypass Grim",
         "Обходити Grim"
      );
      this.add(
         "Крылья",
         "Wings",
         "Крила"
      );
      this.add(
         "Крылья 2",
         "Wings 2",
         "Крила 2"
      );
      this.add(
         "Китайская шляпа",
         "China Hat",
         "Китайський капелюх"
      );
   }

   private void add(
      String str,
      String str2,
      String str3
   ) {
      this.en
         .put(
            str,
            str2
         );
      this.uk
         .put(
            str,
            str3
         );
   }

   public DisplaynameType_1 getLanguage() {
      return this.language;
   }

   public void setLanguage(
      DisplaynameType_1 val
   ) {
      this.language = val
            == null
         ? DisplaynameType_1.RUSSIAN
         : val;
   }

   public void cycleLanguage() {
      this.language = this.language.next();
   }

   public String translateCategory(
      Category val
   ) {
      return this.translate(
         val.getName()
      );
   }

   public String translate(
      String str
   ) {
      if (str
            != null
         && !str.isEmpty()
         )
       {
         return switch (this.language) {
            case RUSSIAN -> str;
            case ENGLISH -> (String)this.en
            .getOrDefault(
               str,
               this.fallbackEnglish(
                  str
               )
            );
            case UKRAINIAN -> (String)this.uk
            .getOrDefault(
               str,
               this.fallbackUkrainian(
                  str
               )
            );
         };
      } else {
         return str;
      }
   }

   private String fallbackEnglish(
      String str
   ) {
      return str.chars()
            .allMatch(
               val -> val
                     < 128
            )
         ? this.humanizeAscii(
            str
         )
         : str;
   }

   private String fallbackUkrainian(
      String str
   ) {
      if (this.uk
         .containsKey(
            str
         )) {
         return this.uk
            .get(
               str
            );
      } else {
         return str.chars()
               .allMatch(
                  val -> val
                        < 128
               )
            ? this.humanizeAscii(
               str
            )
            : str;
      }
   }

   private String humanizeAscii(
      String str
   ) {
      if (str.indexOf(
            32
         )
         >= 0) {
         return str;
      } else {
         String str2 = str.replaceAll(
            "([a-z])([A-Z])", "$1 $2"
         );
         str2 = str2.replaceAll(
            "([A-Z]+)([A-Z][a-z])", "$1 $2"
         );
         return str2.trim();
      }
   }
}
