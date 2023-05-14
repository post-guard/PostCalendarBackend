package top.rrricardo.postcalendarbackend.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;

import java.util.Hashtable;
import java.util.Random;

public class CustomHashTableTest {

    @Test
    void testHashTablePut1() {
        var table = new CustomHashTable<Integer, Integer>();
        var expect = new Hashtable<Integer, Integer>();
        var random = new Random();

        for (var i = 0; i < 1000; i++) {
            var key = random.nextInt();
            var value = random.nextInt();

            table.put(key, value);
            expect.put(key, value);
        }

        Assertions.assertEquals(expect.size(), table.getSize());
        for (var key : expect.keySet()) {
            Assertions.assertEquals(expect.get(key), table.get(key));
        }
    }

    @Test
    void testHashTablePut2() {
        var table = new CustomHashTable<Integer, Integer>(200);

        table.put(2, 3);
        table.put(3, 4);

        Assertions.assertEquals(3, table.put(2, 5));
    }

    @Test
    void testHashTableIsEmpty() {
        var table = new CustomHashTable<Integer, Integer>();

        Assertions.assertTrue(table.isEmpty());

        table.put(2, 3);

        Assertions.assertFalse(table.isEmpty());
    }

    @Test
    void testHashTableRemove() {
        var table = new CustomHashTable<Integer, Integer>();
        var except = new Hashtable<Integer, Integer>();
        var random = new Random();

        for (var i = 0; i < 1000; i++) {
            var key = random.nextInt();
            var value = random.nextInt();

            table.put(key, value);
            except.put(key, value);
        }

        Assertions.assertEquals(except.size(), table.getSize());
        for (var key : except.keySet()) {
            Assertions.assertEquals(except.get(key), table.get(key));
            Assertions.assertEquals(except.get(key), table.remove(key));
        }

        Assertions.assertTrue(table.isEmpty());
    }

    @Test
    void testHashTableClear() {
        var table = new CustomHashTable<Integer, Integer>();
        var random = new Random();

        for (var i = 0; i < 1000; i++) {
            table.put(random.nextInt(), random.nextInt());
        }

        Assertions.assertEquals(1000, table.getSize());

        table.clear();

        Assertions.assertEquals(0, table.getSize());
        Assertions.assertTrue(table.isEmpty());

        var expect = new Hashtable<Integer, Integer>();

        for (var i = 0; i < 1000; i++) {
            var key = random.nextInt();
            var value = random.nextInt();

            table.put(key, value);
            expect.put(key, value);
        }

        Assertions.assertEquals(expect.size(), table.getSize());
        for (var key : expect.keySet()) {
            Assertions.assertEquals(expect.get(key), table.get(key));
        }
    }

    @Test
    void testHashTableIterator() {
        var table = new CustomHashTable<Integer, Integer>();
        var expect = new Hashtable<Integer, Integer>();
        var random = new Random();

        for (var i = 0; i < 1000; i++) {
            var key = random.nextInt();
            var value = random.nextInt();

            table.put(key, value);
            expect.put(key, value);
        }

        Assertions.assertEquals(expect.size(), table.getSize());
        var counter = 0;
        for (var pair : table) {
            counter++;
            Assertions.assertEquals(expect.get(pair.getKey()), pair.getValue());
        }

        Assertions.assertEquals(expect.size(), counter);
    }
}
