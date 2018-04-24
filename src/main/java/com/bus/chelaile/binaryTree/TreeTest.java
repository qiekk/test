package com.bus.chelaile.binaryTree;

import java.util.*;

public class TreeTest {

    private static final int K = 2; // 2-d tree
    private final Node tree;

    public TreeTest(List<Integer> names) {
        final List<Node> nodes = new ArrayList<>(names.size());
        for (final Integer name : names) {
            nodes.add(new Node(name));
        }

        tree = buildTree(nodes, 0);

        System.out.println("build tree over !");
    }

    private Node buildTree(List<Node> nodes, int deepth) {
        if (nodes.isEmpty()) {
            return null;
        }

        int index = nodes.size() / 2;
        Node root = nodes.get(index);
        root.left = buildTree(nodes.subList(0, index), deepth + 1);
        root.right = buildTree(nodes.subList(index + 1, nodes.size()), deepth + 1);

        return root;
    }

    //    @Override
    //    public String toString(){
    //        String str = "";
    //        
    //        if()
    //        
    //        Node leftNode = tree.getLeft();
    //        Node rightNode = tree.getRight();
    //       
    //    }

    public static void main(String[] args) {
        List<Integer> names = new ArrayList<>();
        names.add(1);
        names.add(2);
        names.add(3);
        names.add(4);
        names.add(5);

        TreeTest treeTest = new TreeTest(names);
    }
}
