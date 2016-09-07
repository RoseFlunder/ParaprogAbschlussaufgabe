package de.hsb.smaevers.para.nodes;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class NodeImpl extends NodeAbstract {
	
	private AtomicInteger responseCounter = new AtomicInteger(0);
	private Node wakeUpNeighbour;
	private Object data;

	public NodeImpl(String name, boolean initiator, CountDownLatch startLatch) {
		super(name, initiator, startLatch);
		
		if (initiator){
			start();
		}
	}

	@Override
	public void hello(Node neighbour) {
		this.neighbours.add(neighbour);
	}

	@Override
	public synchronized void wakeup(Node neighbour) {
		responseCounter.incrementAndGet();
		if (wakeUpNeighbour == null && !initiator){
			this.wakeUpNeighbour = neighbour;
			System.out.println(this + " received inital wakeup from " + neighbour);
			start();
		} else {
			System.out.println(this + " received another wakeup from " + neighbour);
			notifyAll();
		}
	}

	@Override
	public synchronized void echo(Node neighbour, Object data) {
		if (this.data == null){
			this.data = data;
		} else {
			this.data = this.data + " ," +  data;
		}
		
		responseCounter.incrementAndGet();
		System.out.println(this + " received echo from " + neighbour);
		notifyAll();
	}

	@Override
	public void setupNeighbours(Node... neighbours) {
		if (neighbours != null){
			for (Node node : neighbours) {
				node.hello(this);
				this.neighbours.add(node);
			}
		}
		
		startLatch.countDown();
	}
	
	@Override
	public void run() {
		try {
			startLatch.await();
			
			for (Node node : neighbours) {
				if (node != wakeUpNeighbour)
					node.wakeup(this);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		synchronized (this) {
			while (responseCounter.get() < neighbours.size()){
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (!initiator)
			wakeUpNeighbour.echo(this, wakeUpNeighbour + "-" + this + (data != null ? ", " + data : ""));
		else
			System.out.println("Fertig \n" + data);
	}

}
