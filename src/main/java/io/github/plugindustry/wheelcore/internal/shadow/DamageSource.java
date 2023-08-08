package io.github.plugindustry.wheelcore.internal.shadow;

import com.comphenix.protocol.reflect.fuzzy.FuzzyFieldContract;
import io.github.czm23333.transparentreflect.annotations.Shadow;
import io.github.plugindustry.wheelcore.utils.FuzzyUtil;

import java.lang.reflect.Modifier;

@Shadow("nms/DamageSource")
@SuppressWarnings("all")
public class DamageSource {
    public static final DamageSource GENERIC_SOURCE;
    public static final DamageSource LAVA_SOURCE;

    static {
        try {
            GENERIC_SOURCE = new DamageSource(ShadowRegistry.GenericDamageSourceField.get(null));
            LAVA_SOURCE = FuzzyUtil.findDeclaredMatches(
                            FuzzyFieldContract.newBuilder().requirePublic().requireModifier(Modifier.STATIC)
                                    .requireModifier(Modifier.FINAL).typeExact(ShadowRegistry.DamageSourceClass).build(),
                            ShadowRegistry.DamageSourceClass).map(f -> {
                        try {
                            return f.get(null);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }).map(DamageSource::new).filter(ds -> "lava".equals(ds.getId())).findFirst()
                    .orElseThrow(() -> new IllegalStateException("Can't find lava damage source"));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DamageSource(Object o) {}

    @Shadow("nms/DamageSource.getId")
    public String getId() {
        return null;
    }
}