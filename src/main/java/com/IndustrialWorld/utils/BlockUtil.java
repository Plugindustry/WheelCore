package com.IndustrialWorld.utils;

import com.IndustrialWorld.interfaces.MachineBlock;
import com.IndustrialWorld.interfaces.Wire;
import com.IndustrialWorld.manager.MainManager;
import static com.IndustrialWorld.utils.LinkedTree.Node;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;

public class BlockUtil {
    public Map.Entry<LinkedTree<Location>, List<SearchResult>> searchFromWire(Block wireBlock) {
        ArrayList<SearchResult> result = new ArrayList<>();
        LinkedTree<Location> network = new LinkedTree<>(new Node<>(wireBlock.getLocation()));
        boolean isEnd = false;
        while(!isEnd){
            LinkedList<Node<Location>> tem = (LinkedList<Node<Location>>) network.getLeafNodes().clone();
            for(Node<Location> node : network.getLeafNodes()){
                Location temp = node.getValue();

                Location t1 = temp.clone().add(1, 0, 0);
                if(isWire(t1.getBlock())) network.addNode(node, new Node<>(t1));
                else if (isMachine(t1.getBlock())) {
                    List<Node<Location>> buf = node.getAllParents();
                    int divisor = 1;
                    for(Node<Location> n : buf)
                        if(n.getChildNodes().size() > 1)
                            divisor *= n.getChildNodes().size();
                    result.add(new SearchResult(t1.getBlock(), buf.size() + 1, divisor));
                }

                Location t2 = temp.clone().add(-1, 0, 0);
                if(isWire(t2.getBlock())) network.addNode(node, new Node<>(t2));
                else if (isMachine(t2.getBlock())) {
                    List<Node<Location>> buf = node.getAllParents();
                    int divisor = 1;
                    for(Node<Location> n : buf)
                        if(n.getChildNodes().size() > 1)
                            divisor *= n.getChildNodes().size();
                    result.add(new SearchResult(t2.getBlock(), buf.size() + 1, divisor));
                }

                Location t3 = temp.clone().add(0, 1, 0);
                if(isWire(t3.getBlock())) network.addNode(node, new Node<>(t3));
                else if (isMachine(t3.getBlock())) {
                    List<Node<Location>> buf = node.getAllParents();
                    int divisor = 1;
                    for(Node<Location> n : buf)
                        if(n.getChildNodes().size() > 1)
                            divisor *= n.getChildNodes().size();
                    result.add(new SearchResult(t3.getBlock(), buf.size() + 1, divisor));
                }

                Location t4 = temp.clone().add(0, -1, 0);
                if(isWire(t4.getBlock())) network.addNode(node, new Node<>(t4));
                else if (isMachine(t4.getBlock())) {
                    List<Node<Location>> buf = node.getAllParents();
                    int divisor = 1;
                    for(Node<Location> n : buf)
                        if(n.getChildNodes().size() > 1)
                            divisor *= n.getChildNodes().size();
                    result.add(new SearchResult(t4.getBlock(), buf.size() + 1, divisor));
                }

                Location t5 = temp.clone().add(0, 0, 1);
                if(isWire(t5.getBlock())) network.addNode(node, new Node<>(t5));
                else if (isMachine(t5.getBlock())) {
                    List<Node<Location>> buf = node.getAllParents();
                    int divisor = 1;
                    for(Node<Location> n : buf)
                        if(n.getChildNodes().size() > 1)
                            divisor *= n.getChildNodes().size();
                    result.add(new SearchResult(t5.getBlock(), buf.size() + 1, divisor));
                }

                Location t6 = temp.clone().add(0, 0, -1);
                if(isWire(t6.getBlock())) network.addNode(node, new Node<>(t6));
                else if (isMachine(t6.getBlock())) {
                    List<Node<Location>> buf = node.getAllParents();
                    int divisor = 1;
                    for(Node<Location> n : buf)
                        if(n.getChildNodes().size() > 1)
                            divisor *= n.getChildNodes().size();
                    result.add(new SearchResult(t6.getBlock(), buf.size() + 1, divisor));
                }
            }
            if(network.getLeafNodes().containsAll(tem))
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
        public int numWire;
        public int divisorOfElectricity;

        public SearchResult(Block machineBlock, int numWire, int doe) {
            this.machineBlock = machineBlock;
            this.numWire = numWire;
            this.divisorOfElectricity = doe;
        }
    }
}
