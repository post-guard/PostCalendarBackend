package top.rrricardo.postcalendarbackend.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.rrricardo.postcalendarbackend.utils.generic.Heap;

import java.util.PriorityQueue;
import java.util.Random;

public class HeapTest {

    @Test
    void testHeap1() {
        var heap = new Heap<Integer>(20);

        heap.add(1);
        heap.add(4);
        heap.add(5);
        heap.add(-1);

        Assertions.assertEquals(-1, heap.peek());

        Assertions.assertEquals(-1, heap.poll());
        Assertions.assertEquals(1, heap.poll());
        Assertions.assertEquals(4, heap.poll());
        Assertions.assertEquals(5, heap.poll());
    }

    @Test
    void testHeap2() {
        var heap = new Heap<Integer>();
        var queue = new PriorityQueue<Integer>();
        var random = new Random();

        for (var i = 0; i < 10; i++) {
            var input = random.nextInt();

            heap.add(input);
            queue.add(input);
            Assertions.assertEquals(queue.size(), heap.getSize());
        }

        for (var i = 0; i < 10; i++) {
            Assertions.assertEquals(queue.poll(), heap.poll());
        }
    }
}
