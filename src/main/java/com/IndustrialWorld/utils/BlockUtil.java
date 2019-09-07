package com.IndustrialWorld.utils;

import com.IndustrialWorld.interfaces.Base;
import com.IndustrialWorld.interfaces.MachineBlock;
import com.IndustrialWorld.interfaces.Wire;
import com.IndustrialWorld.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

import static com.IndustrialWorld.utils.LinkedTree.Node;

public class BlockUtil {
    public static Map.Entry<LinkedTree<Location>, List<SearchResult>> searchFromWire(Block wireBlock, Location expect) {
        ArrayList<SearchResult> result = new ArrayList<>();
        LinkedTree<Location> network = new LinkedTree<>(new Node<>(wireBlock.getLocation()));
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
                if (!t1.equals(expect)) {
                    if (isWire(t1.getBlock()) &&
                        MainManager.getInstanceFromId(MainManager.getBlockId(t1.getBlock())).equals(nodeInstance))
                        network.addNode(node, new Node<>(t1));
                    else if (isMachine(t1.getBlock())) {
                        machines.add(t1.getBlock());
                        ++machineNumber;
                        network.addNode(node, new Node<>(t1));
                        //result.add(new SearchResult(t1.getBlock(), buf.size() + 1, divisor));
                    }
                }

                Location t2 = temp.clone().add(-1, 0, 0);
                if (!t2.equals(expect)) {
                    if (isWire(t2.getBlock()) &&
                        MainManager.getInstanceFromId(MainManager.getBlockId(t1.getBlock())).equals(nodeInstance))
                        network.addNode(node, new Node<>(t2));
                    else if (isMachine(t2.getBlock())) {
                        machines.add(t2.getBlock());
                        ++machineNumber;
                        network.addNode(node, new Node<>(t2));
                    }
                }

                Location t3 = temp.clone().add(0, 1, 0);
                if (!t3.equals(expect)) {
                    if (isWire(t3.getBlock()) &&
                        MainManager.getInstanceFromId(MainManager.getBlockId(t1.getBlock())).equals(nodeInstance))
                        network.addNode(node, new Node<>(t3));
                    else if (isMachine(t3.getBlock())) {
                        machines.add(t3.getBlock());
                        ++machineNumber;
                        network.addNode(node, new Node<>(t3));
                    }
                }

                Location t4 = temp.clone().add(0, -1, 0);
                if (!t4.equals(expect)) {
                    if (isWire(t4.getBlock()) &&
                        MainManager.getInstanceFromId(MainManager.getBlockId(t1.getBlock())).equals(nodeInstance))
                        network.addNode(node, new Node<>(t4));
                    else if (isMachine(t4.getBlock())) {
                        machines.add(t4.getBlock());
                        ++machineNumber;
                        network.addNode(node, new Node<>(t4));
                    }
                }

                Location t5 = temp.clone().add(0, 0, 1);
                if (!t5.equals(expect)) {
                    if (isWire(t5.getBlock()) &&
                        MainManager.getInstanceFromId(MainManager.getBlockId(t1.getBlock())).equals(nodeInstance))
                        network.addNode(node, new Node<>(t5));
                    else if (isMachine(t5.getBlock())) {
                        machines.add(t5.getBlock());
                        ++machineNumber;
                        network.addNode(node, new Node<>(t5));
                    }
                }

                Location t6 = temp.clone().add(0, 0, -1);
                if (!t6.equals(expect)) {
                    if (isWire(t6.getBlock()) &&
                        MainManager.getInstanceFromId(MainManager.getBlockId(t1.getBlock())).equals(nodeInstance))
                        network.addNode(node, new Node<>(t6));
                    else if (isMachine(t6.getBlock())) {
                        machines.add(t6.getBlock());
                        ++machineNumber;
                        network.addNode(node, new Node<>(t6));
                    }
                }

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

    public static boolean isMachine(Block block) {
        return MainManager.hasBlock(block) &&
               MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof MachineBlock;
    }

    public static boolean isWire(Block block) {
        return MainManager.hasBlock(block) &&
               MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof Wire;
    }

    public static class SearchResult {
        public Block machineBlock;
        public ArrayList<Location> wires;
        public int divisorOfElectricity;

        public SearchResult(Block machineBlock, ArrayList<Location> wires, int doe) {
            this.machineBlock = machineBlock;
            this.wires = wires;
            this.divisorOfElectricity = doe;
        }
    }

	public static boolean isReplaceableOreGen(Block block) {
		List<Material> replaceableOreGenList = Arrays.asList(Material.STONE, Material.DIRT, Material.ANDESITE, Material.GRAVEL);
		return replaceableOreGenList.contains(block.getBlockData().getMaterial());
	}
}
