package dev.leg1tka.api.utils.render.fonts.ttf;

import java.awt.Font;
import java.io.InputStream;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class FontUtil {
   public static Font getFontFromTTF(Identifier loc, float fontSize, int fontType) {
      try {
         MinecraftClient client = MinecraftClient.getInstance();
         if (client == null) {
            return null;
         } else if (client.getResourceManager() == null) {
            return null;
         } else {
            Optional<Resource> resource = client.getResourceManager().getResource(loc);
            if (resource.isPresent()) {
               InputStream inputStream = resource.get().getInputStream();
               Font output = Font.createFont(fontType, inputStream);
               output = output.deriveFont(fontSize);
               inputStream.close();
               return output;
            } else {
               return null;
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
         return null;
      }
   }
}
