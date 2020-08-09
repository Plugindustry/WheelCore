package com.industrialworld.manager;

import com.industrialworld.interfaces.block.Wire;
import com.industrialworld.interfaces.power.EnergyInputable;
import com.industrialworld.interfaces.power.EnergyOutputable;
import com.industrialworld.utils.BlockUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowerManager {
    private final static HashMap<Location, InputRequest> inputMap = new HashMap<>();
    private final static HashMap<Location, OutputRequest> outputMap = new HashMap<>();

    public static void inputPower(Block block, double power) {
        inputMap.put(block.getLocation(), new InputRequest(power));
    }

    public static void outputPower(Block block, double power) {
        outputMap.put(block.getLocation(), new OutputRequest(power));
    }

    // Not enabled yet
    public void onTick() {
        /*for (Map.Entry<Location, Request> entry : requestMap.entrySet())
            if (entry.getValue() instanceof OutputRequest) {
                OutputRequest outReq = ((OutputRequest) entry.getValue());
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
                    continue;
                outReq.power /= d;
                for (Location l : buf2)
                    if (BlockUtil.isWire(l.getBlock())) {
                        Map.Entry<HashTree<Location>, List<BlockUtil.SearchResult>> result = BlockUtil.searchFromWire
                        (l.getBlock(), buf);
                        for (BlockUtil.SearchResult r : result.getValue()) {
                            InputRequest resq = getInputRequest(r.machineBlock.getLocation());
                            if (resq == null)
                                continue;
                            float powerGet = Math.min(outReq.power / r.divisorOfElectricity, resq.power);
                            outReq.result += powerGet;
                            for (HashTree.Node<Location> node : result.getKey().getAllNodes())
                                if (node.getValue().equals(r.machineBlock.getLocation()))
                                    for (HashTree.Node<Location> wireNode : node.getAllParents()) {
                                        Wire wireObj = ((Wire) MainManager.getInstanceFromId(MainManager.getBlockId
                                        (wireNode.getValue().getBlock())));
                                        powerGet -= wireObj.getEnergyLoss();
                                        if (wireMap.containsKey(wireNode.getValue())) {
                                            float nowPower = wireMap.get(wireNode.getValue());
                                            float maxPower = wireObj.getMaxTransmissionEnergy();
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
                            float powerGet = Math.min(outReq.power, resq.power);
                            outReq.result += powerGet;
                            resq.result = powerGet;
                        }
                    }
            }

        lastRequestMap = requestMap;
        requestMap = new HashMap<>();*/

        // Output
        outputMap.forEach((key, value) -> BlockUtil.findWireAround(key)
                .filter(MainManager::isBlockActive)
                .forEach(location -> {
                    Wire.WireData data = (Wire.WireData) MainManager.getBlockData(location);
                    if (data == null) {
                        data = new Wire.WireData();
                        MainManager.setBlockData(location, data);
                    }

                    if (((Wire) MainManager.getInstanceFromId(MainManager.getBlockId(location))).getMaxTransmissionEnergy() >=
                        data.packets.stream().reduce(0.0D, (a, b) -> a + b.amount, (a, b) -> null) + value.getPower())
                        data.packets.add(new Wire.PowerPacket(key, value.getPower()));
                }));

        // Input and spread
        MainManager.baseBlocks.entrySet()
                .stream()
                .filter(entry -> entry.getKey() instanceof Wire)
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .filter(MainManager::isBlockActive)
                .forEach(location -> {
                    double energyLoss = ((Wire) MainManager.getInstanceFromId(MainManager.getBlockId(location))).getEnergyLoss();
                    Wire.WireData data = (Wire.WireData) MainManager.getBlockData(location);
                    if (data == null) {
                        data = new Wire.WireData();
                        MainManager.setBlockData(location, data);
                        return;
                    }

                    HashSet<Location> inputs = Stream.of(location.clone().add(1, 0, 0),
                                                         location.clone().add(-1, 0, 0),
                                                         location.clone().add(0, 1, 0),
                                                         location.clone().add(0, -1, 0),
                                                         location.clone().add(0, 0, 1),
                                                         location.clone().add(0, 0, -1))
                            .filter(inputMap::containsKey)
                            .filter(MainManager::isBlockActive)
                            .collect(Collectors.toCollection(HashSet::new));

                    List<Location> list = BlockUtil.findWireAround(location).filter(MainManager::isBlockActive).collect(
                            Collectors.toList());
                    Iterator<Wire.PowerPacket> iterator = data.packets.iterator();
                    while (iterator.hasNext()) {
                        Wire.PowerPacket packet = iterator.next();
                        if (packet.amount > energyLoss) {
                            // Input
                            if (!inputs.isEmpty()) {
                                Location availableInput = inputs.stream()
                                        .filter(loc -> !loc.equals(packet.from))
                                        .findFirst()
                                        .orElse(null);
                                if (availableInput != null) {
                                    InputRequest inputRequest = inputMap.get(availableInput);
                                    Wire.PowerPacket packetClone = packet.clone();
                                    packetClone.amount -= energyLoss;
                                    double powerNeed = inputRequest.power - inputRequest.result;

                                    // Cut
                                    if (packetClone.amount > powerNeed)
                                        packetClone.amount = powerNeed;

                                    // I/O
                                    if (((EnergyOutputable) MainManager.getInstanceFromId(MainManager.getBlockId(packet.src)))
                                            .finishOutput(packetClone))
                                        ((EnergyInputable) MainManager.getInstanceFromId(MainManager.getBlockId(
                                                availableInput))).finishInput(packetClone);

                                    if (packetClone.amount == powerNeed)
                                        inputs.remove(availableInput);
                                    else
                                        inputRequest.result += packetClone.amount;

                                    iterator.remove();
                                    continue;
                                }
                            }

                            // Spread
                            Optional<Location> optional = list.stream().filter(loc -> !loc.equals(packet.from)).filter(
                                    loc -> {
                                        Wire.WireData data1 = (Wire.WireData) MainManager.getBlockData(loc);
                                        Wire instance = (Wire) MainManager.getInstanceFromId(MainManager.getBlockId(loc));
                                        if (data1 == null) {
                                            data1 = new Wire.WireData();
                                            MainManager.setBlockData(loc, data1);
                                            return instance.getMaxTransmissionEnergy() >= packet.amount;
                                        }

                                        return instance.getMaxTransmissionEnergy() >= data1.nextPackets.stream().reduce(
                                                0.0D,
                                                (a, b) -> a + b.amount,
                                                (a, b) -> null) + packet.amount;
                                    }).parallel().findAny();

                            if (optional.isPresent()) {
                                packet.amount -= energyLoss;
                                packet.from = location;
                                ((Wire.WireData) MainManager.getBlockData(optional.get())).nextPackets.add(packet);
                            } else
                                data.nextPackets.add(packet);
                        }
                        iterator.remove();
                    }
                });

        // Swap
        MainManager.baseBlocks.entrySet()
                .stream()
                .filter(entry -> entry.getKey() instanceof Wire)
                .flatMap(entry -> entry.getValue().stream())
                .filter(MainManager::isBlockActive)
                .map(MainManager::getBlockData)
                .forEach(data -> {
                    Wire.WireData temp = (Wire.WireData) data;
                    List<Wire.PowerPacket> packets = temp.packets;
                    temp.packets = temp.nextPackets;
                    temp.nextPackets = packets;
                });
    }

    public static abstract class Request {
        public abstract double getPower();
    }

    public static class InputRequest extends Request {
        private final double power;
        public double result;

        public InputRequest(double power) {
            this.power = power;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof InputRequest;
        }

        public boolean equals(final Object o) {
            if (o == this)
                return true;
            if (!(o instanceof InputRequest))
                return false;
            final InputRequest other = (InputRequest) o;
            if (!other.canEqual(this))
                return false;
            if (Double.compare(this.power, other.power) != 0)
                return false;
            return Double.compare(this.result, other.result) == 0;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final long $power = Double.doubleToLongBits(this.power);
            result = result * PRIME + (int) ($power >>> 32 ^ $power);
            final long $result = Double.doubleToLongBits(this.result);
            result = result * PRIME + (int) ($result >>> 32 ^ $result);
            return result;
        }

        @Override
        public double getPower() {
            return power;
        }
    }

    public static class OutputRequest extends Request {
        public double power;
        public double result;

        public OutputRequest(double power) {
            this.power = power;
        }

        protected boolean canEqual(final Object other) {
            return other instanceof OutputRequest;
        }

        public boolean equals(final Object o) {
            if (o == this)
                return true;
            if (!(o instanceof OutputRequest))
                return false;
            final OutputRequest other = (OutputRequest) o;
            if (!other.canEqual(this))
                return false;
            if (Double.compare(this.power, other.power) != 0)
                return false;
            return Double.compare(this.result, other.result) == 0;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final long $power = Double.doubleToLongBits(this.power);
            result = result * PRIME + (int) ($power >>> 32 ^ $power);
            final long $result = Double.doubleToLongBits(this.result);
            result = result * PRIME + (int) ($result >>> 32 ^ $result);
            return result;
        }

        @Override
        public double getPower() {
            return power;
        }
    }
}
