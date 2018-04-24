package com.bus.chelaile.binaryTree;

public class Node {

    Node left;
    Node right;
    int name;
    
    public Node(int name){
        this.name = name;
    }
    
    public Node getLeft() {
        return left;
    }
    public void setLeft(Node left) {
        this.left = left;
    }
    public Node getRight() {
        return right;
    }
    public void setRight(Node right) {
        this.right = right;
    }
    public int getName() {
        return name;
    }
    public void setName(int name) {
        this.name = name;
    }
    
    
}
