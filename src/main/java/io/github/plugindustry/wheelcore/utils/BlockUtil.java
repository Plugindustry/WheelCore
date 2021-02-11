package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.interfaces.block.MachineBase;
import io.github.plugindustry.wheelcore.interfaces.block.Wire;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BlockUtil {
    private static final List<Material> replaceableOreGenList = Arrays.asList(Material.STONE,
                                                                              Material.DIRT,
                                                                              Material.ANDESITE,
                                                                              Material.GRAVEL);

    /*public static Map.Entry<HashTree<Location>, List<SearchResult>> searchFromWire(Block wireBlock, Location expect) {
        ArrayList<SearchResult> result = new ArrayList<>();
        HashTree<Location> network = new HashTree<>(new Node<>(wireBlock.getLocation()));
        boolean isEnd = false;
        while (!isEnd) {
            LinkedList<Node<Location>> tem = (LinkedList<Node<Location>>) network.getLeafNodes().clone();
            for (Node<Location> node : network.getLeafNodes()) {
                if (!isWire(node.getValue().getBlock()))
                    continue;

                ArrayList<Block> machines = new ArrayList<>();
                Location temp = node.getValue();
                Base nodeInstance = MainManager.getInstanceFromId(MainManager.getBlockId(node.getValue().getBlock()));

                int divisor = 1;
                int machineNumber = 0;
                boolean countingMachines = true;
                List<Node<Location>> buf = node.getAllParents();
                for (Node<Location> n : buf)
                    if (n.getChildNodes().size() > 1) {
                        int i = 0;
                        ArrayList<Node<Location>> buf2 = new ArrayList<>(n.getChildNodes());
                        for (int j = 0; j < buf2.size(); ++j) {
                            Node<Location> n1 = buf2.get(j);
                            if (isWire(n1.getValue().getBlock())) {
                                ++i;
                                buf2.set(j, null);
                            }
                        }
                        while (buf2.remove(null))
                            ;
                        if (buf2.size() > 0) {
                            if (i > 1) {
                                i += buf2.size();
                                if (countingMachines)
                                    countingMachines = false;
                            } else if (countingMachines)
                                machineNumber += buf2.size();
                        }
                        divisor *= i;
                    }

                Location t1 = temp.clone().add(1, 0, 0);
                machineNumber = checkTargetBlock(expect, network, node, machines, nodeInstance, machineNumber, t1, t1
                .getBlock());

                Location t2 = temp.clone().add(-1, 0, 0);
                machineNumber = checkTargetBlock(expect, network, node, machines, nodeInstance, machineNumber, t2, t1
                .getBlock());

                Location t3 = temp.clone().add(0, 1, 0);
                machineNumber = checkTargetBlock(expect, network, node, machines, nodeInstance, machineNumber, t3, t1
                .getBlock());

                Location t4 = temp.clone().add(0, -1, 0);
                machineNumber = checkTargetBlock(expect, network, node, machines, nodeInstance, machineNumber, t4, t1
                .getBlock());

                Location t5 = temp.clone().add(0, 0, 1);
                machineNumber = checkTargetBlock(expect, network, node, machines, nodeInstance, machineNumber, t5, t1
                .getBlock());

                Location t6 = temp.clone().add(0, 0, -1);
                machineNumber = checkTargetBlock(expect, network, node, machines, nodeInstance, machineNumber, t6, t1
                .getBlock());

                for (Block machine : machines) {
                    ArrayList<Location> wires = new ArrayList<>();
                    for (Node<Location> n : node.getAllParents())
                        wires.add(n.getValue());
                    wires.add(node.getValue());
                    result.add(new SearchResult(machine, wires, divisor * machineNumber));
                }
            }
            if (network.getLeafNodes().containsAll(tem))
                isEnd = true;
        }
        return new AbstractMap.SimpleEntry<>(network, result);
    }

    private static int checkTargetBlock(Location expect, HashTree<Location> network, Node<Location> node,
    ArrayList<Block> machines, Base nodeInstance, int machineNumber, Location t1, Block block) {
        if (!t1.equals(expect)) {
            if (isWire(t1.getBlock()) &&
                MainManager.getInstanceFromId(MainManager.getBlockId(block)).equals(nodeInstance))
                network.addNode(node, new Node<>(t1));
            else if (isMachine(t1.getBlock())) {
                machines.add(t1.getBlock());
                ++machineNumber;
                network.addNode(node, new Node<>(t1));
                //result.add(new SearchResult(t1.getBlock(), buf.size() + 1, divisor));
            }
        }
        return machineNumber;
    }*/

    public static Stream<Location> findWireAround(Location src) {
        return Stream.of(src.clone().add(1, 0, 0),
                         src.clone().add(-1, 0, 0),
                         src.clone().add(0, 1, 0),
                         src.clone().add(0, -1, 0),
                         src.clone().add(0, 0, 1),
                         src.clone().add(0, 0, -1)).filter(BlockUtil::isWire);
    }

    public static boolean isMachine(Location block) {
        return MainManager.hasBlock(block) && MainManager.getBlockInstance(block) instanceof MachineBase;
    }

    public static boolean isWire(Location block) {
        return MainManager.hasBlock(block) && MainManager.getBlockInstance(block) instanceof Wire;
    }

    public static boolean isReplaceableOreGen(Block block) {
        return replaceableOreGenList.contains(block.getType());
    }

    /*public static class SearchResult {
        public Block machineBlock;
        public ArrayList<Location> wires;
        public int divisorOfElectricity;

        public SearchResult(Block machineBlock, ArrayList<Location> wires, int doe) {
            this.machineBlock = machineBlock;
            this.wires = wires;
            this.divisorOfElectricity = doe;
        }
    }*/
}
