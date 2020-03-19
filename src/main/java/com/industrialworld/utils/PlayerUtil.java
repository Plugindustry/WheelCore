/*package com.industrialworld.utils;

import java.awt.Image;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class PlayerUtil {
    public static Boolean setDisplayName(String player,String name){
        if(Bukkit.getPlayerExact(player)!=null){
            Bukkit.getPlayerExact(player).setDisplayName(name);
            return true;
        }
        return false;
    }

    public static Boolean sendMap(Player player){
        MapRenderer mapr=new MapRenderer();
        MapCanvas canvas=new MapCanvas();
        canvas.drawText(0,0,new MinecraftFont(),"Test Map");
        player.sendMap(canvas.getMapView());
    }
}
*/