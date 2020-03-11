package com.IndustrialWorld.manager;

import com.IndustrialWorld.item.material.IWMaterial;
import com.IndustrialWorld.item.material.info.MaterialInfo;

import java.util.HashMap;
import java.util.Map;

public class IWMaterialManager {
    private static Map<IWMaterial, MaterialInfo> iwMaterialInfoMap = new HashMap<>();

    public static void register(IWMaterial iwMaterial, MaterialInfo materialInfo) {
        iwMaterialInfoMap.put(iwMaterial, materialInfo);
    }

    public static MaterialInfo getMaterialInfo(IWMaterial iwMaterial) {
        return iwMaterialInfoMap.get(iwMaterial);
    }
}
