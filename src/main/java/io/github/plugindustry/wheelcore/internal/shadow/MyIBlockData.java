package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.ShadowManager;
import io.github.czm23333.transparentreflect.annotations.DisabledConstructor;
import io.github.czm23333.transparentreflect.annotations.Shadow;
import io.github.czm23333.transparentreflect.annotations.ShadowExtend;
import io.github.czm23333.transparentreflect.annotations.ShadowOverride;
import io.github.plugindustry.wheelcore.internal.UnsafeOperation;

@ShadowExtend("nms/IBlockData")
public class MyIBlockData {
    public float blastResistance = 0.0f;
    private MyNMSBlock newBlock = null;

    @DisabledConstructor
    public MyIBlockData() {}

    @Shadow("nms/BlockData.getBlock")
    public NMSBlock getOriginBlock() {
        return null;
    }

    @ShadowOverride("nms/BlockData.getBlock")
    public NMSBlock getBlock() {
        if (newBlock == null) {
            Object temp = ShadowManager.shadowUnpack(getOriginBlock());
            MyNMSBlock res = UnsafeOperation.newInstance(MyNMSBlock.class);
            UnsafeOperation.fieldsCopy(temp, res);
            res.blastResistance = blastResistance;
            newBlock = res;
        }
        return new NMSBlock(newBlock);
    }
}