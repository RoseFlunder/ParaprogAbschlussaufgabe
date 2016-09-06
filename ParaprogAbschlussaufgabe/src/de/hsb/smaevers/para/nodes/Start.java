package de.hsb.smaevers.para.nodes;

import java.util.concurrent.CountDownLatch;

public class Start {

	public static void main(String[] args) {
		CountDownLatch latch = new CountDownLatch(4);
		NodeImpl initator = new NodeImpl("init", true, latch);
		NodeImpl node1 = new NodeImpl("node1", false, latch);
		NodeImpl node2 = new NodeImpl("node2", false, latch);
		NodeImpl node3 = new NodeImpl("node3", false, latch);
		NodeImpl node4 = new NodeImpl("node4", false, latch);
		
		initator.setupNeighbours(node1, node4);
		node1.setupNeighbours(initator, node2);
		node2.setupNeighbours(node1);
		node3.setupNeighbours(node1, node2);
		node4.setupNeighbours(initator);
		
	}

}
