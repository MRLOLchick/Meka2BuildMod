package com.MrLOLchick.mekanism2bc;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

// Импорты для NTM (примерный путь, т.к. точный неизвестен)
// import com.hbm.items.machine.ItemBatteryBase; // Пример: базовый класс батарейки
// import com.hbm.tileentity.machine.TileEntityMachineBase; // Пример: базовый класс машины

public class TileEntityConverter extends TileEntity implements ITickable, IStrictEnergyAcceptor, IMjConnector {

    private long microBuffer = 0;
    private static final long MAX_BUFFER = 10_000_000L; // 10 MJ буфер
    private static final double J_TO_MICRO_MJ = 250_000.0;

    // Коэффициент для NTM: 1 HE = 5000 микро-MJ
    private static final long MICRO_MJ_PER_HE = 5000L;

    // ========== Mekanism API ==========
    @Override
    public double acceptEnergy(EnumFacing side, double amount, boolean simulate) {
        if (simulate) {
            return amount;
        }
        long toAdd = (long) (amount * J_TO_MICRO_MJ);
        if (microBuffer + toAdd > MAX_BUFFER) {
            toAdd = MAX_BUFFER - microBuffer;
        }
        microBuffer += toAdd;
        return amount;
    }

    @Override
    public boolean canReceiveEnergy(EnumFacing side) {
        return true;
    }

    // ========== BuildCraft API ==========
    @Override
    public boolean canConnect(IMjConnector other) {
        return true;
    }

    // ========== Update ==========
    @Override
    public void update() {
        if (world.isRemote) {
            return; // Не работаем на клиенте
        }

        if (microBuffer > 0) {
            // 1. Сначала пытаемся отдать энергию в обычную BuildCraft сеть
            sendEnergyToBuildCraft();

            // 2. Если есть остаток энергии, пытаемся зарядить NTM батарейки в соседних блоках
            if (microBuffer > 0) {
                chargeNTMItems();
            }
        }
    }

    // ========== Логика для BuildCraft (MJ) ==========
    private void sendEnergyToBuildCraft() {
        for (EnumFacing side : EnumFacing.values()) {
            TileEntity tile = world.getTileEntity(pos.offset(side));
            if (tile instanceof IMjReceiver) {
                IMjReceiver receiver = (IMjReceiver) tile;
                if (receiver.canReceive()) {
                    long offered = Math.min(microBuffer, MjAPI.MJ);
                    long accepted = receiver.receivePower(offered, false);
                    microBuffer -= accepted;
                }
            }
        }
    }

    // ========== Логика для NTM (HE) ==========
    private void chargeNTMItems() {
        for (EnumFacing side : EnumFacing.values()) {
            TileEntity tile = world.getTileEntity(pos.offset(side));
            if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) {
                IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
                if (itemHandler == null) continue;

                for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if (stack.isEmpty()) continue;

                    // Блок проверки на NTM батарейки/предметы (требуется уточнение путей)
                    /*
                    if (stack.getItem() instanceof ItemBatteryBase) {
                        // Мы не можем просто так передать HE через getCapability, т.к. NTM использует
                        // собственную систему. Обычно это прямой вызов методов.
                        // Пример гипотетического метода:
                        // long maxCharge = ((ItemBatteryBase) stack.getItem()).getMaxCharge();
                        // long currentCharge = getCurrentCharge(stack);
                        // long chargeRoom = maxCharge - currentCharge;
                        // long heToGive = Math.min(microBuffer / MICRO_MJ_PER_HE, chargeRoom);
                        // if (heToGive > 0) {
                        //     ((ItemBatteryBase) stack.getItem()).charge(stack, currentCharge + heToGive);
                        //     microBuffer -= heToGive * MICRO_MJ_PER_HE;
                        // }
                    }
                    */

                    if (microBuffer <= 0) return;
                }
            }
        }
    }
}