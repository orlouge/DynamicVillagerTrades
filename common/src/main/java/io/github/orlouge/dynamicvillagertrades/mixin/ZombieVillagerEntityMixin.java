package io.github.orlouge.dynamicvillagertrades.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.orlouge.dynamicvillagertrades.DynamicVillagerTradesMod;
import io.github.orlouge.dynamicvillagertrades.ExtendedVillagerEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ZombieVillagerEntity.class)
public class ZombieVillagerEntityMixin implements ExtendedVillagerEntity {
    private static final String extra_data_key = DynamicVillagerTradesMod.MOD_ID + "_data";
    private NbtCompound extraData = null;

    @Inject(method = "method_63659", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;setExperience(I)V"))
    private void transferOffers(ServerWorld serverWorld, VillagerEntity villager, CallbackInfo ci) {
        if (extraData != null && villager instanceof ExtendedVillagerEntity extendedVillager) {
            extendedVillager.readExtraData(this.extraData);
        }
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    public void onWriteCustomData(WriteView view, CallbackInfo ci) {
        NbtCompound nbt = new NbtCompound();
        writeExtraData(nbt);
        view.put(extra_data_key, Codecs.NBT_ELEMENT, nbt);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    public void onReadCustomData(ReadView view, CallbackInfo ci) {
        view.read(extra_data_key, Codecs.NBT_ELEMENT).ifPresent(el -> readExtraData((NbtCompound) el));
    }

    @Override
    public void readExtraData(NbtCompound nbt) {
        if (this.extraData == null) {
            this.extraData = new NbtCompound();
        }
        this.extraData.copyFrom(nbt);
    }

    @Override
    public void writeExtraData(NbtCompound nbt) {
        if (this.extraData != null) {
            nbt.copyFrom(this.extraData);
        }
    }
}
