package com.IndustrialWorld.manager;

import com.IndustrialWorld.event.TickEvent;
import com.IndustrialWorld.interfaces.Wire;
import com.IndustrialWorld.utils.BlockUtil;
import com.IndustrialWorld.utils.LinkedTree;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerManager {
    private static HashMap<Location, Request> lastRequestMap = null;
    private static HashMap<Location, Request> requestMap = new HashMap<>();
    private static HashMap<Location, Float> wireMap = new HashMap<>();

    public static void inputPower(Block block, float power, boolean need) {
        requestMap.put(block.getLocation(), new InputRequest(power, need));
    }

    public static void outputPower(Block block, float power) {
        requestMap.put(block.getLocation(), new OutputRequest(power));
    }

    public static HashMap<Location, Request> getLastRequests() {
        return lastRequestMap;
    }

    public static InputRequest getInputRequest(Location loc) {
        InputRequest resq = null;
        for (Map.Entry<Location, Request> entry : requestMap.entrySet()) {
            if (entry.getKey().equals(loc) && entry.getValue() instanceof InputRequest) {
                resq = ((InputRequest) entry.getValue());
                break;
            }
        }
        return resq;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTick(TickEvent event) {
        for (Map.Entry<Location, Request> entry : requestMap.entrySet())
            if (entry.getValue() instanceof OutputRequest) {
                Location buf = entry.getKey();
                Location[] buf2 = new Location[]{
                        buf.clone().add(1, 0, 0), buf.clone().add(-1, 0, 0), buf.clone().add(0, 1, 0),
                        buf.clone().add(0, -1, 0), buf.clone().add(0, 0, 1), buf.clone().add(0, 0, -1),
                        };
                int d = 0;
                for (Location l : buf2)
                    if (BlockUtil.isWire(l.getBlock()) ||
                        (BlockUtil.isMachine(l.getBlock()) && getInputRequest(l) != null))
                        ++d;
                if (d == 0)
                    d = 1;
                ((OutputRequest) entry.getValue()).power /= d;
                for (Location l : buf2)
                    if (BlockUtil.isWire(l.getBlock())) {
                        Map.Entry<LinkedTree<Location>, List<BlockUtil.SearchResult>> result = BlockUtil.searchFromWire(l.getBlock());
                        for (BlockUtil.SearchResult r : result.getValue()) {
                            InputRequest resq = getInputRequest(r.machineBlock.getLocation());
                            if (resq == null)
                                continue;
                            float powerGet = Math.min(
                                    ((OutputRequest) entry.getValue()).power / r.divisorOfElectricity, resq.power);
                            ((OutputRequest) entry.getValue()).result += powerGet;
                            for (LinkedTree.Node<Location> node : result.getKey().getAllNodes())
                                if (node.getValue().equals(r.machineBlock.getLocation()))
                                    for (LinkedTree.Node<Location> wireNode : node.getAllParents()) {
                                        powerGet -= ((Wire) MainManager.getInstanceFromId(MainManager.getBlockId(wireNode.getValue().getBlock()))).getEnergyLoss();
                                        if (wireMap.containsKey(wireNode.getValue())) {
                                            float nowPower = wireMap.get(wireNode.getValue());
                                            float maxPower = ((Wire) MainManager.getInstanceFromId(MainManager.getBlockId(wireNode.getValue().getBlock()))).getMaxTransmissionEnergy();
                                            float newPower = nowPower + powerGet;
                                            if (newPower > maxPower) {
                                                newPower = maxPower;
                                                powerGet = newPower - nowPower;
                                            }
                                            wireMap.replace(wireNode.getValue(), newPower);
                                        } else
                                            wireMap.put(wireNode.getValue(), powerGet);
                                    }
                            resq.result = powerGet;
                        }
                    } else if (BlockUtil.isMachine(l.getBlock())) {
                        InputRequest resq = getInputRequest(l);
                        if (resq != null) {
                            float powerGet = Math.min(((OutputRequest) entry.getValue()).power, resq.power);
                            ((OutputRequest) entry.getValue()).result += powerGet;
                            resq.result = powerGet;
                        }
                    }
            }

        lastRequestMap = requestMap;
        requestMap = new HashMap<>();
    }

    public static abstract class Request {
    }

    public static class InputRequest extends Request {
        public float power;
        public boolean need;
        public float result;

        public InputRequest(float power, boolean need) {
            this.power = power;
            this.need = need;
        }

        public boolean equals(final Object o) {
            if (o == this)
                return true;
            if (!(o instanceof InputRequest))
                return false;
            final InputRequest other = (InputRequest) o;
            if (!other.canEqual((Object) this))
                return false;
            if (Float.compare(this.power, other.power) != 0)
                return false;
            if (this.need != other.need)
                return false;
            if (Float.compare(this.result, other.result) != 0)
                return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof InputRequest;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + Float.floatToIntBits(this.power);
            result = result * PRIME + (this.need ? 79 : 97);
            result = result * PRIME + Float.floatToIntBits(this.result);
            return result;
        }
    }

    public static class OutputRequest extends Request {
        public float power;
        public float result;

        public OutputRequest(float power) {
            this.power = power;
        }

        public boolean equals(final Object o) {
            if (o == this)
                return true;
            if (!(o instanceof OutputRequest))
                return false;
            final OutputRequest other = (OutputRequest) o;
            if (!other.canEqual((Object) this))
                return false;
            if (Float.compare(this.power, other.power) != 0)
                return false;
            if (Float.compare(this.result, other.result) != 0)
                return false;
            return true;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof OutputRequest;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + Float.floatToIntBits(this.power);
            result = result * PRIME + Float.floatToIntBits(this.result);
            return result;
        }
    }
}
