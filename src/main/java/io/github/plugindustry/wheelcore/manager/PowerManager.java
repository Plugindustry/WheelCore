package io.github.plugindustry.wheelcore.manager;

import io.github.plugindustry.wheelcore.interfaces.block.Wire;
import io.github.plugindustry.wheelcore.interfaces.power.EnergyInputable;
import io.github.plugindustry.wheelcore.interfaces.power.EnergyOutputable;
import io.github.plugindustry.wheelcore.utils.BlockUtil;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowerManager {
    private final static HashMap<Location, InputRequest> inputMap = new HashMap<>();
    private final static HashMap<Location, OutputRequest> outputMap = new HashMap<>();
    private final static Random r = new Random();

    public static void inputPower(Location block, double power) {
        inputMap.put(block, new InputRequest(power));
    }

    public static void outputPower(Location block, double power) {
        outputMap.put(block, new OutputRequest(power));
    }

    public static void onTick() {
        // Output
        outputMap.forEach((key, value) -> BlockUtil.findWireAround(key).forEach(location -> {
            Wire.WireData data = (Wire.WireData) MainManager.getBlockData(location);
            if (data == null) {
                data = new Wire.WireData();
                MainManager.setBlockData(location, data);
            }

            double current = data.stat;
            double max = ((Wire) MainManager.getBlockInstance(location)).getMaxTransmissionEnergy();
            if (current < max) {
                double finalPower = max < current + value.getPower() ? max - current : value.getPower();
                data.packets.add(new Wire.PowerPacket(key, finalPower));
                data.stat += finalPower;
            }
        }));

        // Input and spread
        MainManager.getBlockMapping()
                .values()
                .stream()
                .filter(base -> base instanceof Wire)
                .map(MainManager.dataProvider::blocksOf)
                .flatMap(Collection::stream)
                .forEach(location -> {
                    Wire instanceOri = ((Wire) MainManager.getBlockInstance(location));
                    double energyLoss = instanceOri.getEnergyLoss();
                    double maxTrans = instanceOri.getMaxTransmissionEnergy();
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
                            .collect(Collectors.toCollection(HashSet::new));

                    Iterator<Wire.PowerPacket> iterator = data.packets.iterator();
                    while (iterator.hasNext()) {
                        Wire.PowerPacket packet = iterator.next();
                        if (MainManager.getBlockInstance(packet.src) instanceof EnergyOutputable &&
                            packet.amount > energyLoss) {
                            // Input
                            if (!inputs.isEmpty() && r.nextBoolean()) {
                                Location availableInput = inputs.stream()
                                        .filter(loc -> !loc.equals(packet.from))
                                        .parallel()
                                        .findAny()
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
                                    if (((EnergyOutputable) MainManager.getBlockInstance(packet.src)).finishOutput(
                                            packet.src,
                                            packetClone))
                                        ((EnergyInputable) MainManager.getBlockInstance(availableInput)).finishInput(
                                                availableInput,
                                                packetClone);

                                    if (packetClone.amount == powerNeed) {
                                        inputs.remove(availableInput);
                                        inputMap.remove(availableInput);
                                    } else
                                        inputRequest.result += packetClone.amount;

                                    if (packetClone.amount < packet.amount - energyLoss) {
                                        packetClone.amount = packet.amount - energyLoss - packetClone.amount;
                                        if (maxTrans >= data.statNext + packetClone.amount) {
                                            data.nextPackets.add(packetClone);
                                            data.statNext += packetClone.amount;
                                        }
                                    }
                                    iterator.remove();
                                    data.stat -= packet.amount;
                                    continue;
                                }
                            }

                            // Spread
                            Optional<Location> optional = BlockUtil.findWireAround(location).filter(loc -> !loc.equals(
                                    packet.from)).filter(loc -> {
                                Wire.WireData data1 = (Wire.WireData) MainManager.getBlockData(loc);
                                Wire instance = (Wire) MainManager.getBlockInstance(loc);
                                if (data1 == null) {
                                    data1 = new Wire.WireData();
                                    MainManager.setBlockData(loc, data1);
                                    return true;
                                }

                                return instance.getMaxTransmissionEnergy() > data1.statNext;
                            }).parallel().findAny();

                            if (optional.isPresent()) {
                                Wire.PowerPacket packetClone = packet.clone();
                                packetClone.amount -= energyLoss;
                                packetClone.from = location;
                                Wire.WireData data2 = ((Wire.WireData) MainManager.getBlockData(optional.get()));
                                double current = data2.stat;
                                double max = ((Wire) MainManager.getBlockInstance(optional.get())).getMaxTransmissionEnergy();
                                packetClone.amount = max < current + packetClone.amount ?
                                                     max - current :
                                                     packetClone.amount;
                                data2.nextPackets.add(packetClone);
                                data2.statNext += packetClone.amount;
                            }
                        }
                        iterator.remove();
                        data.stat -= packet.amount;
                    }
                });

        // Swap
        MainManager.getBlockMapping()
                .values()
                .stream()
                .filter(base -> base instanceof Wire)
                .map(MainManager.dataProvider::blocksOf)
                .flatMap(Collection::stream).map(MainManager::getBlockData).forEach(data -> {
                    Wire.WireData temp = (Wire.WireData) data;
                    List<Wire.PowerPacket> packets = temp.packets;
                    double stat = temp.stat;
                    temp.packets = temp.nextPackets;
                    temp.nextPackets = packets;
                    temp.stat = temp.statNext;
                    temp.statNext = stat;
                });

        inputMap.clear();
        outputMap.clear();
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
        public final double power;
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
