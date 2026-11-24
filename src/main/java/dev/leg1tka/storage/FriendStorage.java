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


import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class FriendStorage {
   private final List<String> friends = new ArrayList<>();

   public void add(
      String str
   ) {
      if (!str.isEmpty()
         )
       {
         this.friends
            .add(
               str
            );
      }
   }

   public void remove(
      String str
   ) {
      this.friends
         .remove(
            str
         );
   }

   public void clear() {
      this.friends.clear();
   }

   public boolean isFriend(
      String str
   ) {
      return this.friends
         .contains(
            str
         );
   }

   public boolean isEmpty() {
      return this.friends.isEmpty();
   }

   @Generated
   public List<String> getFriends() {
      return this.friends;
   }
}
