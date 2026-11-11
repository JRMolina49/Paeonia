import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Stores non-null items in FIFO order using private singly linked nodes.
 * @param <T> the stored item type
 * @author Conrad
 */
public class LinkedQueue<T>
{
    private Node<T> front;
    private Node<T> back;
    private int count;

    /** Creates an empty queue. */
    public LinkedQueue()
    {
        front = null;
        back = null;
        count = 0;
    }

    /**
     * Adds an item at the back in constant time.
     * @param item the item to add
     * @throws IllegalArgumentException if the item is null
     */
    public void enqueue(T item)
    {
        if (item == null)
        {
            throw new IllegalArgumentException("Queue items cannot be null");
        }
        Node<T> node = new Node<>(item);
        if (isEmpty())
        {
            front = node;
        }
        else
        {
            back.next = node;
        }
        back = node;
        count++;
    }

    /**
     * Removes the oldest item in constant time.
     * @return the removed front item
     * @throws NoSuchElementException if the queue is empty
     */
    public T dequeue()
    {
        T value = peek();
        front = front.next;
        count--;
        if (isEmpty())
        {
            back = null;
        }
        return value;
    }

    /**
     * Inspects the oldest item without removing it.
     * @return the front item
     * @throws NoSuchElementException if the queue is empty
     */
    public T peek()
    {
        if (isEmpty())
        {
            throw new NoSuchElementException("Queue is empty");
        }
        return front.value;
    }

    /**
     * Reports whether any items are stored.
     * @return true if the queue has no items
     */
    public boolean isEmpty()
    {
        return count == 0;
    }

    /**
     * Reports the number of stored items.
     * @return the number of items
     */
    public int size()
    {
        return count;
    }

    /**
     * Copies the items in FIFO order without modifying the queue.
     * @return an independent list of the items
     */
    public List<T> toList()
    {
        List<T> items = new ArrayList<>(count);
        Node<T> current = front;
        while (current != null)
        {
            items.add(current.value);
            current = current.next;
        }
        return items;
    }

    private static final class Node<T>
    {
        private final T value;
        private Node<T> next;

        private Node(T value)
        {
            this.value = value;
        }
    }
}
