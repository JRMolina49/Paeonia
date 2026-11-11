import java.util.List;
import java.util.NoSuchElementException;

/**
 * Standalone checks for LinkedQueue.
 * @author Conrad
 */
public final class LinkedQueueTest
{
    private LinkedQueueTest()
    {
        // Utility class.
    }

    /**
     * Runs the checks.
     * @param args unused
     * @throws Exception if a check fails
     */
    public static void main(String[] args) throws Exception
    {
        LinkedQueue<String> queue = new LinkedQueue<>();
        TestSupport.check(queue.isEmpty() && queue.size() == 0, "New queue");
        TestSupport.check(queue.toList().isEmpty(), "Empty list conversion");
        TestSupport.expect(NoSuchElementException.class, queue::dequeue);
        TestSupport.expect(NoSuchElementException.class, queue::peek);
        TestSupport.expect(IllegalArgumentException.class, () -> queue.enqueue(null));
        TestSupport.check(queue.isEmpty() && queue.size() == 0, "Errors preserve empty queue");
        queue.enqueue("first");
        queue.enqueue("second");
        queue.enqueue("second");
        TestSupport.check(!queue.isEmpty() && queue.size() == 3, "Enqueue and duplicates");
        TestSupport.check("first".equals(queue.peek()) && queue.size() == 3, "Peek does not remove");
        TestSupport.expect(IllegalArgumentException.class, () -> queue.enqueue(null));
        List<String> copy = queue.toList();
        TestSupport.check(copy.equals(List.of("first", "second", "second")), "FIFO list");
        copy.clear();
        TestSupport.check(queue.size() == 3 && "first".equals(queue.peek()), "List is independent");
        TestSupport.check("first".equals(queue.dequeue()), "First out");
        TestSupport.check(queue.size() == 2 && "second".equals(queue.peek()), "New front");
        TestSupport.check("second".equals(queue.dequeue()), "Second out");
        TestSupport.check("second".equals(queue.dequeue()), "Last out");
        TestSupport.check(queue.isEmpty() && queue.size() == 0, "Drain to empty");
        TestSupport.expect(NoSuchElementException.class, queue::dequeue);
        TestSupport.expect(NoSuchElementException.class, queue::peek);
        queue.enqueue("reused");
        TestSupport.check("reused".equals(queue.peek()), "Reuse after draining");
        TestSupport.check("reused".equals(queue.dequeue()) && queue.isEmpty(), "Single item round trip");
        LinkedQueue<Integer> numbers = new LinkedQueue<>();
        for (int i = 0; i < 1000; i++)
        {
            numbers.enqueue(i);
        }
        for (int i = 0; i < 1000; i++)
        {
            TestSupport.check(numbers.dequeue() == i, "FIFO over many items");
        }
        TestSupport.check(numbers.isEmpty(), "Drain many items");
        numbers.enqueue(1);
        numbers.enqueue(2);
        TestSupport.check(numbers.dequeue() == 1, "Interleaved first removal");
        numbers.enqueue(3);
        TestSupport.check(numbers.toList().equals(List.of(2, 3)), "Enqueue after partial removal");
        List<Integer> before = numbers.toList();
        TestSupport.check(numbers.dequeue() == 2 && numbers.dequeue() == 3, "Interleaved FIFO");
        TestSupport.check(before.equals(List.of(2, 3)), "Queue changes cannot change old list");
        System.out.println("LinkedQueueTest passed");
    }
}
