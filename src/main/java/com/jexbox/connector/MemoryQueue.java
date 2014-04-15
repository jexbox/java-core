package com.jexbox.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

public class MemoryQueue extends CAbstractQueue{
    private static Logger _logger = Logger.getLogger(MemoryQueue.class.getName());
	
    private LinkedList<JsonObject> _queue = new LinkedList<JsonObject>();
    
	public MemoryQueue(){
	}
	
	protected synchronized Class<JsonObject> getPersistentClass() {
		return JsonObject.class;
	}
	
	public synchronized int size() {
		return _queue.size();
	}

	public synchronized boolean isEmpty() {
		return _queue.isEmpty();
	}

	public synchronized boolean contains(Object o) {
		return _queue.contains(o);
	}

	public synchronized List<JsonObject> all(){
		return new ArrayList<JsonObject>(_queue);
	}
	
	public synchronized Iterator<JsonObject> iterator() {
		return (Iterator<JsonObject>)all().iterator();
	}

	public synchronized Object[] toArray() {
		return all().toArray();
	}

	public synchronized <JsonObject> JsonObject[] toArray(JsonObject[] a) {
		return all().toArray(a);
	}

	public synchronized boolean remove(Object o) {
		if(o == null) throw new NullPointerException("The SQL queue do not support null JsonObjects");
		if(!(o instanceof JsonObject)) throw new IllegalArgumentException("The removed object is not instance of JsonObject");
		JsonObject JsonObject = (JsonObject) o;
		return _queue.remove(JsonObject);
	}

	public synchronized boolean containsAll(Collection<?> c) {
		return _queue.containsAll(c);
	}

	public synchronized boolean addAll(Collection<? extends JsonObject> c) {
		boolean res = _queue.addAll(c);
		notifyAll();
		return res;
	}

	public synchronized boolean removeAll(Collection<?> c) {
		return _queue.removeAll(c);
	}

	public synchronized boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not supported by Memory queue");
	}

	public synchronized void clear() {
		throw new UnsupportedOperationException("Not supported by Memory queue");
	}

	public synchronized  boolean add(JsonObject e) {
		return offer(e);
	}

	public synchronized boolean offer(JsonObject JsonObject) {
		if(JsonObject == null) throw new NullPointerException("The Memory queue do not support null JsonObjects");
		boolean res = _queue.offer(JsonObject);
		notifyAll();
		return res;
	}

	public synchronized JsonObject remove() {
		JsonObject JsonObject = poll();
		if(JsonObject == null) throw new NoSuchElementException("The memory queue is empty or there is issue with database");
		return JsonObject;
	}

	public synchronized JsonObject poll() {
		return _queue.poll();
	}

	public synchronized JsonObject element() {
		JsonObject JsonObject = peek();
		if(JsonObject == null) throw new NoSuchElementException("The memory queue is empty or there is issue with database");
		return JsonObject;
	}

	public synchronized JsonObject peek() {
		return _queue.peek();
	}
	
	public synchronized void waitWorker(){
		try {
			wait();
		} catch (InterruptedException ex) {
			_logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public synchronized void notifyWorker() {
		notifyAll();
	}
	
}
