package wordle;

import java.util.*;


//http://stevehanov.ca/blog/index.php?id=114
//This will help us for when we are making a search bar
/**
 * This Trie data structure was written for a previous project
 * but allows fast prefix lookup which will be useful for hints
 * it also has fastest lookup times (faster than hashset)
 */

/**
 * A tree of the keys to get to specified objects based on their keys
 * This is also iterable, so the whole thing will be looped over
 * using a breadth-first search to find all values in a foreach loop
 *
 * Note: Keys must be Strings for this implementation
 *
 * @author paganinik
 *
 * @param <E> The type of the objects that the keys reference
 */
public class Trie<E> implements Iterable<E> {       //TODO: Possible Collection implementation
    public static final char CHILD_START_IND = ' ';
    public static final char CHILD_END_IND = '~';

    private final Node<E> root;
    private int size = 0;

    /**
     * Initializes trie with empty root
     */
    public Trie() {
        this(new Node<>());
    }

    /**
     * Creates a trie rooted at an already created trie node
     * @param root The existing node
     */
    public Trie(Node<E> root) {
        this.root = root;
    }

    /**
     * Number of elements in the Trie
     * @return Number of elements in the Trie
     */
    public int size() {
        return size;
    }

    /**
     * If trie is empty
     * @return True if empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clears the trie by removing all nodes connected to root
     */
    public void clear() {
        for (int i = 0; i < (int)CHILD_END_IND - (int)CHILD_START_IND + 1; ++i) {
            root.children[i] = null;
        }
    }

    /**
     * Class for iterating the trie through a breadth-first search
     */
    public class TrieIterator implements Iterator<E> {
        private final LinkedList<Node<E>> q;

        /**
         * Initializes the queue of nodes
         */
        public TrieIterator() {
            q = new LinkedList<>();
            q.offer(root);
        }

        /**
         * If there is another value not iterated over
         * @return True if Trie has next, false otherwise
         */
        @Override
        public boolean hasNext() {
            return !q.isEmpty();
        }

        /**
         * Continues the breadth first search until finds another value
         * @return The next element
         */
        @Override
        public E next() {
            while (!q.isEmpty()) {
                Node<E> top = q.poll();
                for (Node<E> node: top.children) {
                    if (node != null) {
                        q.offer(node);
                    }
                }
//                System.out.println(top);
//                System.out.println(top.value);
//                System.out.println();
                if (top.value != null) {
                    return top.value;
                }
            }
            return null;
        }
    }

    /**
     * Gets the iterator for the Trie
     * @return The iterator for the Trie
     */
    @Override
    public Iterator<E> iterator() {
        return new TrieIterator();
    }

    /**
     * Class for a Trie Node
     * @param <E> The type of its value
     */
    private static class Node<E> {
        private final Node<E>[] children;
        private E value;

        /**
         * Initializes node
         * @param value The value of the node
         */
        public Node(E value) {
            this.value = value;
            this.children = new Node[(int)Trie.CHILD_END_IND - (int)Trie.CHILD_START_IND + 1];
        }

        /**
         * Initializes a node that has no value
         */
        public Node() {
            this(null);
        }

        /**
         * Adds a child based on a character's index
         * @param childChar The child character
         * @param childObject The value of the node
         */
        public void addChild(char childChar, E childObject) {
            this.children[(int)childChar - (int)Trie.CHILD_START_IND] = new Node<>(childObject);
        }

        /**
         * Gets the child of a node based on a child character
         * @param childChar The child character
         * @return The child node
         */
        private Node<E> getChild(char childChar) {
            return this.children[(int)childChar - (int)Trie.CHILD_START_IND];
        }
    }

    /**
     * Gets a node based on a key
     * @param key The key to search for
     * @param createNewIfNull If new nodes should be created in the case of a null child
     *                        being found in traversal problem
     * @return The node for this key (null if node doesn't exist)
     */
    private Node<E> getKeyNode(String key, boolean createNewIfNull) {
        Node<E> curr = root;
        for (char c: key.toCharArray()) {
            if (curr.getChild(c) == null) {
                if (createNewIfNull) {
                    curr.addChild(c, null);
                } else {
                    return null;
                }
            }
            curr = curr.getChild(c);
        }
        return curr;
    }

    /**
     * Adds a key to the trie
     * @param key The key
     * @param value The value to store at the key's node
     * @return True if key node was empty, false otherwise
     */
    public boolean addKey(String key, E value) {
        Node<E> keyNode = getKeyNode(key, true);
        if (keyNode != null && keyNode.value == null) {
            keyNode.value = value;
            ++size;
            return true;
        } else {
            return false; // if key exists
        }
    }

    /**
     * Gets value of node at a given key's node
     * @param key The key
     * @return The value of the object at the key node
     */
    public E getValue(String key) {
        Node<E> keyNode = getKeyNode(key, false);
        if (keyNode != null) {
            return keyNode.value;
        } else {
            return null;
        }
    }

    /**
     * Gets a sub-trie that can be used for finding objects with same prefix
     * @param prefix The prefix to traverse down to and create the sub-trie from
     * @return The sub-trie for the given prefix
     */
    public Trie<E> getTrieFromKey(String prefix) {
        Node<E> keyNode = getKeyNode(prefix, false);
        if (keyNode != null) {
            return new Trie<>(keyNode);
        } else {
            return null;
        }
    }

    /**
     * Finds the "max" nearest objects or all remaining objects if less than max from a given prefix
     * @param prefix The prefix to start from
     * @param max The maximum number of values to find
     * @return The nearest values in the Trie
     */
    public ArrayList<E> nearestObjects(String prefix, int max) {
        ArrayList<E> nearestObjects = new ArrayList<>();
        Trie<E> trieWithPrefix = getTrieFromKey(prefix);
        if (trieWithPrefix == null) {
            return null;
        }
        Iterator<E> it = trieWithPrefix.iterator();
        while (it.hasNext() && nearestObjects.size() < max) {
            nearestObjects.add(it.next());
        }
        return nearestObjects;
    }

    //TODO: We should remove this
    public static void main(String[] args) {
        String key = "dog";
        String key2 = "doggo";
        String key3 = "cat";
        String key4 = "c";
        Trie<String> trie = new Trie<String>();
        trie.addKey(key, key);
        trie.addKey(key2, key2);
        trie.addKey(key3, key3);
        trie.addKey(key4, key4);
        Trie<String> keyTrie = trie.getTrieFromKey("do");
//        System.out.println(trie.getValue("dog"));
        for (String s: trie) {
            System.out.println(s);
        }
        System.out.println("\nClosest words to stem \"do\":");
        for (String s: keyTrie) {
            System.out.println(s);
        }
        System.out.println(trie.nearestObjects("", 8));
    }
}
