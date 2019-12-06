package com.ellisiumx.elrankup.utils;

import com.ellisiumx.elrankup.mapedit.MapEditManager;
import com.ellisiumx.elrankup.mapedit.PlayerPoints;
import net.minecraft.server.v1_8_R3.ExceptionEntityNotFound;
import net.minecraft.server.v1_8_R3.ExceptionWorldConflict;
import org.bukkit.entity.Player;

public final class UtilCheck {

    public static PlayerPoints getPoints(Player caller) throws ExceptionEntityNotFound, ExceptionWorldConflict {
        PlayerPoints points = MapEditManager.getPlayerPoints(caller);

        if(points.getPoint1() == null)
            throw new ExceptionEntityNotFound("Point 1 is not defined!");

        if(points.getPoint2() == null)
            throw new ExceptionEntityNotFound("Point 2 is not defined!");

        if(points.getPoint1().getWorld() != points.getPoint2().getWorld())
            throw new ExceptionWorldConflict("The points are not in the same world!");

        return points;
    }

}
