package com.industrialworld.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class HashTree<T> {
    private Node<T> rootNode;
    private HashSet<Node<T>> nodes = new HashSet<>();
    private HashSet<Node<T>> leafNodes = new HashSet<>();

    public HashTree(Node<T> root) {
        this.rootNode = root;
        nodes.add(root);
        leafNodes.add(root);
    }

    public void addNode(Node<T> parentNode, Node<T> newNode) {
        if (nodes.contains(newNode) || !(nodes.contains(parentNode)))
            return;
        parentNode.addChildNode(newNode);
        newNode.setParentNode(parentNode);
        nodes.add(newNode);
        leafNodes.add(newNode);
        leafNodes.remove(parentNode);
    }

    public void removeNode(Node<T> node) {
        if (!nodes.contains(node))
            return;
        nodes.remove(node);
        leafNodes.remove(node);
        node.remove();
    }

    public HashSet<Node<T>> getAllNodes() {
        return nodes;
    }

    public HashSet<Node<T>> getLeafNodes() {
        return leafNodes;
    }

    public Node<T> getRootNode() {
        return rootNode;
    }

    public static class Node<E> {
        private E value;
        private Node<E> parentNode = null;
        private LinkedList<Node<E>> childNodes = new LinkedList<>();

        public Node(E value) {
            this.value = value;
        }

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
        }

        public Node<E> getParentNode() {
            return parentNode;
        }

        private void setParentNode(Node<E> parentNode) {
            this.parentNode = parentNode;
        }

        public LinkedList<Node<E>> getChildNodes() {
            return childNodes;
        }

        private void addChildNode(Node<E> node) {
            if (!childNodes.contains(node))
                childNodes.add(node);
        }

        private void removeChildNode(Node<E> node) {
            childNodes.remove(node);
        }

        private void remove() {
            parentNode.removeChildNode(this);
            parentNode = null;
            for (Node<E> node : childNodes)
                node.remove();
        }

        public List<Node<E>> getAllParents() {
            ArrayList<Node<E>> list = new ArrayList<>();
            Node<E> nowNode = this;
            while ((nowNode = nowNode.getParentNode()) != null)
                list.add(nowNode);
            return list;
        }
    }
}
