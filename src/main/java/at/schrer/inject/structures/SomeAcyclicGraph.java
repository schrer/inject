package at.schrer.inject.structures;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A custom acyclic graph implementation.
 *
 * @param <T> the type of objects to store in the graph.
 */
public class SomeAcyclicGraph<T> {

    private final Set<Node<T>> nodes;

    public SomeAcyclicGraph() {
        this.nodes = new HashSet<>();
    }

    public Node<T> addNode(T value){
        return this.addNode(value, Set.of());
    }

    public Node<T> addNode(T value, Collection<T> outbounds){
        if (value == null) {
            return null;
        }

        Node<T> existing = this.getNode(value);
        if (existing != null) {
            return existing;
        }

        Set<Node<T>> outboundNodes = new HashSet<>();
        for (T elem : outbounds) {
            Node<T> outboundNode = this.getNode(elem);
            if (outboundNode == null) {
                throw new NoSuchElementException("No existing node found containing outbound element: " + elem);
            }
            outboundNodes.add(outboundNode);
        }

        Node<T> newNode = new Node<>(value, outboundNodes);
        boolean success = this.nodes.add(newNode);
        if (!success) {
            // TODO throw error?
        }
        return newNode;
    }

    /**
     * Finds and returns the node that is in the graph.
     * @param value the object to look for in the graph.
     * @return the node containing this object, or null if no such node exists in the graph
     */
    public Node<T> getNode(T value){
        if (value == null) {
            return null;
        }
        for (Node<T> node : this.nodes) {
            if (value.equals(node.getValue())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Finds a node containing a given value and returns the nodes outbound nodes.
     *
     * @param value the value to look for in a node.
     * @return the outbounds of the node containing this value
     * @throws NullPointerException if no node containing the given value is found
     */
    public Set<T> getOutbounds(T value) {
        return this.getNode(value)
                .getOutbounds().stream()
                .map(Node::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * Checks if a node with the given value already exists in the graph.
     *
     * @param value the object to look for inside this graph
     * @return true if value is not null and is equal to an element in one of the nodes.
     */
    public boolean hasNode(T value){
        return this.getNode(value) != null;
    }

    /**
     * Removes all nodes in the graph.
     */
    public void clear(){
        this.nodes.clear();
    }

    public int size(){
        return nodes.size();
    }

    /**
     * Find a value contained in the node that matches
     * @param predicate a predicate to filter by
     * @return an Optional containing a graph element matching the predicate, an empty Optional if no matching element is in the graph
     */
    public Optional<T> find(Predicate<T> predicate){
        return nodes.stream()
                .map(Node::getValue)
                .filter(predicate)
                .findFirst();
    }

    public static class Node<K> {
        private final K value;
        private final Set<Node<K>> outbounds;

        public Node(K value, Collection<Node<K>> outbound) {
            this.value = value;
            this.outbounds = outbound != null ? Set.copyOf(outbound) : Set.of();
        }

        public K getValue(){
            return this.value;
        }

        public boolean hasOutboundNodes(){
            return this.outbounds.isEmpty();
        }

        public Set<Node<K>> getOutbounds() {
            return outbounds;
        }
    }
}
